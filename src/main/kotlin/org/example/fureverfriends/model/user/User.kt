package org.example.fureverfriends.model.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.example.fureverfriends.dto.user.UserDTO

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false)
    val password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role = Role.USER
) {
    fun mapToDTO() = UserDTO(username)
}

enum class Role {
    USER, ADMIN
}
