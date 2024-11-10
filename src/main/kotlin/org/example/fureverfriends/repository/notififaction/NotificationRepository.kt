package org.example.fureverfriends.repository.notififaction

import org.example.fureverfriends.model.notification.Notification
import org.example.fureverfriends.model.notification.NotificationStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository: JpaRepository<Notification, Long> {
    fun findNotificationsByUserUsernameAndStatusNot(userName: String, statusNot: NotificationStatus): List<Notification>
}