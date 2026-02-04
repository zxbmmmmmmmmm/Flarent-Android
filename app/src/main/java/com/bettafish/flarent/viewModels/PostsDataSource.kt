package com.bettafish.flarent.viewModels

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.utils.HtmlConverter

class PostsDataSource(
    private val repository: PostsRepository,
    private val pageSize: Int,
    private val author: String? = null,
    private val onFirstLoad: ((Post) -> Unit)? = null
) : PagingSource<Int, Post>(){
    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(pageSize) ?: page.nextKey?.minus(pageSize)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            val offset = params.key ?: 0
            val items = repository.fetchPosts(author = author, type = "comment" , offset = offset, limit = pageSize, sort = "-createdAt")

            if (offset == 0 && items.isNotEmpty()) {
                onFirstLoad?.invoke(items.first())
            }

            val nextKey = if (items.size < pageSize) null else offset + pageSize
            val prevKey = if (offset == 0) null else maxOf(0, offset - pageSize)
            items.forEach { item ->
                if (item.contentHtml != null) {
                    item.contentMarkdown = HtmlConverter.convert(item.contentHtml)
                }
            }
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
