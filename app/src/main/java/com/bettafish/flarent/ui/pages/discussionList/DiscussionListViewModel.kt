package com.bettafish.flarent.ui.pages.discussionList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bettafish.flarent.data.DiscussionsRepository
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.navigation.TagNavArgs
import kotlinx.coroutines.flow.Flow

class DiscussionListViewModel(
    private val repository: DiscussionsRepository,
    val navArgs: TagNavArgs? = null
) : ViewModel() {
    companion object{
        private const val LOAD_COUNT = 20;
    }
    val discussions: Flow<PagingData<Discussion>> = Pager(
        config = PagingConfig(pageSize = LOAD_COUNT, enablePlaceholders = false),
        pagingSourceFactory = {
            DiscussionListDataSource(
                repository,
                LOAD_COUNT,
                navArgs?.slug
            )
        }
    ).flow.cachedIn(viewModelScope)
}