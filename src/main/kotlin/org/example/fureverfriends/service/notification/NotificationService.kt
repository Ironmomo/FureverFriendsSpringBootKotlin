package org.example.fureverfriends.service.notification

import org.example.fureverfriends.dto.notification.NotificationDTO
import org.example.fureverfriends.model.notification.NotificationDefinition
import org.example.fureverfriends.model.notification.NotificationPayload

interface NotificationService {
    fun <T : NotificationPayload> createNotification(notification: NotificationDefinition<T>, userName: String)

    fun loadNotificationByUser(userName: String): List<NotificationDTO>
}