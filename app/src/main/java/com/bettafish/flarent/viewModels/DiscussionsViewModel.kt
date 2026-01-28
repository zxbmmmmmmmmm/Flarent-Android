package com.bettafish.flarent.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.bettafish.flarent.data.DiscussionsRepository
import com.bettafish.flarent.models.Discussion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DiscussionsViewModel(
    private val repository: DiscussionsRepository
) : ViewModel() {
    companion object{
        private const val LOAD_COUNT = 20;
    }
    val discussions: Flow<PagingData<Discussion>> = Pager(
        config = PagingConfig(pageSize = LOAD_COUNT, enablePlaceholders = false),
        pagingSourceFactory = { DiscussionDataSource(repository,
            LOAD_COUNT
        ) }
    ).flow.cachedIn(viewModelScope)
}


class DiscussionDataSource(
    private val repository: DiscussionsRepository,
    private val pageSize: Int
) : PagingSource<Int, Discussion>(){
    override fun getRefreshKey(state: PagingState<Int, Discussion>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(pageSize) ?: page.nextKey?.minus(pageSize)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Discussion> {
        return try {
            val offset = params.key ?: 0
            val items = repository.fetchDiscussions(offset)
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


