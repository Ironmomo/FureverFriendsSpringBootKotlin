package org.example.fureverfriends.processing.service.userfollowing

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.example.fureverfriends.config.post.PaginationProperties
import org.example.fureverfriends.api.dto.user.FoundUsersDTO
import org.example.fureverfriends.model.user.User
import org.example.fureverfriends.model.userfollowing.UserFollowing
import org.example.fureverfriends.model.userfollowing.UserFollowingKey
import org.example.fureverfriends.model.userfollowing.UserRelationStatus.ACCEPTED
import org.example.fureverfriends.model.userfollowing.UserRelationStatus.PENDING
import org.example.fureverfriends.processing.service.userfollowing.UserFollowingServiceImpl
import org.example.fureverfriends.repository.user.UserRepository
import org.example.fureverfriends.repository.userfollowing.UserFollowingRepository
import org.example.fureverfriends.stubs.stubUser
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.stream.Stream

class UserFollowingServiceImplTest {

    @Nested
    inner class FollowingRequestTests {
        @Test
        fun `should create relation for existing user`() {
            val user1 = stubUser(1)
            val user2 = stubUser(2)
            val userFollowingRepository: UserFollowingRepository = mock()
            val service = createService(
                userFollowingRepository = userFollowingRepository,
                userRepository = mock {
                    on { findUserByUsername(user1.username) } doReturn user1
                    on { findUserByUsername(user2.username) } doReturn user2
                }
            )

            service.followingRequest(followerId = user1.username, followingId = user2.username)

            verify(userFollowingRepository).save(
                UserFollowing(
                    id = UserFollowingKey(follower = user1.username, following = user2.username),
                    follower = user1,
                    following = user2,
                    status = PENDING
                )
            )
        }

        @ParameterizedTest
        @ArgumentsSource(UserArgumentsProvider::class)
        fun `should throw error for missing follower`(user1: User, user2: User, toReturn1: User?, toReturn2: User?) {
            val userFollowingRepository: UserFollowingRepository = mock()
            val service = createService(
                userFollowingRepository = userFollowingRepository,
                userRepository = mock {
                    on { findUserByUsername(user1.username) } doReturn toReturn1
                    on { findUserByUsername(user2.username) } doReturn toReturn2
                }
            )

            val throwable = catchThrowable { service.followingRequest(user1.username, user2.username) }

            assertThat(throwable).isInstanceOf(IllegalStateException::class.java).hasMessageContaining("User does not exist")
        }
    }

    @Nested
    inner class AcceptFollowingRequestTests {
        @Test
        fun `should update pending relation`() {
            val user1 = stubUser(1)
            val user2 = stubUser(2)
            val userFollowing = UserFollowing(
                id = UserFollowingKey(
                    follower = user1.username, following = user2.username
                ),
                follower = user1,
                following = user2,
                status = PENDING
            )
            val userFollowingRepository: UserFollowingRepository = mock {
                on { findUserFollowingByIdFollowerAndIdFollowingAndStatus(
                    followerId = user1.username,
                    followingId = user2.username,
                    status = PENDING
                ) } doReturn userFollowing
            }
            val service = createService(
                userFollowingRepository = userFollowingRepository
            )

            service.acceptFollowingRequest(followerId = user1.username, followingId = user2.username)

            assertAll(
                {
                    verify(userFollowingRepository).findUserFollowingByIdFollowerAndIdFollowingAndStatus(followerId = user1.username, followingId = user2.username, status = PENDING)
                    verify(userFollowingRepository).save(
                        UserFollowing(
                            id = UserFollowingKey(
                                follower = user1.username, following = user2.username
                            ),
                            follower = user1,
                            following = user2,
                            status = ACCEPTED
                        )
                    )
                }
            )
        }

        @Test
        fun `should throw error on non existing relation`() {
            val user1 = stubUser(1)
            val user2 = stubUser(2)
            val service = createService()

            val throwable = catchThrowable { service.acceptFollowingRequest(user1.username, user2.username) }

            assertThat(throwable).isInstanceOf(IllegalStateException::class.java).hasMessageContaining("There is no pending UserRelation")
        }
    }

