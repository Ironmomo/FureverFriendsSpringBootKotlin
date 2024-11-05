package org.example.fureverfriends.repository.post

import jakarta.persistence.EntityManager
import jakarta.persistence.Persistence
import jakarta.persistence.PersistenceUtil
import org.assertj.core.api.Assertions.assertThat
import org.example.fureverfriends.model.post.Post
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
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.domain.Sort.by
import java.time.LocalDateTime
import kotlin.test.assertTrue


@DataJpaTest
class PostRepositoryTest @Autowired constructor(
    private val postRepository: PostRepository,
    private val entityManager: EntityManager,
) {
    private lateinit var hibernateStatistics: Statistics
    private lateinit var persistenceUtil: PersistenceUtil

    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var user3: User
    private lateinit var user4: User
    private lateinit var post1: Post
    private lateinit var post2: Post
    private lateinit var post3: Post
    private lateinit var post4: Post

    @BeforeEach
    fun setUp() {
        initUtil()
        insertInitialData()
        hibernateStatistics.clear()
    }

    @Nested
    open inner class FindPostsByFollowedUsersTests {
        @Test
        open fun `should execute findPostsByFollowedUsersTests preventing n + 1 problem for user`() {
            val pageable = PageRequest.of(0, 10, by(DESC, "createdAt"))

            val posts = postRepository.findPostsByFollowedUsers("User 1", ACCEPTED, pageable)

            assertAll(
                { posts.forEach { assertTrue { persistenceUtil.isLoaded(it.user) } } },
                { assertThat(hibernateStatistics.collectionFetchCount).isZero() },
                { assertThat(hibernateStatistics.prepareStatementCount).isOne() }
            )
        }

        @Test
        open fun `should find posts from following`() {
            val pageable = PageRequest.of(0, 10, by(DESC, "createdAt"))

            val posts = postRepository.findPostsByFollowedUsers("User 1", ACCEPTED, pageable)

            assertAll(
                { assertThat(posts.content.size).isEqualTo(2) },
                { assertThat(posts.content).isEqualTo(listOf(post2, post4)) }
            )
        }

        @Test
        open fun `should return empty list of posts if not following anyone`() {
            val pageable = PageRequest.of(0, 10, by(DESC, "createdAt"))

            val posts = postRepository.findPostsByFollowedUsers("User 2", ACCEPTED, pageable)

            assertThat(posts.content.size).isZero()
        }
    }

    @Nested
    open inner class FindPostByIdTests {
        @Test
        fun `on findPostById should prevent n + 1 problem for user`() {
            val post = postRepository.findPostById(post1.id)

            assertAll(
                { assertThat(post).isEqualTo(post1) },
                { assertThat(hibernateStatistics.collectionFetchCount).isZero() },
                { assertThat(hibernateStatistics.prepareStatementCount).isOne() }
            )
        }
    }

    private fun initUtil() {
        val sessionFactory = entityManager.unwrap(Session::class.java).sessionFactory
        hibernateStatistics = sessionFactory.statistics
        hibernateStatistics.isStatisticsEnabled = true
        persistenceUtil = Persistence.getPersistenceUtil()
    }

    private fun insertInitialData() {
        // Insert initial test data
        user1 = User(
            username = "User 1", password = ".."
        )

        user2 = User(
            username = "User 2", password = ".."
        )

        user3 = User(
            username = "User 3", password = ".."
        )

        user4 = User(
            username = "User 4", password = ".."
        )

        entityManager.persist(user1)
        entityManager.persist(user2)
        entityManager.persist(user3)
        entityManager.persist(user4)

        post1 = Post(
            title = "Title 1", content = "Content 1", user = user1, createdAt = LocalDateTime.of(2024, 10, 30, 10, 10, 50)
        )
        post2 = Post(
            title = "Title 2", content = "Content 2", user = user2, createdAt = LocalDateTime.of(2024, 10, 30, 10, 10, 40)
        )
        post3 = Post(
            title = "Title 3", content = "Content 3", user = user3, createdAt = LocalDateTime.of(2024, 10, 30, 10, 10, 30)
        )
        post4 = Post(
            title = "Title 4", content = "Content 4", user = user4, createdAt = LocalDateTime.of(2024, 10, 30, 10, 10, 20)
        )

        entityManager.persist(post1)
        entityManager.persist(post2)
        entityManager.persist(post3)
        entityManager.persist(post4)

        val relation1 = UserFollowing(
            id = UserFollowingKey(
                follower = user1.username, following = user2.username
            ),
            follower = user1,
            following = user2,
            status = ACCEPTED
        )

        val relation2 = UserFollowing(
            id = UserFollowingKey(
                follower = user1.username, following = user3.username
            ),
            follower = user1,
            following = user3,
            status = PENDING
        )

        val relation3 = UserFollowing(
            id = UserFollowingKey(
                follower = user1.username, following = user4.username
            ),
            follower = user1,
            following = user4,
            status = ACCEPTED
        )

        entityManager.persist(relation1)
        entityManager.persist(relation2)
        entityManager.persist(relation3)

        entityManager.flush()
        entityManager.clear()
    }
}