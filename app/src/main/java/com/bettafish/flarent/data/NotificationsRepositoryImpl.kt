package com.bettafish.flarent.data

import com.bettafish.flarent.models.Notification
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.request.NotificationRequest
import com.bettafish.flarent.network.FlarumService

class NotificationsRepositoryImpl(private val service: FlarumService): NotificationsRepository {
    override suspend fun fetchNotifications(request: NotificationRequest): List<Notification>  =
        service.getNotifications(request.toQueryMap())

    override suspend fun markNotificationAsRead(notificationId: String) {
        patchNotification(notificationId) {
            it.isRead = true
        }
    }

    override suspend fun markAllNotificationsAsRead() {
        service.markAllNotificationsAsRead()
    }

    suspend fun patchNotification(notificationId: String, block: (Notification) -> Unit): Notification? {
        val post = Notification().apply { id = notificationId }
        block(post)
        return service.patchNotification(notificationId, post)
    }
}