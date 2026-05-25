package com.bettafish.flarent.ui.pages.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bettafish.flarent.data.NotificationsRepository
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.Notification
import com.bettafish.flarent.ui.pages.discussionList.DiscussionListDataSource
import kotlinx.coroutines.flow.Flow

class NotificationsViewModel(val repository: NotificationsRepository): ViewModel() {
    companion object{
        private const val LOAD_COUNT = 20;
    }
    val notifications: Flow<PagingData<Notification>> = Pager(
        config = PagingConfig(pageSize = LOAD_COUNT, enablePlaceholders = false),
        pagingSourceFactory = {
             NotificationsDataSource(
                repository,
                LOAD_COUNT,
            )
        }
    ).flow.cachedIn(viewModelScope)
}