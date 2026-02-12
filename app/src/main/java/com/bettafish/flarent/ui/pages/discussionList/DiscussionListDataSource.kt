package com.bettafish.flarent.ui.pages.discussionList

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bettafish.flarent.data.DiscussionsRepository
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.request.DiscussionListRequest

class DiscussionListDataSource(
    private val repository: DiscussionsRepository,
    private val pageSize: Int,
    private val tag: String? = null,
    private val author: String? = null,
    private val sort: String? = null
) : PagingSource<Int, Discussion>(){
    override fun getRefreshKey(state: PagingState<Int, Discussion>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(pageSize) ?: page.nextKey?.minus(pageSize)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Discussion> {
        return try {
            val offset = params.key ?: 0
            val items = repository.fetchDiscussionList(DiscussionListRequest(offset, tag, author, sort))
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