package com.bettafish.flarent.ui.pages.like

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bettafish.flarent.data.UsersRepository
import com.bettafish.flarent.models.User
import com.bettafish.flarent.models.request.DiscussionListRequest
import com.bettafish.flarent.models.request.UsersRequest

class LikesDataSource(
    private val repository: UsersRepository,
    private val postId: String,
    private val pageSize: Int
) :
    PagingSource<Int, User>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val offset = params.key ?: 0
            val items =
                repository.fetchUsers(
                    UsersRequest(
                        mapOf("liked" to postId),
                        limit = pageSize, offset = offset
                    )
                )
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

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(pageSize) ?: page.nextKey?.minus(pageSize)
    }

}