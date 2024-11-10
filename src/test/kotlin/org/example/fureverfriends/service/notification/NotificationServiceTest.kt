package org.example.fureverfriends.service.notification

import com.google.gson.Gson
import org.assertj.core.api.Assertions.assertThat
import org.example.fureverfriends.dto.notification.NotificationDTO
import org.example.fureverfriends.dto.notification.NotificationStatusDTO
import org.example.fureverfriends.dto.notification.NotificationTypeDTO
import org.example.fureverfriends.model.notification.FollowRequestNotificationPayload
import org.example.fureverfriends.model.notification.LikeNotificationPayload
import org.example.fureverfriends.model.notification.Notification
import org.example.fureverfriends.model.notification.NotificationDefinition.FollowRequestNotificationDefinitionImpl
import org.example.fureverfriends.model.notification.NotificationDefinition.LikeNotificationDefinitionImpl
import org.example.fureverfriends.model.notification.NotificationStatus.RESOLVED
import org.example.fureverfriends.repository.notififaction.NotificationRepository
import org.example.fureverfriends.service.user.UserService
import org.example.fureverfriends.stubs.stubNotification
import org.example.fureverfriends.stubs.stubUser
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@SpringBootTest
class NotificationServiceTest {

    @Nested
    inner class CreateNotificationTests {
        @Test
        fun `should create notification for LikeNotification`() {
            val user = stubUser()
            val userName = "name"
            val postTitle = "title"
            val payload = LikeNotificationPayload(
                userName = userName,
                postTitle = postTitle
            )
            val json = "json payload"
            val notification = LikeNotificationDefinitionImpl(payload = payload)
            val notificationRepository: NotificationRepository = mock()
            val service = createService(
                userService = mock {
                    on { getUserByUsername(user.username) } doReturn user
                },
                gson = mock {
                    on { toJson(payload) } doReturn json
                },
                notificationRepository = notificationRepository
            )

            service.createNotification(notification, user.username)

            verify(notificationRepository).save(
                Notification(
                    type = notification.type,
                    payload = json,
                    user = user
                )
            )
        }

        @Test
        fun `should create notification for FollowRequestNotification`() {
            val user = stubUser()
            val userName = "name"
            val payload = FollowRequestNotificationPayload(
                userName = userName
            )
            val json = "json payload"
            val notification = FollowRequestNotificationDefinitionImpl(payload = payload)
            val notificationRepository: NotificationRepository = mock()
            val service = createService(
                userService = mock {
                    on { getUserByUsername(user.username) } doReturn user
                },
                gson = mock {
                    on { toJson(payload) } doReturn json
                },
                notificationRepository = notificationRepository
            )

            service.createNotification(notification, user.username)

            verify(notificationRepository).save(
                Notification(
                    type = notification.type,
                    payload = json,
                    user = user
                )
            )
        }
    }

    @Nested
    inner class LoadNotificationByUserTests {
        @Test
        fun `should load notification by user`() {
            val user = stubUser()
            val notificationOne = stubNotification(1)
            val notifications = listOf(
                notificationOne
            )
            val service = createService(
                notificationRepository = mock {
                    on { findNotificationsByUserUsernameAndStatusNot(user.username, RESOLVED) } doReturn notifications
                }
            )

            val returned = service.loadNotificationByUser(user.username)

            assertAll(
                { assertThat(returned.size).isOne() },
                { assertThat(returned).isEqualTo(
                    listOf(NotificationDTO(
                        payload = notificationOne.payload,
                        status = NotificationStatusDTO.valueOf(notificationOne.status.name),
                        type = NotificationTypeDTO.valueOf(notificationOne.type.name)
                    ))
                ) }
            )
        }

        @Test
        fun `should load empty notification by user`() {
            val user = stubUser()
            val notifications = emptyList<Notification>()
            val service = createService(
                notificationRepository = mock {
                    on { findNotificationsByUserUsernameAndStatusNot(user.username, RESOLVED) } doReturn notifications
                }
            )

            val returned = service.loadNotificationByUser(user.username)

            assertThat(returned.size).isZero()
        }
    }

    private fun createService(
        notificationRepository: NotificationRepository = mock(),
        userService: UserService = mock(),
        gson: Gson = mock()
    ) = NotificationServiceImpl(
        notificationRepository = notificationRepository,
        userService = userService,
        gson = gson
    )
}