    @Nested
    inner class RejectFollowingRequestTests {
        @Test
        fun `should delete pending relation`() {
            val user1 = stubUser(1)
            val user2 = stubUser(2)
            val userFollowing = UserFollowing(
                id = UserFollowingKey(
                    follower = user1.username, following = user2.username
                ),
                follower = user1,
                following = user2,
                status = PENDING
            )
            val userFollowingRepository: UserFollowingRepository = mock {
                on { findUserFollowingByIdFollowerAndIdFollowingAndStatus(
                    followerId = user1.username,
                    followingId = user2.username,
                    status = PENDING
                ) } doReturn userFollowing
            }
            val service = createService(
                userFollowingRepository = userFollowingRepository
            )

            service.rejectFollowingRequest(followerId = user1.username, followingId = user2.username)

            assertAll(
                {
                    verify(userFollowingRepository).findUserFollowingByIdFollowerAndIdFollowingAndStatus(followerId = user1.username, followingId = user2.username, status = PENDING)
                    verify(userFollowingRepository).delete(userFollowing)
                }
            )
        }

        @Test
        fun `should throw error on non existing relation`() {
            val user1 = stubUser(1)
            val user2 = stubUser(2)
            val service = createService()

            val throwable = catchThrowable { service.acceptFollowingRequest(user1.username, user2.username) }

            assertThat(throwable).isInstanceOf(IllegalStateException::class.java).hasMessageContaining("There is no pending UserRelation")
        }
    }
    
    @Nested
    inner class FindFollowingsTests {
        @Test
        fun `should return user followings with remaining user followings`() {
            val currentUser = "someUser"
            val pageSizeValue = 1
            val pageIndexValue = 0
            val user1 = stubUser(1)
            val user2 = stubUser(2)
            val user3 = stubUser(3)
            val userFollowings = listOf(
                UserFollowing(
                    id = UserFollowingKey(follower = user1.username, following = user2.username), 
                    follower = user1, 
                    following = user2, 
                    status = ACCEPTED
                ),
                UserFollowing(
                    id = UserFollowingKey(follower = user1.username, following = user3.username),
                    follower = user1,
                    following = user3,
                    status = ACCEPTED
                )
            )
            val followingsDTO = userFollowings.map { it.following.mapToDTO() }
            val pageRequest = PageRequest.of(pageIndexValue, pageSizeValue)
            val pagedUserFollowings = PageImpl(userFollowings, pageRequest, userFollowings.size.toLong())
            val service = createService(
                userFollowingRepository = mock {
                    on { findUserFollowingsByIdFollowerAndStatus(currentUser, ACCEPTED, pageRequest) } doReturn pagedUserFollowings
                },
                paginationProperties = mock { on { pageSize } doReturn pageSizeValue }
            )
            
            val followings = service.findFollowings(currentUser, pageIndexValue)
            
            assertThat(followings).isEqualTo(
                org.example.fureverfriends.api.dto.user.FoundUsersDTO(
                    foundUsers = followingsDTO, isLastPage = false
                )
            )
        }

        @Test
        fun `should return user followings without remaining user followings`() {
            val currentUser = "someUser"
            val pageSizeValue = 1
            val pageIndexValue = 0
            val user1 = stubUser(1)
            val user2 = stubUser(2)
            val userFollowings = listOf(
                UserFollowing(
                    id = UserFollowingKey(follower = user1.username, following = user2.username),
                    follower = user1,
                    following = user2,
                    status = ACCEPTED
                )
            )
            val followingsDTO = userFollowings.map { it.following.mapToDTO() }
            val pageRequest = PageRequest.of(pageIndexValue, pageSizeValue)
            val pagedUserFollowings = PageImpl(userFollowings, pageRequest, userFollowings.size.toLong())
            val service = createService(
                userFollowingRepository = mock {
                    on { findUserFollowingsByIdFollowerAndStatus(currentUser, ACCEPTED, pageRequest) } doReturn pagedUserFollowings
                },
                paginationProperties = mock { on { pageSize } doReturn pageSizeValue }
            )

            val followings = service.findFollowings(currentUser, pageIndexValue)

            assertThat(followings).isEqualTo(
                org.example.fureverfriends.api.dto.user.FoundUsersDTO(
                    foundUsers = followingsDTO, isLastPage = true
                )
            )
        }

        @Test
        fun `should return user followings without user followings`() {
            val currentUser = "someUser"
            val pageSizeValue = 1
            val pageIndexValue = 0
            val userFollowings = emptyList<UserFollowing>()
            val followingsDTO = userFollowings.map { it.following.mapToDTO() }
            val pageRequest = PageRequest.of(pageIndexValue, pageSizeValue)
            val pagedUserFollowings = PageImpl(userFollowings, pageRequest, 0)
            val service = createService(
                userFollowingRepository = mock {
                    on { findUserFollowingsByIdFollowerAndStatus(currentUser, ACCEPTED, pageRequest) } doReturn pagedUserFollowings
                },
                paginationProperties = mock { on { pageSize } doReturn pageSizeValue }
            )

            val followings = service.findFollowings(currentUser, pageIndexValue)

            assertThat(followings).isEqualTo(
                org.example.fureverfriends.api.dto.user.FoundUsersDTO(
                    foundUsers = followingsDTO, isLastPage = true
                )
            )
        }
    }

