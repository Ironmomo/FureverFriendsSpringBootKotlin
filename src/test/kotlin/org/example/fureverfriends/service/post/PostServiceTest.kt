package org.example.fureverfriends.service.post

import org.assertj.core.api.Assertions.assertThat
import org.example.fureverfriends.config.post.PaginationProperties
import org.example.fureverfriends.dto.post.LatestPostsDTO
import org.example.fureverfriends.dto.post.PostResponseDTO
import org.example.fureverfriends.model.post.Post
import org.example.fureverfriends.model.userfollowing.UserRelationStatus.ACCEPTED
import org.example.fureverfriends.repository.post.PostRepository
import org.example.fureverfriends.stubs.stubPost
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import kotlin.test.Test

class PostServiceTest {

    @Nested
    inner class GetLatestPostsTests {
        @Test
        fun `test pagination page size with remaining posts`() {
            val currentUser = "someUser"
            val pageSizeValue = 4
            val pageIndexValue = 0
            val posts = listOf(stubPost(1), stubPost(2), stubPost(3), stubPost(4), stubPost(5))
            val postDTOs = posts.map { it.mapToDTO() }
            val pageRequest = PageRequest.of(pageIndexValue, pageSizeValue, Sort.by(Sort.Direction.DESC, "createdAt"))
            val pagedPosts = PageImpl(posts, pageRequest, posts.size.toLong())
            val postService = createPostService(
                postRepository = mock {
                    on { findPostsByFollowedUsers(currentUser, ACCEPTED, pageRequest) } doReturn pagedPosts
                },
                paginationProperties = mock { on { pageSize } doReturn pageSizeValue }
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
            val currentUser = "someUser"
            val pageSizeValue = 4
            val pageIndexValue = 0
            val posts = listOf(stubPost(1), stubPost(2), stubPost(3), stubPost(4))
            val postDTOs = posts.map { it.mapToDTO() }
            val pageRequest = PageRequest.of(pageIndexValue, pageSizeValue, Sort.by(Sort.Direction.DESC, "createdAt"))
            val pagedPosts = PageImpl(posts, pageRequest, posts.size.toLong())
            val postService = createPostService(
                postRepository = mock {
                    on { findPostsByFollowedUsers(currentUser, ACCEPTED, pageRequest) } doReturn pagedPosts
                },
                paginationProperties = mock { on { pageSize } doReturn pageSizeValue }
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

    private fun createPostService(
        postRepository: PostRepository = mock(),
        paginationProperties: PaginationProperties = mock()
    ) = PostService(
        postRepository = postRepository,
        paginationProperties = paginationProperties
    )
}