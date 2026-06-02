package com.bettafish.flarent.data

import com.bettafish.flarent.models.Notification
import com.bettafish.flarent.models.request.NotificationRequest

interface NotificationsRepository {
    suspend fun fetchNotifications (request: NotificationRequest): List<Notification>

    suspend fun markNotificationAsRead(notificationId: String)
    suspend fun markAllNotificationsAsRead()
}