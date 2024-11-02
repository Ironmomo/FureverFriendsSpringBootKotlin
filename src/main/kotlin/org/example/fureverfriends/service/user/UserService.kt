package org.example.fureverfriends.service.user

import org.example.fureverfriends.dto.user.CreateUserRequestDTO
import org.example.fureverfriends.dto.user.FoundUsersDTO
import org.example.fureverfriends.model.user.User
import org.example.fureverfriends.processor.NGramSearchProcessor
import org.example.fureverfriends.repository.user.UserRepository
import org.example.fureverfriends.util.checkExpectNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder,
    private val nGramSearchProcessor: NGramSearchProcessor
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
        val foundUsers = nGramSearchProcessor.nGramSearch(searchString)
        return FoundUsersDTO(foundUsers = foundUsers.map { it.mapToDTO() })
    }

    private fun checkPassword(user: CreateUserRequestDTO) {
        if (user.password.length < 8) throw IllegalArgumentException("Password must be at least 8 characters long")
    }
}