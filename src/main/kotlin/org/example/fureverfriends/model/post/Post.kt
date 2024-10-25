package org.example.fureverfriends.model.post

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.example.fureverfriends.dto.post.PostResponseDTO
import org.example.fureverfriends.model.user.User
import java.time.LocalDateTime

@Entity
@Table(name = "posts")
data class Post(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val content: String,

    @Column(nullable = false, name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column
    val likes: Int = 0,

    @Column
    val dislikes: Int = 0,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User
) {
    fun mapToDTO() = PostResponseDTO(id, title, content, createdAt, likes, dislikes, user = user.mapToDTO())
}
