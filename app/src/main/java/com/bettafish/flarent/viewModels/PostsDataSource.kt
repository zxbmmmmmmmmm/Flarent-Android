package com.bettafish.flarent.viewModels

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.models.Post

private const val PAGE_SIZE = 20;
class PostsDataSource(
    val postsRepository: PostsRepository,
    var posts:List<Post>,
    val startingPosition: Int) : PagingSource<Int, Post>(){

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(PAGE_SIZE) ?: page.nextKey?.minus(PAGE_SIZE)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            val offset = params.key ?: 0
            val items = postsRepository.fetchPostsById(listOf(1))
            val nextKey = if (items.size < PAGE_SIZE) null else offset + PAGE_SIZE
            val prevKey = if (offset == 0) null else maxOf(0, offset - PAGE_SIZE)
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