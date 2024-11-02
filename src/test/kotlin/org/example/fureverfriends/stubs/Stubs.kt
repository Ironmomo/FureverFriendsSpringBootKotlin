package org.example.fureverfriends.stubs

import org.example.fureverfriends.dto.post.PostResponseDTO
import org.example.fureverfriends.dto.user.CreateUserRequestDTO
import org.example.fureverfriends.dto.user.UserDTO
import org.example.fureverfriends.model.post.Post
import org.example.fureverfriends.model.user.Role.USER
import org.example.fureverfriends.model.user.User
import org.example.fureverfriends.security.configs.jwt.JwtProperties
import org.example.fureverfriends.security.dto.AuthenticationRequest
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import org.springframework.security.core.userdetails.User as UserDetailsImpl

fun stubUser(id: Int = 1): User = User(
    username = "username $id",
    password = "password $id",
    role = USER
)

fun stubUserDTO(id: Int = 1): UserDTO = UserDTO(
    username = "username $id"
)

fun stubPost(id: Int = 1): Post = Post(
    id = id.toLong(),
    title = "title $id",
    content = "content $id",
    createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    likes = 1,
    user = stubUser()
)

fun stubAuthenticationRequest(): AuthenticationRequest = AuthenticationRequest(
    username = "username",
    password = "password"
)

fun stubUserDetails(): UserDetails = UserDetailsImpl
    .builder()
    .username("username")
    .password("password")
    .roles(USER.name)
    .build()

fun stubJwtProperties(): JwtProperties = JwtProperties(
    key = "hmac-very-secret-key-to-use-for-stub",
    accessTokenExpiration = 3600000,
    refreshTokenExpiration = 86400000
)

fun stubCreateUserRequestDTO(): CreateUserRequestDTO = CreateUserRequestDTO(
    username = "username",
    password = "password"
)

fun stubPostDTO(id: Int = 1): PostResponseDTO = PostResponseDTO(
    id = id.toLong(),
    title = "title $id",
    content = "content $id",
    createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    likes = 1,
    user = stubUserDTO()
)