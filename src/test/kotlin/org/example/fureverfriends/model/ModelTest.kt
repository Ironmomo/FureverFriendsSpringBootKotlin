package org.example.fureverfriends.model

import com.google.gson.Gson
import org.assertj.core.api.Assertions.assertThat
import org.example.fureverfriends.api.dto.notification.NotificationStatusDTO
import org.example.fureverfriends.api.dto.notification.NotificationTypeDTO
import org.example.fureverfriends.api.dto.post.PostResponseDTO
import org.example.fureverfriends.api.dto.user.UserDTO
import org.example.fureverfriends.model.notification.FollowRequestNotificationPayload
import org.example.fureverfriends.model.notification.LikeNotificationPayload
import org.example.fureverfriends.model.notification.NotificationDefinition.FollowRequestNotificationDefinitionImpl
import org.example.fureverfriends.model.notification.NotificationDefinition.LikeNotificationDefinitionImpl
import org.example.fureverfriends.model.notification.NotificationStatus
import org.example.fureverfriends.model.notification.NotificationType
import org.example.fureverfriends.model.notification.NotificationType.FollowRequestNotification
import org.example.fureverfriends.model.notification.NotificationType.LikeNotification
import org.example.fureverfriends.model.post.Post
import org.example.fureverfriends.model.user.Role.ADMIN
import org.example.fureverfriends.model.user.User
import org.example.fureverfriends.stubs.stubUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import kotlin.test.Test

@SpringBootTest
class ModelTest @Autowired constructor(
    private val gson: Gson
) {

    @Nested
    inner class MapPostTests {
        @Test
        fun `should map Post to DTO`() {
            val createdAt = LocalDateTime.now()
            val user = stubUser()
            val post = Post(
                id = 0,
                title = "some title",
                content = "some content",
                createdAt = createdAt,
                likes = 1,
                user = user
            )

            val mapedPostDTO = post.mapToDTO()

            assertThat(mapedPostDTO).isEqualTo(
                PostResponseDTO(
                    id = 0,
                    title = "some title",
                    content = "some content",
                    createdAt = createdAt,
                    likes = 1,
                    user = UserDTO(user.username)
                )
            )
        }
    }

    @Nested
    inner class MapUserTests {
        @Test
        fun `should map User to DTO`() {
            val user = User(
                username = "fancy user", password = "secret Password", role = ADMIN
            )

            val mapedUserDTO = user.mapToDTO()

            assertThat(mapedUserDTO).isEqualTo(UserDTO(user.username))
        }
    }

    @Nested
    inner class NotificationTests {
        @ParameterizedTest
        @EnumSource(NotificationType::class)
        fun `type dto has all members implemented`(notificationType: NotificationType) {
            val dto = NotificationTypeDTO.valueOf(notificationType.name)
            assertEquals(notificationType.name, dto.name)
        }

        @ParameterizedTest
        @EnumSource(NotificationStatus::class)
        fun `status dto has all members implemented`(notificationStatus: NotificationStatus) {
            val dto = NotificationStatusDTO.valueOf(notificationStatus.name)
            assertEquals(notificationStatus.name, dto.name)
        }

        @Test
        fun `should serialize payload to json`() {
            val likeNotificationPayload = LikeNotificationPayload(
                userName = "someUser",
                postTitle = "someTitle"
            )

            val json = gson.toJson(likeNotificationPayload)

            assertThat(json).isEqualTo("{\"userName\":\"someUser\",\"postTitle\":\"someTitle\"}")
        }

        @Nested
        inner class NotificationDefinitionsTests {
            @Test
            fun `should create LikeNotification`() {
                val payload = LikeNotificationPayload(
                    userName = "someName",
                    postTitle = "someTitle"
                )

                val likeNotification = LikeNotificationDefinitionImpl(
                    payload = payload
                )

                assertThat(likeNotification.type).isEqualTo(LikeNotification)
            }

            @Test
            fun `should create FollowRequestNotification`() {
                val payload = FollowRequestNotificationPayload(
                    userName = "someUser"
                )

                val followRequestNotification = FollowRequestNotificationDefinitionImpl(
                    payload = payload
                )

                assertThat(followRequestNotification.type).isEqualTo(FollowRequestNotification)
            }
        }
    }
}