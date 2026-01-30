package com.bettafish.flarent.viewModels

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.models.Post
import kotlin.math.min

private const val PAGE_SIZE = 20;
class PostsDataSource(
    val postsRepository: PostsRepository,
    var posts: List<Post>,
    val startingPosition: Int) : PagingSource<Int, Post>(){

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(PAGE_SIZE) ?: page.nextKey?.minus(PAGE_SIZE)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            val offset = params.key ?: startingPosition
            val items = if(params.key == null){
                // 首次加载，直接从posts中获取
                posts.subList(startingPosition, min(startingPosition + PAGE_SIZE, posts.size - 1))
            }
            else{
                val postIds = posts.subList(offset, offset + PAGE_SIZE).map(Post::id)
                postsRepository.fetchPostsById(postIds);
            }
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