package com.bettafish.flarent.data

import com.bettafish.flarent.models.Notification
import com.bettafish.flarent.models.request.NotificationRequest
import com.bettafish.flarent.network.FlarumService

class NotificationsRepositoryImpl(private val service: FlarumService): NotificationsRepository {
    override suspend fun fetchNotifications(request: NotificationRequest): List<Notification>  =
        service.getNotifications(request.toQueryMap())
}