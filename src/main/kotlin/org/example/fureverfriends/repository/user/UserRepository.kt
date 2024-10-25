package org.example.fureverfriends.repository.user

import org.example.fureverfriends.model.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {
    fun findUserByUsername(name: String): User?

    fun findByUsernameContainingIgnoreCase(name: String, pageable: Pageable): Page<User>

    fun findAllByUsernameIn(usernames: List<String>): Set<User>
}