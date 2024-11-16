package org.example.fureverfriends.api.controller.post

import org.example.fureverfriends.api.dto.post.LatestPostsDTO
import org.example.fureverfriends.processing.service.post.PostService
import org.example.fureverfriends.stubs.stubPostDTO
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.temporal.ChronoUnit.SECONDS
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @MockBean
    private lateinit var postService: PostService

    @Nested
    inner class LikePostTests {
        @Test
        fun `on not authenticated return 403`() {
            mockMvc.perform(post("/api/post/like").param("postId", "2"))
                .andExpect(status().isForbidden)
        }

        @Test
        @WithMockUser(username = "someUser")
        fun `should call service`() {
            val postId: Long = 2

            mockMvc.perform(post("/api/post/like").param("postId", postId.toString()))
            .andExpect(status().isAccepted)
            verify(postService).likePost("someUser", postId)
        }

        @Test
        @WithMockUser(username = "someUser")
        fun `on wrong parameter type should return 406`() {
            val invalidParameter = "invalid"

            mockMvc.perform(post("/api/post/like").param("postId", invalidParameter))
                .andExpect(status().isNotAcceptable)
        }

        @Test
        @WithMockUser(username = "someUser")
        fun `on missing parameter type should return 400`() {
            mockMvc.perform(post("/api/post/like"))
                .andExpect(status().isBadRequest)
        }
    }

    @Nested
    inner class GetLatestPostTests {
        @Test
        fun `on not authenticated return 403`() {
            mockMvc.perform(get("/api/post/latest").param("page", "0"))
                .andExpect(status().isForbidden)
        }

        @Test
        @WithMockUser(username = "someUser")
        fun `test getLatestPosts authenticated returns latest posts`() {
            val post1 = stubPostDTO(1)
            val post2 = stubPostDTO(2)
            val posts = listOf(post1, post2)
            val latestPostsDTO =
                LatestPostsDTO(posts = posts, isLastPage = false)

            whenever(postService.findLatestPosts("someUser", 0)).doReturn(latestPostsDTO)

            mockMvc.perform(get("/api/post/latest").param("page", "0")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.posts[0].id").value(post1.id))
                .andExpect(jsonPath("$.posts[0].title").value(post1.title))
                .andExpect(jsonPath("$.posts[0].content").value(post1.content))
                .andExpect(jsonPath("$.posts[0].createdAt").value(post1.createdAt.truncatedTo(SECONDS).toString()))
                .andExpect(jsonPath("$.posts[0].user.username").value(post1.user.username))
                .andExpect(jsonPath("$.posts[1].id").value(post2.id))
                .andExpect(jsonPath("$.posts[1].title").value(post2.title))
                .andExpect(jsonPath("$.posts[1].content").value(post2.content))
                .andExpect(jsonPath("$.posts[1].createdAt").value(post2.createdAt.truncatedTo(SECONDS).toString()))
                .andExpect(jsonPath("$.isLastPage").value(false))
        }
    }
}