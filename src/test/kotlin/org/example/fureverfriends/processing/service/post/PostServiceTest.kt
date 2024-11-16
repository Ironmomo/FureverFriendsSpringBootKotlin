package org.example.fureverfriends.processing.service.post

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.example.fureverfriends.api.dto.actions.ActionDTO
import org.example.fureverfriends.api.dto.actions.PostAction.LIKE
import org.example.fureverfriends.api.dto.post.LatestPostsDTO
import org.example.fureverfriends.api.dto.post.PostResponseDTO
import org.example.fureverfriends.api.uriprovider.PostUriProviderImpl
import org.example.fureverfriends.config.post.PaginationProperties
import org.example.fureverfriends.model.post.Post
import org.example.fureverfriends.model.userfollowing.UserRelationStatus.ACCEPTED
import org.example.fureverfriends.processing.processor.PostLikeProcessor
import org.example.fureverfriends.repository.post.PostRepository
import org.example.fureverfriends.stubs.stubPost
import org.example.fureverfriends.stubs.stubUser
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import kotlin.test.Test

class PostServiceTest {

    @Nested
    inner class GetLatestPostsTests {
        @Test
        fun `test pagination page size with remaining posts`() {
            val uri = "some uri"
            val currentUser = "someUser"
            val pageSizeValue = 4
            val pageIndexValue = 0
            val posts = listOf(stubPost(1), stubPost(2), stubPost(3), stubPost(4), stubPost(5))
            val postDTOs = posts.map { it.mapToDTO().copy(
                actions = listOf(
                    ActionDTO(
                    uri = uri,
                    action = LIKE
                )
                )
            ) }
            val pageRequest = PageRequest.of(pageIndexValue, pageSizeValue, Sort.by(Sort.Direction.DESC, "createdAt"))
            val pagedPosts = PageImpl(posts, pageRequest, posts.size.toLong())
            val postService = createPostService(
                postRepository = mock {
                    on { findPostsByFollowedUsers(currentUser, ACCEPTED, pageRequest) } doReturn pagedPosts
                },
                paginationProperties = mock { on { pageSize } doReturn pageSizeValue },
                postUriProviderImpl = mock {
                    on { getLikePostUri() } doReturn uri
                    on { toExternalUri(uri) } doReturn uri
                }
            )

            val latestPosts = postService.findLatestPosts(currentUser, pageIndexValue)

            assertThat(latestPosts).isEqualTo(
                LatestPostsDTO(
                    posts = postDTOs,
                    isLastPage = false
                )
            )
        }

        @Test
        fun `test pagination page size without remaining posts`() {
            val uri = "some uri"
            val currentUser = "someUser"
            val pageSizeValue = 4
            val pageIndexValue = 0
            val posts = listOf(stubPost(1), stubPost(2), stubPost(3), stubPost(4))
            val postDTOs = posts.map { it.mapToDTO().copy(
                actions = listOf(
                    ActionDTO(
                        uri = uri,
                        action = LIKE
                    )
                )
            ) }
            val pageRequest = PageRequest.of(pageIndexValue, pageSizeValue, Sort.by(Sort.Direction.DESC, "createdAt"))
            val pagedPosts = PageImpl(posts, pageRequest, posts.size.toLong())
            val postService = createPostService(
                postRepository = mock {
                    on { findPostsByFollowedUsers(currentUser, ACCEPTED, pageRequest) } doReturn pagedPosts
                },
                paginationProperties = mock { on { pageSize } doReturn pageSizeValue },
                postUriProviderImpl = mock {
                    on { getLikePostUri() } doReturn uri
                    on { toExternalUri(uri) } doReturn uri
                }
            )

            val latestPosts = postService.findLatestPosts(currentUser, pageIndexValue)

            assertThat(latestPosts).isEqualTo(
                LatestPostsDTO(
                    posts = postDTOs,
                    isLastPage = true
                )
            )
        }

        @Test
        fun `test pagination with empty list`() {
            val currentUser = "someUser"
            val pageSizeValue = 4
            val pageIndexValue = 2
            val posts = emptyList<Post>()
            val postResponseDTOS = emptyList<PostResponseDTO>()
            val pageRequest = PageRequest.of(pageIndexValue, pageSizeValue, Sort.by(Sort.Direction.DESC, "createdAt"))
            val pagedPosts = PageImpl(posts, pageRequest, 0)
            val postService = createPostService(
                postRepository = mock {
                    on { findPostsByFollowedUsers(currentUser, ACCEPTED, pageRequest) } doReturn pagedPosts
                },
                paginationProperties = mock { on { pageSize } doReturn pageSizeValue }
            )

            val latestPosts = postService.findLatestPosts(currentUser, pageIndexValue)

            assertThat(latestPosts).isEqualTo(
                LatestPostsDTO(
                    posts = postResponseDTOS,
                    isLastPage = true
                )
            )
        }
    }
    
    @Nested
    inner class LikePostTests {
        @Test
        fun `should call processor`() {
            val currentUserName = "SomeUser"
            val post = Post(
                title = "SomeTitle",
                content = "SomeContnet",
                user = stubUser()
            )
            val repository: PostRepository = mock {
                on { findPostById(post.id) } doReturn post
            }
            val postLikeProcessor: PostLikeProcessor = mock()
            val service = createPostService(
                postRepository = repository,
                postLikeProcessor = postLikeProcessor
            )
            
            service.likePost(currentUserName, post.id)
            
            assertAll(
                { verify(repository).findPostById(post.id) },
                { verify(postLikeProcessor).updateLike(currentUserName, post.user.username, post) }
            )
        }

        @Test
        fun `should throw error on missing post`() {
            val currentUserName = "SomeUser"
            val service = createPostService()

            val throwable = catchThrowable {
                service.likePost(currentUserName, 1)
            }

            assertThat(throwable).isInstanceOf(IllegalStateException::class.java).hasMessageContaining("Post not found")
        }
    }

    private fun createPostService(
        postRepository: PostRepository = mock(),
        paginationProperties: PaginationProperties = mock(),
        postLikeProcessor: PostLikeProcessor = mock(),
        postUriProviderImpl: PostUriProviderImpl = mock()
    ) = PostServiceImpl(
        postRepository = postRepository,
        paginationProperties = paginationProperties,
        postLikeProcessor = postLikeProcessor,
        postUriProviderImpl = postUriProviderImpl,
    )
}