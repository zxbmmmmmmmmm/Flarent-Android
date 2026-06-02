package com.bettafish.flarent.ui.pages.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.bettafish.flarent.data.NotificationsRepository
import com.bettafish.flarent.models.Notification
import com.bettafish.flarent.utils.LocalUpdatedValueStore.Companion.NotificationIsReadStore
import com.bettafish.flarent.utils.SuspendCommand
import com.bettafish.flarent.utils.SuspendCommand1
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NotificationsViewModel(val repository: NotificationsRepository) : ViewModel() {
    companion object {
        private const val LOAD_COUNT = 20
    }

    val markAllAsReadCommand = SuspendCommand1(::markAllAsRead, viewModelScope)

    val notifications: Flow<PagingData<Notification>> = Pager(
        config = PagingConfig(pageSize = LOAD_COUNT, enablePlaceholders = false),
        pagingSourceFactory = {
            NotificationsDataSource(
                repository,
                LOAD_COUNT,
            )
        }
    ).flow.cachedIn(viewModelScope)

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                repository.markNotificationAsRead(notificationId)
                NotificationIsReadStore.update(notificationId, true)
            } catch (e: Exception) {
            }
        }
    }

    suspend fun markAllAsRead(items: List<Notification>) {
        repository.markAllNotificationsAsRead()
        items.forEach { notification ->
            NotificationIsReadStore.update(notification.id, true)
        }
    }
}