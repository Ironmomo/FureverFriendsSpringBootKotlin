package org.example.fureverfriends.processing.service.notification

import org.example.fureverfriends.api.dto.notification.NotificationDTO
import org.example.fureverfriends.model.notification.NotificationDefinition
import org.example.fureverfriends.model.notification.NotificationPayload

interface NotificationService {
    fun <T : NotificationPayload> createNotification(notification: NotificationDefinition<T>, userName: String)

    fun loadNotificationByUser(userName: String): List<NotificationDTO>
}