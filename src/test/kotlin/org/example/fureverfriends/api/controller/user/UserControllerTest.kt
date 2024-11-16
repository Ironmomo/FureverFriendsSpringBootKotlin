package org.example.fureverfriends.api.controller.user

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.fureverfriends.api.dto.user.CreateUserRequestDTO
import org.example.fureverfriends.api.dto.user.FoundUsersDTO
import org.example.fureverfriends.api.dto.user.UserDTO
import org.example.fureverfriends.processing.service.user.UserService
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
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
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @MockBean
    private lateinit var userService: UserService
    @Autowired
    lateinit var objectMapper: ObjectMapper

     @Nested
     inner class SearchUsersTests {
         @Test
         @WithMockUser
         fun `should return found users`() {
             val searchString = "something"
             val founUsersDTO = org.example.fureverfriends.api.dto.user.FoundUsersDTO(
                 foundUsers = listOf(org.example.fureverfriends.api.dto.user.UserDTO("someUser"))
             )

             whenever(userService.searchForUser(searchString)).doReturn(founUsersDTO)

             mockMvc.perform(
                 get("/api/user/search/$searchString")
             ).andExpect(status().isFound)
                 .andExpect(jsonPath("$.foundUsers[0].username").value("someUser"))
                 .andExpect(jsonPath("$.foundUsers[1]").doesNotExist())
                 .andExpect(jsonPath("$.foundUsers").isArray)
             verify(userService).searchForUser(searchString)
         }

         @Test
         fun `should return 403 on missing authentication`() {
             val searchString = "something"

             mockMvc.perform(
                 get("/api/user/search/$searchString")
             ).andExpect(status().isForbidden)
         }
     }

    @Nested
    inner class CreateUserTests {
        @Test
        fun `should create new user and return ok`() {
            val createUserRequestDTO = org.example.fureverfriends.api.dto.user.CreateUserRequestDTO(
                username = "fancyUser", password = "password"
            )
            val requestBody = objectMapper.writeValueAsString(createUserRequestDTO)

            mockMvc.perform(
                post("/api/user/create")
                .contentType(APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated)
            verify(userService).createUser(createUserRequestDTO)
        }

        @Test
        fun `should return 409 on user existing`() {
            val createUserRequestDTO = org.example.fureverfriends.api.dto.user.CreateUserRequestDTO(
                username = "fancyUser", password = "soSecret"
            )
            val requestBody = objectMapper.writeValueAsString(createUserRequestDTO)
            whenever(userService.createUser(createUserRequestDTO)).doThrow(IllegalStateException("user exists"))

            mockMvc.perform(
                post("/api/user/create")
                    .contentType(APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isConflict)
        }

        @Test
        fun `should return 406 on user existing`() {
            val createUserRequestDTO = org.example.fureverfriends.api.dto.user.CreateUserRequestDTO(
                username = "fancyUser", password = "soSecret"
            )
            val requestBody = objectMapper.writeValueAsString(createUserRequestDTO)
            whenever(userService.createUser(createUserRequestDTO)).doThrow(IllegalArgumentException("password not valid"))

            mockMvc.perform(
                post("/api/user/create")
                    .contentType(APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isNotAcceptable)
        }
    }

}