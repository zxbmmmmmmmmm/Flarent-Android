package com.bettafish.flarent.viewModels

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.utils.HtmlConverter
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import kotlin.math.max
import kotlin.math.min

class DiscussionDetailPostsDataSource(
    val postsRepository: PostsRepository,
    val posts: List<Post>) : PagingSource<Int, Post>() {

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(state.config.pageSize) ?: page.nextKey?.minus(state.config.pageSize)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            val offset = params.key ?: 0
            val loadSize = params.loadSize

            // Calculate the actual valid range of indices in the posts list
            // Allow 'offset' to be negative (to support non-aligned previous pages)
            // but clamp the read window to [0, posts.size]
            val actualStart = max(0, offset)
            val actualEnd = min(offset + loadSize, posts.size)

            if (actualStart >= actualEnd) {
                return LoadResult.Page(emptyList(), null, null)
            }

            // Check if we already have content for this range
            val subList = posts.subList(actualStart, actualEnd)
            val hasContent = subList.all { it.content != null || it.contentHtml != null }

            val items = if (hasContent) {
                 subList
            } else {
                val postIds = subList.map(Post::id)
                postsRepository.fetchPosts(postIds)
            }

            items.forEach { item ->
                if (item.contentHtml != null) {
                    item.contentMarkdown = HtmlConverter.convert(item.contentHtml)
                }
            }

            // Next Key: Only if we haven't reached the end of the list
            val nextKey = if (actualEnd >= posts.size) null else offset + loadSize

            // Prev Key: Only if we haven't reached the start of the list
            // If actualStart <= 0, we have included the first item, so no more previous items.
            // We return 'offset - loadSize' even if it is negative, to maintain grid alignment without overlap.
            val prevKey = if (actualStart <= 0) null else offset - loadSize

            LoadResult.Page(
                data = items,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}