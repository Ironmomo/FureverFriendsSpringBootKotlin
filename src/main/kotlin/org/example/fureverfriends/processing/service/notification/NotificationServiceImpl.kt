package org.example.fureverfriends.processing.service.notification

import com.google.gson.Gson
import org.example.fureverfriends.api.dto.notification.NotificationDTO
import org.example.fureverfriends.api.dto.notification.NotificationStatusDTO
import org.example.fureverfriends.api.dto.notification.NotificationTypeDTO
import org.example.fureverfriends.model.notification.Notification
import org.example.fureverfriends.model.notification.NotificationDefinition
import org.example.fureverfriends.model.notification.NotificationPayload
import org.example.fureverfriends.model.notification.NotificationStatus.RESOLVED
import org.example.fureverfriends.processing.service.user.UserService
import org.example.fureverfriends.repository.notififaction.NotificationRepository
import org.springframework.stereotype.Service

@Service
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository,
    private val userService: UserService,
    private val gson: Gson
) : NotificationService {

    override fun <T : NotificationPayload> createNotification(notification: NotificationDefinition<T>, userName: String) {
        val user = userService.getUserByUsername(userName)
        notificationRepository.save(
            Notification(
                type = notification.type,
                payload = gson.toJson(notification.payload),
                user = user
            )
        )
    }

    override fun loadNotificationByUser(userName: String): List<NotificationDTO> {
        val notifications = notificationRepository.findNotificationsByUserUsernameAndStatusNot(
            userName = userName,
            statusNot = RESOLVED
        )
        return notifications.map { it.toDTO() }
    }

    fun Notification.toDTO() = NotificationDTO(
        payload = this.payload,
        status = NotificationStatusDTO.valueOf(this.status.name),
        type = NotificationTypeDTO.valueOf(this.type.name)
    )
}