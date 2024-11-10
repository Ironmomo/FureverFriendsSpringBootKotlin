package org.example.fureverfriends.model.notification

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.example.fureverfriends.model.notification.NotificationStatus.NEW
import org.example.fureverfriends.model.user.User

@Entity
@Table(name = "notifications")
data class Notification(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val type: NotificationType,

    @Column
    val payload: String,

    @Column(nullable = false)
    val status: NotificationStatus = NEW,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User
)

enum class NotificationType {
    LikeNotification,
    FollowRequestNotification
}

enum class NotificationStatus {
    NEW,
    SEEN,
    RESOLVED
}