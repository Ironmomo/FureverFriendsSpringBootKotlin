package org.example.fureverfriends.processing.service.user

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.example.fureverfriends.api.dto.actions.ActionDTO
import org.example.fureverfriends.api.dto.actions.UserAction.FOLLOW
import org.example.fureverfriends.api.dto.user.CreateUserRequestDTO
import org.example.fureverfriends.api.dto.user.FoundUsersDTO
import org.example.fureverfriends.api.dto.user.UserDTO
import org.example.fureverfriends.api.uriprovider.UserFollowingUriProviderImpl
import org.example.fureverfriends.model.user.Role.USER
import org.example.fureverfriends.model.user.User
import org.example.fureverfriends.processing.processor.NGramSearchProcessor
import org.example.fureverfriends.repository.user.UserRepository
import org.example.fureverfriends.stubs.stubUser
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder

class UserServiceImplTest {
    @Nested
    inner class CreateUserTests {
        @Test
        fun `should create user if not existing`() {
            val userRequest = CreateUserRequestDTO(
                username = "username", password = "password"
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
                username = "username", password = "password"
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
                username = "username", password = "toshort"
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
    inner class SearchForUserTests {
        @Test
        fun `should return users on success`() {
            val searchString = "famousGuy"
            val user1 = stubUser(1)
            val user2 = stubUser(2)
            val requestUri = "https://test.com/action"
            val nGramSearchProcessor: NGramSearchProcessor = mock {
                on { nGramSearch(searchString) } doReturn setOf(user1, user2)
            }
            val userFollowingUriProviderImpl: UserFollowingUriProviderImpl = mock {
                on { getFollowingRequestUri() } doReturn requestUri
                on { toExternalUri(requestUri) } doReturn requestUri
            }
            val userService = createUserService(
                nGramSearchProcessor = nGramSearchProcessor,
                userFollowingUriProviderImpl = userFollowingUriProviderImpl
            )

            val foundUsersDTO = userService.searchForUser(searchString)

            assertAll(
                { verify(nGramSearchProcessor).nGramSearch(searchString) },
                { assertThat(foundUsersDTO).isEqualTo(
                    FoundUsersDTO(
                        foundUsers = listOf(
                            UserDTO(
                                username = user1.username,
                                actions = listOf(
                                    ActionDTO(
                                        uri = requestUri,
                                        action = FOLLOW
                                    )
                                )
                            ),
                            UserDTO(
                                username = user2.username,
                                actions = listOf(
                                    ActionDTO(
                                        uri = requestUri,
                                        action = FOLLOW
                                    )
                                )
                            )
                        )
                    )
                ) }
            )
        }
    }

    @Nested
    inner class GetUserByUsernameTests {
        @Test
        fun `should find user by username`() {
            val user = stubUser()
            val service = createUserService(
                userRepository = mock {
                    on { findUserByUsername(user.username) } doReturn user
                }
            )

            val returned = service.getUserByUsername(user.username)

            assertThat(returned).isEqualTo(user)
        }

        @Test
        fun `should throw UsernameNotFoundException when user not found`() {
            val service = createUserService()

            val throwable = catchThrowable { service.getUserByUsername("someUser") }

            assertThat(throwable).isInstanceOf(UsernameNotFoundException::class.java)
        }
    }

    private fun createUserService(
        userRepository: UserRepository = mock(),
        encoder: PasswordEncoder = mock(),
        nGramSearchProcessor: NGramSearchProcessor = mock(),
        userFollowingUriProviderImpl: UserFollowingUriProviderImpl = mock()
    ) =
        UserServiceImpl(
            userRepository = userRepository,
            encoder = encoder,
            nGramSearchProcessor = nGramSearchProcessor,
            userFollowingUriProviderImpl = userFollowingUriProviderImpl,
        )
}