package org.example.fureverfriends.repository.userfollowing

import jakarta.persistence.EntityManager
import jakarta.persistence.Persistence
import jakarta.persistence.PersistenceUtil
import org.assertj.core.api.Assertions.assertThat
import org.example.fureverfriends.model.user.User
import org.example.fureverfriends.model.userfollowing.UserFollowing
import org.example.fureverfriends.model.userfollowing.UserFollowingKey
import org.example.fureverfriends.model.userfollowing.UserRelationStatus.ACCEPTED
import org.example.fureverfriends.model.userfollowing.UserRelationStatus.PENDING
import org.hibernate.Session
import org.hibernate.stat.Statistics
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

@DataJpaTest
class UserFollowingRepositoryTest @Autowired constructor(
    private val entityManager: EntityManager,
    private val userFollowingRepository: UserFollowingRepository
) {
    private lateinit var hibernateStatistics: Statistics
    private lateinit var persistenceUtil: PersistenceUtil

    private lateinit var entry1: UserFollowing
    private lateinit var entry2: UserFollowing
    private lateinit var entry3: UserFollowing
    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var user3: User


    @Nested
    open inner class FindindUserFollowingByIdFollowerAndIdFollowingAndStatusTests {

        @Test
        @Transactional
        open fun `should return UserFollowing existing`() {
            val userFollowing = userFollowingRepository.findUserFollowingByIdFollowerAndIdFollowingAndStatus(
                followerId = user1.username, followingId = user2.username, status = ACCEPTED
            )

            assertThat(userFollowing).isNotNull
        }

        @Test
        @Transactional
        open fun `should return null on UserFollowing pending`() {
            val userFollowing = userFollowingRepository.findUserFollowingByIdFollowerAndIdFollowingAndStatus(
                followerId = user1.username, followingId = user3.username, status = ACCEPTED
            )

            assertThat(userFollowing).isNull()
        }

        @Test
        @Transactional
        open fun `should return null on UserFollowing not existing`() {
            val userFollowing = userFollowingRepository.findUserFollowingByIdFollowerAndIdFollowingAndStatus(
                followerId = user3.username, followingId = user1.username, status = ACCEPTED
            )

            assertThat(userFollowing).isNull()
        }
    }

    @Nested
    open inner class FindUserFollowingsByFollowerIdTests {

        @Test
        @Transactional
        open fun `should prevent n + 1 problem`() {
            val page = PageRequest.of(0, 10)

            userFollowingRepository.findUserFollowingsByIdFollowerAndStatus(followerId = user1.username, ACCEPTED, page)

            assertThat(hibernateStatistics.prepareStatementCount).isOne()
        }

        @Test
        @Transactional
        open fun `findUserFollowingsByFollowerId should return user followers`() {
            val page = PageRequest.of(0, 10)

            val foundAccepted = userFollowingRepository.findUserFollowingsByIdFollowerAndStatus(followerId = user1.username, ACCEPTED, page)
            val foundPending = userFollowingRepository.findUserFollowingsByIdFollowerAndStatus(followerId = user1.username, PENDING, page)

            assertAll(
                { assertThat(foundAccepted.content.size).isEqualTo(1) },
                { assertThat(foundAccepted.content).isEqualTo(listOf(entry1)) },
                { assertThat(foundPending.content.size).isEqualTo(1) },
                { assertThat(foundPending.content).isEqualTo(listOf(entry2)) }
            )
        }

        @Test
        @Transactional
        open fun `findUserFollowingsByFollowerId should return no user followers`() {
            val page = PageRequest.of(0, 10)

            val found = userFollowingRepository.findUserFollowingsByIdFollowerAndStatus(followerId = user3.username, status = ACCEPTED, page)

            assertThat(found.content.size).isEqualTo(0)
        }
    }

    @Nested
    open inner class FindUserFollowingsByFollowingIdTests {

        @Test
        @Transactional
        open fun `should prevent n + 1 problem`() {
            val page = PageRequest.of(0, 10)

            userFollowingRepository.findUserFollowingsByIdFollowingAndStatus(
                followingId = user1.username,
                status = ACCEPTED,
                pageable = page
            )

            assertThat(hibernateStatistics.prepareStatementCount).isOne()
        }

        @Test
        @Transactional
        open fun `findUserFollowingsByFollowerId should return user followers`() {
            val page = PageRequest.of(0, 10)

            val foundAccepted = userFollowingRepository.findUserFollowingsByIdFollowingAndStatus(
                followingId = user1.username,
                status = ACCEPTED,
                pageable = page
            )

            val foundPending = userFollowingRepository.findUserFollowingsByIdFollowingAndStatus(
                followingId = user3.username,
                status = PENDING,
                pageable = page
            )

            assertAll(
                { assertThat(foundAccepted.content.size).isEqualTo(1) },
                { assertThat(foundAccepted.content).isEqualTo(listOf(entry3)) },
                { assertThat(foundPending.content.size).isEqualTo(1) },
                { assertThat(foundPending.content).isEqualTo(listOf(entry2)) }
            )
        }

        @Test
        @Transactional
        open fun `findUserFollowingsByFollowerId should return no user followers`() {
            val page = PageRequest.of(0, 10)

            val found = userFollowingRepository.findUserFollowingsByIdFollowingAndStatus(
                followingId = user3.username,
                status = ACCEPTED,
                pageable = page
            )

            assertThat(found.content.size).isEqualTo(0)
        }
    }


    @BeforeEach
    fun setUp() {
        initData()
        initUtils()
        hibernateStatistics.clear()
    }

    private fun initData() {
        user1 = User(
            username = "user1", password = "1234"
        )
        user2 = User(
            username = "user2", password = "1234"
        )
        user3 = User(
            username = "user3", password = "1234"
        )
        entityManager.persist(user1)
        entityManager.persist(user2)
        entityManager.persist(user3)
        entityManager.flush()
        entry1 = UserFollowing(
            id = UserFollowingKey(follower = user1.username, following = user2.username),
            follower = user1,
            following = user2,
            status = ACCEPTED
        )
        entry2 = UserFollowing(
            id = UserFollowingKey(follower = user1.username, following = user3.username),
            follower = user1,
            following = user3,
            status = PENDING
        )
        entry3 = UserFollowing(
            id = UserFollowingKey(follower = user2.username, following = user1.username),
            follower = user2,
            following = user1,
            status = ACCEPTED
        )
        entityManager.persist(entry1)
        entityManager.persist(entry2)
        entityManager.persist(entry3)
        entityManager.flush()
        entityManager.clear()
    }

    private fun initUtils() {
        val sessionFactory = entityManager.unwrap(Session::class.java).sessionFactory
        hibernateStatistics = sessionFactory.statistics
        hibernateStatistics.isStatisticsEnabled = true
        persistenceUtil = Persistence.getPersistenceUtil()
    }
}