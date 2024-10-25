package org.example.fureverfriends.repository.post

import jakarta.persistence.EntityManager
import jakarta.persistence.Persistence
import jakarta.persistence.PersistenceUtil
import org.assertj.core.api.Assertions.assertThat
import org.example.fureverfriends.model.post.Post
import org.example.fureverfriends.model.user.User
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
import kotlin.test.assertTrue


@DataJpaTest
internal class PostRepositoryTest @Autowired constructor(
    private val postRepository: PostRepository,
    private val entityManager: EntityManager,
) {
    private lateinit var hibernateStatistics: Statistics
    private lateinit var persistenceUtil: PersistenceUtil

    @BeforeEach
    fun setUp() {
        initUtil()
        insertInitialData()
        hibernateStatistics.clear()
    }

    @Nested
    open inner class FindAllTests {

        @Test
        open fun `should execute findAll preventing n + 1 problem for user`() {
            val pageable = PageRequest.of(0, 10, by(DESC, "createdAt"))

            val posts = postRepository.findAll(pageable)

            assertAll(
                { posts.forEach { assertTrue { persistenceUtil.isLoaded(it.user) } } },
                { assertThat(hibernateStatistics.collectionFetchCount).isZero() },
                { assertThat(hibernateStatistics.prepareStatementCount).isEqualTo(2) } // 2 queries because of paging
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
        val initialNumberPost = 10
        // Insert initial test data
        val user1 = User(
            username = "User 1", password = ".."
        )

        val user2 = User(
            username = "User 2", password = ".."
        )

        entityManager.persist(user1)
        entityManager.persist(user2)


        for (i in 1..initialNumberPost) {
            val post = Post(
                title = "Title $i", content = "Contnet $i", user = user1
            )
            entityManager.persist(post)
        }

        for (i in 1..initialNumberPost) {
            val post = Post(
                title = "Title $i", content = "Contnet $i", user = user2
            )
            entityManager.persist(post)
        }

        entityManager.flush()
        entityManager.clear()
    }
}