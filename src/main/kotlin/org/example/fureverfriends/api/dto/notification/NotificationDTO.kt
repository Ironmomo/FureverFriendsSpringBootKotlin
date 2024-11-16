package org.example.fureverfriends.api.dto.notification

data class NotificationDTO(
    val payload: String,
    val status: NotificationStatusDTO,
    val type: NotificationTypeDTO
)

enum class NotificationTypeDTO {
    LikeNotification,
    FollowRequestNotification
}

enum class NotificationStatusDTO {
    NEW,
    SEEN,
    RESOLVED
}
