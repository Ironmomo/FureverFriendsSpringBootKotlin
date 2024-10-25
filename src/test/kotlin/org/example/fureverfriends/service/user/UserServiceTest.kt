package org.example.fureverfriends.service.user

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.example.fureverfriends.dto.user.CreateUserRequestDTO
import org.example.fureverfriends.dto.user.FoundUsersDTO
import org.example.fureverfriends.dto.user.UserDTO
import org.example.fureverfriends.handler.NGramSearchHandler
import org.example.fureverfriends.model.user.Role.USER
import org.example.fureverfriends.model.user.User
import org.example.fureverfriends.repository.user.UserRepository
import org.example.fureverfriends.stubs.stubUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.security.crypto.password.PasswordEncoder

class UserServiceTest {
    @Nested
    inner class CreateUserTests {
        @Test
        fun `should create user if not existing`() {
            val userRequest = CreateUserRequestDTO(
                username = "username",
                password = "password"
            )
            val encodedUser = User(
                username = "username",
                password = "encoded-password"
            )
            val userRepository: UserRepository = mock {
                on { findUserByUsername(userRequest.username) } doReturn null
            }
            val encoder: PasswordEncoder = mock {
                on { encode(userRequest.password) } doReturn "encoded-password"
            }
            val userService = createUserService(
                userRepository = userRepository,
                encoder = encoder
            )

            userService.createUser(userRequest)

            assertAll(
                { verify(encoder).encode(userRequest.password) },
                { verify(userRepository).findUserByUsername(userRequest.username) },
                { verify(userRepository).save(encodedUser) },
                { assertThat(encodedUser.role).isEqualTo(USER) }
            )
        }

        @Test
        fun `should throw IllegalStateException on user existing`() {
            val userRequest = CreateUserRequestDTO(
                username = "username",
                password = "password"
            )
            val userService = createUserService(
                userRepository = mock {
                    on { findUserByUsername(userRequest.username) } doReturn stubUser()
                }
            )

            val throwable = catchThrowable { userService.createUser(userRequest) }

            assertThat(throwable).isInstanceOf(IllegalStateException::class.java).hasMessage("User already exists")
        }

        @Test
        fun `should throw IllegalArgumentException on password not valid`() {
            val userRequest = CreateUserRequestDTO(
                username = "username",
                password = "toshort"
            )
            val userService = createUserService(
                userRepository = mock {
                    on { findUserByUsername(userRequest.username) } doReturn stubUser()
                }
            )

            val throwable = catchThrowable { userService.createUser(userRequest) }

            assertThat(throwable).isInstanceOf(IllegalArgumentException::class.java).hasMessage("Password must be at least 8 characters long")
        }
    }

    @Nested
    inner class FindUserByUsernameTests {
        @Test
        fun `should find user by username`() {
            val user = stubUser().copy(username = "username", password = "password")
            val userRepository: UserRepository = mock {
                on { findUserByUsername(user.username) } doReturn user
            }
            val userService = createUserService(
                userRepository = userRepository
            )

            val returnedUser = userService.findUserByUsername(user.username)

            assertAll(
                { assertEquals(user, returnedUser) },
                { verify(userRepository).findUserByUsername(user.username) }
            )
        }

        @Test
        fun `should throw IllegalStateException if user not existing`() {
            val user = stubUser().copy(username = "username", password = "password")
            val userService = createUserService(
                userRepository = mock {
                    on { findUserByUsername(user.username) } doReturn null
                }
            )

            val throwable = catchThrowable { userService.findUserByUsername(user.username) }

            assertThat(throwable).isInstanceOf(IllegalStateException::class.java).hasMessage("User doesn't exist")
        }
    }

    @Nested
    inner class SearchForUserTests {
        @Test
        fun `should return users on success`() {
            val searchString = "famousGuy"
            val user1 = stubUser(1)
            val user2 = stubUser(2)
            val nGramSearchHandler: NGramSearchHandler = mock {
                on { nGramSearch(searchString) } doReturn setOf(user1, user2)
            }
            val userService = createUserService(nGramSearchHandler = nGramSearchHandler)

            val foundUsersDTO = userService.searchForUser(searchString)

            assertAll(
                { verify(nGramSearchHandler).nGramSearch(searchString) },
                { assertThat(foundUsersDTO).isEqualTo(FoundUsersDTO(
                    foundUsers = listOf(
                        UserDTO(user1.username),
                        UserDTO(user2.username)
                    )
                )) }
            )
        }
    }

    private fun createUserService(
        userRepository: UserRepository = mock(),
        encoder: PasswordEncoder = mock(),
        nGramSearchHandler: NGramSearchHandler = mock()
    ) =
        UserService(
            userRepository = userRepository,
            encoder = encoder,
            nGramSearchHandler = nGramSearchHandler,
        )
}