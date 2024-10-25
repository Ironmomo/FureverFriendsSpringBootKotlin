package org.example.fureverfriends.service.user

import org.example.fureverfriends.dto.user.CreateUserRequestDTO
import org.example.fureverfriends.dto.user.FoundUsersDTO
import org.example.fureverfriends.dto.user.UserDTO
import org.example.fureverfriends.handler.NGramSearchHandler
import org.example.fureverfriends.model.user.User
import org.example.fureverfriends.repository.user.UserRepository
import org.example.fureverfriends.util.checkExpectNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder,
    private val nGramSearchHandler: NGramSearchHandler
) {
    fun createUser(user: CreateUserRequestDTO) {
        checkPassword(user)
        val found = userRepository.findUserByUsername(user.username)
        checkExpectNull(found) { "User already exists" }
        val encodedUser = User(
            username = user.username,
            password = encoder.encode(user.password)
        )
        userRepository.save(encodedUser)
    }

    fun searchForUser(searchString: String): FoundUsersDTO {
        val foundUsers = nGramSearchHandler.nGramSearch(searchString)
        return FoundUsersDTO(foundUsers = foundUsers.map { it.mapToUserDTO() })
    }

    fun findUserByUsername(username: String): User {
        val user = userRepository.findUserByUsername(username)
        checkNotNull(user) { "User doesn't exist" }
        return user
    }

    private fun checkPassword(user: CreateUserRequestDTO) {
        if (user.password.length < 8) throw IllegalArgumentException("Password must be at least 8 characters long")
    }

    private fun User.mapToUserDTO() = UserDTO(username = username)

}