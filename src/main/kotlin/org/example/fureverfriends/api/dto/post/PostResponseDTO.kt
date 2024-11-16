package org.example.fureverfriends.api.dto.post

import org.example.fureverfriends.api.dto.user.UserDTO
import java.time.LocalDateTime

data class PostResponseDTO(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val likes: Int,
    val user: UserDTO
)
