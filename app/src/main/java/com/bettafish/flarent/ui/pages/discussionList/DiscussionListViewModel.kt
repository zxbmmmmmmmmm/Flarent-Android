package com.bettafish.flarent.ui.pages.discussionList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bettafish.flarent.data.DiscussionsRepository
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.navigation.TagNavArgs
import com.bettafish.flarent.models.request.DiscussionListRequest
import kotlinx.coroutines.flow.Flow

class DiscussionListViewModel(
    private val repository: DiscussionsRepository,
    val navArgs: TagNavArgs? = null
) : ViewModel() {
    companion object{
        const val LOAD_COUNT = 20
    }

    private val request = DiscussionListRequest(tag = navArgs?.slug)

    @OptIn(ExperimentalPagingApi::class)
    val discussions: Flow<PagingData<Discussion>> = Pager(
        config = PagingConfig(pageSize = LOAD_COUNT, enablePlaceholders = false),
        remoteMediator = repository.discussionListRemoteMediator(
            request = request,
            pageSize = LOAD_COUNT
        ),
        pagingSourceFactory = {
            repository.discussionListPagingSource(request)
        }
    ).flow.cachedIn(viewModelScope)
}
