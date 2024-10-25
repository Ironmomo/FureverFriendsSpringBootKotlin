package org.example.fureverfriends.dto.post

import org.example.fureverfriends.dto.user.UserDTO
import java.time.LocalDateTime

data class PostResponseDTO(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val likes: Int,
    val dislikes: Int,
    val user: UserDTO
)
