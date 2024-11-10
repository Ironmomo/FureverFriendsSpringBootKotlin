package org.example.fureverfriends.repository.notification

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.example.fureverfriends.model.notification.Notification
import org.example.fureverfriends.model.notification.NotificationStatus
import org.example.fureverfriends.model.notification.NotificationStatus.RESOLVED
import org.example.fureverfriends.model.notification.NotificationType.LikeNotification
import org.example.fureverfriends.model.user.User
import org.example.fureverfriends.repository.notififaction.NotificationRepository
import org.example.fureverfriends.stubs.stubUser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class NotificationRepositoryTest @Autowired constructor(
    private val notificationRepository: NotificationRepository,
    private val entityManager: EntityManager
) {
    private lateinit var user1: User
    private lateinit var user2: User

    @Nested
    open inner class FindByUserNameAndStatusNotTests {
        @Test
        fun `should find notifications from user`() {
            val notification1 = Notification(
                type = LikeNotification,
                payload = "some payload",
                user = user1
            )
            val notification2 = Notification(
                type = LikeNotification,
                payload = "some payload",
                user = user1,
                status = RESOLVED
            )
            val notification3 = Notification(
                type = LikeNotification,
                payload = "some payload",
                user = user2
            )
            entityManager.persist(notification1)
            entityManager.persist(notification2)
            entityManager.persist(notification3)
            entityManager.flush()

            val notifications = notificationRepository.findNotificationsByUserUsernameAndStatusNot(user1.username, RESOLVED)

            assertAll(
                { assertThat(notifications.size).isEqualTo(1) },
                { assertThat(notifications[0]).isEqualTo(notification1) },
            )
        }

        @ParameterizedTest
        @EnumSource(value = NotificationStatus::class)
        fun `should find empty notifications from user`(status: NotificationStatus) {
            val notifications = notificationRepository.findNotificationsByUserUsernameAndStatusNot(user1.username, status)

            assertThat(notifications.size).isZero()
        }
    }

    @BeforeEach
    fun setUp() {
        user1 = stubUser(1)
        user2 = stubUser(2)
        entityManager.persist(user1)
        entityManager.persist(user2)
        entityManager.flush()
        entityManager.clear()
    }
}