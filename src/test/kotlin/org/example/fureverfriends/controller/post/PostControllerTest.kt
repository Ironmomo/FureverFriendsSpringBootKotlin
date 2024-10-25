package org.example.fureverfriends.controller.post

import org.example.fureverfriends.dto.post.LatestPostsDTO
import org.example.fureverfriends.service.post.PostService
import org.example.fureverfriends.stubs.stubPostDTO
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @MockBean
    private lateinit var postService: PostService

    @Nested
    inner class GetLatestPostTests {
        @Test
        fun `test getLatestPosts not authenticated`() {
            val post1 = stubPostDTO(1)
            val post2 = stubPostDTO(2)
            val posts = listOf(post1, post2)
            val latestPostsDTO = LatestPostsDTO(posts = posts, isLastPage = false)

            whenever(postService.findLatestPosts(0)).doReturn(latestPostsDTO)

            mockMvc.perform(get("/api/post/latest").param("page", "0"))
                .andExpect(status().isForbidden)
        }

        @Test
        @WithMockUser
        fun `test getLatestPosts authenticated returns latest posts`() {
            val post1 = stubPostDTO(1)
            val post2 = stubPostDTO(2)
            val posts = listOf(post1, post2)
            val latestPostsDTO = LatestPostsDTO(posts = posts, isLastPage = false)

            whenever(postService.findLatestPosts(0)).doReturn(latestPostsDTO)

            mockMvc.perform(get("/api/post/latest").param("page", "0"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.posts[0].id").value(post1.id))
                .andExpect(jsonPath("$.posts[0].title").value(post1.title))
                .andExpect(jsonPath("$.posts[0].content").value(post1.content))
                .andExpect(jsonPath("$.posts[0].createdAt").value(post1.createdAt.toString()))
                .andExpect(jsonPath("$.posts[0].user.username").value(post1.user.username))
                .andExpect(jsonPath("$.posts[1].id").value(post2.id))
                .andExpect(jsonPath("$.posts[1].title").value(post2.title))
                .andExpect(jsonPath("$.posts[1].content").value(post2.content))
                .andExpect(jsonPath("$.posts[1].createdAt").value(post2.createdAt.toString()))
                .andExpect(jsonPath("$.isLastPage").value(false))
        }
    }
}