package org.example.fureverfriends.repository.user

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.example.fureverfriends.model.user.Role
import org.example.fureverfriends.model.user.User
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest

@DataJpaTest
class UserRepositoryTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val entityManager: EntityManager,
) {
    @Nested
    open inner class FindAllByUsernameIn {

        @Test
        @Transactional
        open fun `should find users`() {
            val user1 = User(
                username = "aaa", password = "somePassword"
            )
            val user2 = User(
                username = "bbb", password = "somePassword"
            )
            val user3 = User(
                username = "aabb", password = "somePassword"
            )
            val user4 = User(
                username = "ccc", password = "somePassword"
            )
            entityManager.persist(user1)
            entityManager.persist(user2)
            entityManager.persist(user3)
            entityManager.persist(user4)
            entityManager.flush()
            entityManager.clear()

            val foundUsers = userRepository.findAllByUsernameIn(listOf(user1.username, user2.username, "not-existing"))

            assertThat(foundUsers).isEqualTo(setOf(user1, user2))
        }

        @Test
        @Transactional
        open fun `should find no users for empty list`() {
            val user1 = User(
                username = "aaa", password = "somePassword"
            )
            val user2 = User(
                username = "bbb", password = "somePassword"
            )
            val user3 = User(
                username = "aabb", password = "somePassword"
            )
            val user4 = User(
                username = "ccc", password = "somePassword"
            )
            entityManager.persist(user1)
            entityManager.persist(user2)
            entityManager.persist(user3)
            entityManager.persist(user4)
            entityManager.flush()
            entityManager.clear()

            val foundUsers = userRepository.findAllByUsernameIn(emptyList())

            assertThat(foundUsers.size).isEqualTo(0)
        }
    }

    @Nested
    open inner class FindByUsernameContainingTests {

        @Test
        @Transactional
        open fun `should find users by existing substring`() {
            val user1 = User(
                username = "aaa", password = "somePassword"
            )
            val user2 = User(
                username = "bbb", password = "somePassword"
            )
            val user3 = User(
                username = "aabb", password = "somePassword"
            )
            val user4 = User(
                username = "ccc", password = "somePassword"
            )
            val user5 = User(
                username = "Aacc", password = "somePassword"
            )
            entityManager.persist(user1)
            entityManager.persist(user2)
            entityManager.persist(user3)
            entityManager.persist(user4)
            entityManager.persist(user5)
            entityManager.flush()
            entityManager.clear()
            val page = PageRequest.of(0, 10)

            val pageable = userRepository.findByUsernameContainingIgnoreCase("aa", page)

            assertAll(
                { assertThat(pageable.totalElements).isEqualTo(3) },
                { assertThat(pageable.content).isEqualTo(listOf(user1, user3, user5)) }
            )
        }

        @Test
        @Transactional
        open fun `should find no user by none-existing substring`() {
            val user1 = User(
                username = "aaa", password = "somePassword"
            )
            val user2 = User(
                username = "bbb", password = "somePassword"
            )
            val user3 = User(
                username = "aabb", password = "somePassword"
            )
            val user4 = User(
                username = "ccc", password = "somePassword"
            )
            entityManager.persist(user1)
            entityManager.persist(user2)
            entityManager.persist(user3)
            entityManager.persist(user4)
            entityManager.flush()
            entityManager.clear()
            val page = PageRequest.of(0, 10)

            val pageable = userRepository.findByUsernameContainingIgnoreCase("dd", page)

            assertThat(pageable.totalElements).isEqualTo(0)
        }
    }

    @Nested
    open inner class FindByNameTests {

        @Test
        @Transactional
        open fun `should find user by name`() {
            val user = userRepository.findUserByUsername("User 1")

            assertThat(user).isNotNull()
        }

        @Test
        @Transactional
        open fun `should return null on non existing user`() {
            val user = userRepository.findUserByUsername("Not a User")

            assertNull(user)
        }
    }

    @Nested
    open inner class CreateUserTests {

        @Test
        @Transactional
        open fun `should create user with role USER by default`() {
            val userToCreate = User(username = "username", password = "password")
            userRepository.save(userToCreate)
            val fetchedUser = userRepository.findUserByUsername("username")

            assertAll(
                { assertThat(fetchedUser!!.role).isEqualTo(Role.USER) },
            )
        }

    }

    @BeforeEach
    fun setUp() {
        val user1 = User(
            username = "User 1", password = "some password"
        )
        val user2 = User(
            username = "User 2", password = "some password"
        )

        entityManager.persist(user1)
        entityManager.persist(user2)

        entityManager.flush()
        entityManager.clear()
    }
}