package org.example.fureverfriends.security.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.example.fureverfriends.repository.user.UserRepository
import org.example.fureverfriends.stubs.stubUser
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.User as UserDetailsImpl

class CustomUserDetailsServiceTest {

    @Test
    fun `should load existing user`() {
        val user = stubUser()
        val customUserDetailsService = createCustomUserDetailsService(
            userRepository = mock {
                on { findUserByUsername(user.username) } doReturn user
            }
        )

        val userDetails = customUserDetailsService.loadUserByUsername(user.username)

        assertThat(userDetails).isEqualTo(
            UserDetailsImpl
                .builder()
                .username(user.username)
                .password(user.password)
                .roles(user.role.name)
                .build()
        )
    }

    @Test
    fun `should throw UsernameNotFoundException if user not found`() {
        val user = stubUser()
        val customUserDetailsService = createCustomUserDetailsService(
            userRepository = mock {
                on { findUserByUsername(user.username) } doReturn null
            }
        )

        val throwable = catchThrowable { customUserDetailsService.loadUserByUsername(user.username) }

        assertThat(throwable)
            .isInstanceOf(UsernameNotFoundException::class.java)
            .hasMessageContaining("Username ${user.username} not found")
    }

    private fun createCustomUserDetailsService(
        userRepository: UserRepository = mock()
    ): CustomUserDetailsService = CustomUserDetailsService(
        userRepository = userRepository
    )
}