package org.example.fureverfriends.controller.userfollowing

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.fureverfriends.dto.user.FoundUsersDTO
import org.example.fureverfriends.dto.userfollowing.UserFollowingRequestDTO
import org.example.fureverfriends.service.userfollowing.UserFollowingService
import org.example.fureverfriends.stubs.stubUserDTO
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class UserFollowingControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @MockBean
    private lateinit var userFollowingService: UserFollowingService
    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Nested
    inner class GetFollowerTests {
        @Test
        fun `should return 403 on missing authentication`() {
            mockMvc.perform(
                get("/api/relation/followers").param("page", "0"))
                .andExpect(status().isForbidden)
        }

        @Test
        @WithMockUser(username = "SomeUser")
        fun `on success`() {
            val dto = FoundUsersDTO(
                foundUsers = listOf(stubUserDTO()),
                isLastPage = true
            )

            whenever(userFollowingService.findFollowers("SomeUser", 0)).doReturn(dto)
            mockMvc.perform(get("/api/relation/followers").accept(APPLICATION_JSON))
                .andExpect(status().isFound)
                .andExpect(jsonPath("$.foundUsers[0].username").value(dto.foundUsers.first().username))
                .andExpect(jsonPath("$.isLastPage").value(true))
        }

        @Test
        @WithMockUser(username = "SomeUser")
        fun `on invalid parameter`() {
            mockMvc.perform(get("/api/relation/followers").param("page", "notANumber").accept(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable)
        }
    }

    @Nested
    inner class GetFollowingsTests {
        @Test
        fun `should return 403 on missing authentication`() {
            mockMvc.perform(
                get("/api/relation/followings").param("page", "0"))
                .andExpect(status().isForbidden)
        }

        @Test
        @WithMockUser(username = "SomeUser")
        fun `on success`() {
            val dto = FoundUsersDTO(
                foundUsers = listOf(stubUserDTO()),
                isLastPage = true
            )

            whenever(userFollowingService.findFollowings("SomeUser", 0)).doReturn(dto)
            mockMvc.perform(get("/api/relation/followings").accept(APPLICATION_JSON))
                .andExpect(status().isFound)
                .andExpect(jsonPath("$.foundUsers[0].username").value(dto.foundUsers.first().username))
                .andExpect(jsonPath("$.isLastPage").value(true))
        }

        @Test
        @WithMockUser(username = "SomeUser")
        fun `on invalid parameter`() {
            mockMvc.perform(get("/api/relation/followings").param("page", "notANumber").accept(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable)
        }
    }

    @Nested
    inner class FollowingRequestTests {
        @Test
        @WithMockUser(username = "SomeUser")
        fun `on success`() {
            val dto = UserFollowingRequestDTO("ToFollow")
            val requestBody = objectMapper.writeValueAsString(dto)

            mockMvc.perform(
                post("/api/relation/follow")
                    .contentType(APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isCreated)
            verify(userFollowingService).followingRequest(followerId = "SomeUser", followingId = "ToFollow")
        }

        @Test
        fun `should return 403 on missing authentication`() {
            val dto = UserFollowingRequestDTO("ToFollow")
            val requestBody = objectMapper.writeValueAsString(dto)

            mockMvc.perform(
                post("/api/relation/follow")
                    .contentType(APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isForbidden)
        }
    }

    @Nested
    inner class AcceptFollowingRequestTests {
        @Test
        @WithMockUser(username = "SomeUser")
        fun `on success`() {
            val dto = UserFollowingRequestDTO("ToFollow")
            val requestBody = objectMapper.writeValueAsString(dto)

            mockMvc.perform(
                post("/api/relation/accept")
                    .contentType(APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isCreated)
            verify(userFollowingService).acceptFollowingRequest(followerId = "SomeUser", followingId = "ToFollow")
        }

        @Test
        fun `should return 403 on missing authentication`() {
            val dto = UserFollowingRequestDTO("ToFollow")
            val requestBody = objectMapper.writeValueAsString(dto)

            mockMvc.perform(
                post("/api/relation/accept")
                    .contentType(APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isForbidden)
        }
    }

    @Nested
    inner class RejectFollowingRequestTests {
        @Test
        @WithMockUser(username = "SomeUser")
        fun `on success`() {
            val dto = UserFollowingRequestDTO("ToFollow")
            val requestBody = objectMapper.writeValueAsString(dto)

            mockMvc.perform(
                post("/api/relation/reject")
                    .contentType(APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isOk)
            verify(userFollowingService).rejectFollowingRequest(followerId = "SomeUser", followingId = "ToFollow")
        }

        @Test
        fun `should return 403 on missing authentication`() {
            val dto = UserFollowingRequestDTO("ToFollow")
            val requestBody = objectMapper.writeValueAsString(dto)

            mockMvc.perform(
                post("/api/relation/reject")
                    .contentType(APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isForbidden)
        }
    }
}