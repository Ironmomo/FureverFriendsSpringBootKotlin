package org.example.fureverfriends.handler

import org.assertj.core.api.Assertions.assertThat
import org.example.fureverfriends.repository.user.UserRepository
import org.example.fureverfriends.stubs.stubUser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class NGramSearchHandlerTest {

    @Test
    fun `should call service and return a list of users`() {
        val user1 = stubUser(1)
        val user2 = stubUser(2)
        val user3 = stubUser(3)
        val user4 = stubUser(4)
        val user5 = stubUser(5)
        val user6 = stubUser(6)
        val searchPattern = "name 1"
        val userRepository: UserRepository = mock {
            on { findByUsernameContainingIgnoreCase("nam", PageRequest.of(0, 100)) } doReturn PageImpl(listOf(user1, user2, user3, user4, user5, user6))
            on { findByUsernameContainingIgnoreCase("e 1", PageRequest.of(0, 100)) } doReturn PageImpl(listOf(user1, user2, user3, user4, user5))
            on { findAllByUsernameIn(listOf(user1.username, user2.username, user3.username, user4.username, user5.username))} doReturn setOf(user1, user2, user3, user4, user5)
        }
        val handler = createHandler(userRepository)

        val foundUser = handler.nGramSearch(searchPattern)

        assertAll(
            { verify(userRepository).findByUsernameContainingIgnoreCase("nam", PageRequest.of(0, 100)) },
            { verify(userRepository).findByUsernameContainingIgnoreCase("e 1", PageRequest.of(0, 100)) },
            { verify(userRepository).findAllByUsernameIn(listOf(user1.username, user2.username, user3.username, user4.username, user5.username)) },
            { assertThat(foundUser).isEqualTo(setOf(user1, user2, user3, user4, user5)) }
        )
    }

    @Test
    fun `on searchString length less then 3 should return empty list`() {
        val searchString = "so"
        val handler = createHandler()

        val foundUsers = handler.nGramSearch(searchString)

        assertThat(foundUsers).isEmpty()
    }

    private fun createHandler(
        userRepository: UserRepository = mock(),
    ) = NGramSearchHandler(
        userRepository = userRepository
    )
}