    @Nested
    inner class FindFollowersTests {
        @Test
        fun `should return user followers with remaining user followings`() {
            val currentUser = "someUser"
            val pageSizeValue = 1
            val pageIndexValue = 0
            val user1 = stubUser(1)
            val user2 = stubUser(2)
            val user3 = stubUser(3)
            val userFollowings = listOf(
                UserFollowing(
                    id = UserFollowingKey(follower = user1.username, following = user2.username),
                    follower = user1,
                    following = user2,
                    status = ACCEPTED
                ),
                UserFollowing(
                    id = UserFollowingKey(follower = user1.username, following = user3.username),
                    follower = user1,
                    following = user3,
                    status = ACCEPTED
                )
            )
            val followersDTO = userFollowings.map { it.follower.mapToDTO() }
            val pageRequest = PageRequest.of(pageIndexValue, pageSizeValue)
            val pagedUserFollowings = PageImpl(userFollowings, pageRequest, userFollowings.size.toLong())
            val service = createService(
                userFollowingRepository = mock {
                    on { findUserFollowingsByIdFollowingAndStatus(currentUser, ACCEPTED, pageRequest) } doReturn pagedUserFollowings
                },
                paginationProperties = mock { on { pageSize } doReturn pageSizeValue }
            )

            val followings = service.findFollowers(currentUser, pageIndexValue)

            assertThat(followings).isEqualTo(
                org.example.fureverfriends.api.dto.user.FoundUsersDTO(
                    foundUsers = followersDTO, isLastPage = false
                )
            )
        }

        @Test
        fun `should return user followings without remaining user followers`() {
            val currentUser = "someUser"
            val pageSizeValue = 1
            val pageIndexValue = 0
            val user1 = stubUser(1)
            val user2 = stubUser(2)
            val userFollowings = listOf(
                UserFollowing(
                    id = UserFollowingKey(follower = user1.username, following = user2.username),
                    follower = user1,
                    following = user2,
                    status = ACCEPTED
                )
            )
            val followingsDTO = userFollowings.map { it.follower.mapToDTO() }
            val pageRequest = PageRequest.of(pageIndexValue, pageSizeValue)
            val pagedUserFollowings = PageImpl(userFollowings, pageRequest, userFollowings.size.toLong())
            val service = createService(
                userFollowingRepository = mock {
                    on { findUserFollowingsByIdFollowingAndStatus(currentUser, ACCEPTED, pageRequest) } doReturn pagedUserFollowings
                },
                paginationProperties = mock { on { pageSize } doReturn pageSizeValue }
            )

            val followings = service.findFollowers(currentUser, pageIndexValue)

            assertThat(followings).isEqualTo(
                org.example.fureverfriends.api.dto.user.FoundUsersDTO(
                    foundUsers = followingsDTO, isLastPage = true
                )
            )
        }

        @Test
        fun `should return user followings without user followers`() {
            val currentUser = "someUser"
            val pageSizeValue = 1
            val pageIndexValue = 0
            val userFollowings = emptyList<UserFollowing>()
            val followingsDTO = userFollowings.map { it.following.mapToDTO() }
            val pageRequest = PageRequest.of(pageIndexValue, pageSizeValue)
            val pagedUserFollowings = PageImpl(userFollowings, pageRequest, 0)
            val service = createService(
                userFollowingRepository = mock {
                    on { findUserFollowingsByIdFollowingAndStatus(currentUser, ACCEPTED, pageRequest) } doReturn pagedUserFollowings
                },
                paginationProperties = mock { on { pageSize } doReturn pageSizeValue }
            )

            val followings = service.findFollowers(currentUser, pageIndexValue)

            assertThat(followings).isEqualTo(
                org.example.fureverfriends.api.dto.user.FoundUsersDTO(
                    foundUsers = followingsDTO, isLastPage = true
                )
            )
        }
    }

    private fun createService(
        userFollowingRepository: UserFollowingRepository = mock(),
        userRepository: UserRepository = mock(),
        paginationProperties: PaginationProperties = mock()
    ) = UserFollowingServiceImpl(
        userFollowingRepository = userFollowingRepository,
        userRepository = userRepository,
        paginationProperties = paginationProperties
    )
}

class UserArgumentsProvider : ArgumentsProvider {
    private val user1 = stubUser(1)
    private val user2 = stubUser(2)
    override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
        return Stream.of(
            Arguments.of(user1, user2, null, user2),
            Arguments.of(user1, user2, user1, null)
        )
    }

}