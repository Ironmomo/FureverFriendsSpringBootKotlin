package org.example.fureverfriends.model

import org.assertj.core.api.Assertions.assertThat
import org.example.fureverfriends.dto.post.PostResponseDTO
import org.example.fureverfriends.dto.user.UserDTO
import org.example.fureverfriends.model.post.Post
import org.example.fureverfriends.model.user.Role.ADMIN
import org.example.fureverfriends.model.user.User
import org.example.fureverfriends.stubs.stubUser
import org.junit.jupiter.api.Nested
import java.time.LocalDateTime
import kotlin.test.Test

class ModelTest {

    @Nested
    inner class MapPostTests {
        @Test
        fun `should map Post to DTO`() {
            val createdAt = LocalDateTime.now()
            val user = stubUser()
            val post = Post(
                id = 0,
                title = "some title",
                content = "some content",
                createdAt = createdAt,
                likes = 1,
                user = user
            )

            val mapedPostDTO = post.mapToDTO()

            assertThat(mapedPostDTO).isEqualTo(
                PostResponseDTO(
                id = 0,
                title = "some title",
                content = "some content",
                createdAt = createdAt,
                likes = 1,
                user = UserDTO(user.username)
            )
            )
        }
    }

    @Nested
    inner class MapUserTests {
        @Test
        fun `should map User to DTO`() {
            val user = User(
                username = "fancy user", password = "secret Password", role = ADMIN
            )

            val mapedUserDTO = user.mapToDTO()

            assertThat(mapedUserDTO).isEqualTo(UserDTO(user.username))
        }
    }
}