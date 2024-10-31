package org.example.fureverfriends.util.aop.userfollowing

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.example.fureverfriends.model.user.User
import org.example.fureverfriends.model.userfollowing.UserFollowing
import org.example.fureverfriends.model.userfollowing.UserFollowingKey
import org.example.fureverfriends.model.userfollowing.UserRelationStatus.ACCEPTED
import org.example.fureverfriends.model.userfollowing.UserRelationStatus.PENDING
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class FollowerAspectTest @Autowired constructor(
    private val entityManager: EntityManager,
    private val functionService: FunctionService
) {

    private lateinit var entry1: UserFollowing
    private lateinit var entry2: UserFollowing
    private lateinit var entry3: UserFollowing
    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var user3: User

    @Nested
    inner class TwoArgumentsTests {
        @Test
        fun `should execute function using existing relation`() {
            val returned = functionService.returnNumberTwoArg(user1.username, user2.username)

            assertThat(returned).isEqualTo(5)
        }

        @Test
        fun `should throw error using pending relation`() {
            val throwable = catchThrowable { functionService.returnNumberTwoArg(user1.username, user3.username) }

            assertThat(throwable).isInstanceOf(IllegalAccessException::class.java)
        }

        @Test
        fun `should throw error using non existing relation`() {
            val throwable = catchThrowable { functionService.returnNumberTwoArg(user2.username, user3.username) }

            assertThat(throwable).isInstanceOf(IllegalAccessException::class.java)
        }
    }

    @Nested
    inner class ThreeArgumentsTests {
        @Test
        fun `should execute function using existing relation`() {
            val toReturn = 10
            val returned = functionService.returnNumberThreeArg(user1.username, user2.username, toReturn)

            assertThat(returned).isEqualTo(toReturn)
        }

        @Test
        fun `should throw error using pending relation`() {
            val throwable = catchThrowable { functionService.returnNumberThreeArg(user1.username, user3.username, 10) }

            assertThat(throwable).isInstanceOf(IllegalAccessException::class.java)
        }

        @Test
        fun `should throw error using non existing relation`() {
            val throwable = catchThrowable { functionService.returnNumberThreeArg(user2.username, user3.username, 10) }

            assertThat(throwable).isInstanceOf(IllegalAccessException::class.java)
        }
    }

    @BeforeEach
    fun initData() {
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
}