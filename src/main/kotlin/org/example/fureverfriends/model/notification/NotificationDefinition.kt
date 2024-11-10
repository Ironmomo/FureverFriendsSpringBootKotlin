package org.example.fureverfriends.model.notification

import org.example.fureverfriends.model.notification.NotificationType.FollowRequestNotification
import org.example.fureverfriends.model.notification.NotificationType.LikeNotification

sealed class NotificationDefinition<T : NotificationPayload> {
    abstract val payload: T
    abstract val type: NotificationType

    data class LikeNotificationDefinitionImpl(
        override val payload: LikeNotificationPayload,
    ) : NotificationDefinition<LikeNotificationPayload>() {
        override val type: NotificationType = LikeNotification
    }

    data class FollowRequestNotificationDefinitionImpl(
        override val payload: FollowRequestNotificationPayload,
    ) : NotificationDefinition<FollowRequestNotificationPayload>() {
        override val type: NotificationType = FollowRequestNotification
    }
}

data class LikeNotificationPayload(
    val userName: String,
    val postTitle: String
): NotificationPayload

data class FollowRequestNotificationPayload(
    val userName: String
): NotificationPayload

interface NotificationPayload