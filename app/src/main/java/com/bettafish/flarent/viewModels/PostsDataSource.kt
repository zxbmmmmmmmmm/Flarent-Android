package com.bettafish.flarent.viewModels

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.models.Post
import kotlin.math.min

class PostsDataSource(
    val postsRepository: PostsRepository,
    val posts: List<Post>,
    val startingPosition: Int,
    val pageSize : Int) : PagingSource<Int, Post>(){

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(pageSize) ?: page.nextKey?.minus(pageSize)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            val offset = params.key ?: startingPosition
            val items = if(params.key == null){
                // 首次加载，直接从posts中获取
                posts.subList(startingPosition, min(startingPosition + pageSize, posts.size - 1))
            }
            else{
                val postIds = posts.subList(offset, offset + pageSize).map(Post::id)
                postsRepository.fetchPostsById(postIds);
            }
            val nextKey = if (items.size < pageSize) null else offset + pageSize
            val prevKey = if (offset == 0) null else maxOf(0, offset - pageSize)
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