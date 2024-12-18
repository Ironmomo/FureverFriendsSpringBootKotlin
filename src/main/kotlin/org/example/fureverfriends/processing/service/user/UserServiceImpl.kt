package org.example.fureverfriends.processing.service.user

import org.example.fureverfriends.api.dto.actions.ActionDTO
import org.example.fureverfriends.api.dto.actions.UserAction.FOLLOW
import org.example.fureverfriends.api.dto.user.CreateUserRequestDTO
import org.example.fureverfriends.api.dto.user.FoundUsersDTO
import org.example.fureverfriends.api.uriprovider.UserFollowingUriProviderImpl
import org.example.fureverfriends.model.user.User
import org.example.fureverfriends.processing.processor.NGramSearchProcessor
import org.example.fureverfriends.repository.user.UserRepository
import org.example.fureverfriends.util.checkExpectNull
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder,
    private val nGramSearchProcessor: NGramSearchProcessor,
    private val userFollowingUriProviderImpl: UserFollowingUriProviderImpl
): UserService {

    override fun createUser(user: CreateUserRequestDTO) {
        checkPassword(user)
        val found = userRepository.findUserByUsername(user.username)
        checkExpectNull(found) { "User already exists" }
        val encodedUser = User(
            username = user.username,
            password = encoder.encode(user.password)
        )
        userRepository.save(encodedUser)
    }

    override fun searchForUser(searchString: String): FoundUsersDTO {
        val foundUsers = nGramSearchProcessor.nGramSearch(searchString)
        return FoundUsersDTO(foundUsers = foundUsers.map { it.mapToDTO()
            .copy(actions = listOf(
                ActionDTO(
                uri = userFollowingUriProviderImpl.toExternalUri(userFollowingUriProviderImpl.getFollowingRequestUri()),
                action = FOLLOW
            )))
        })
    }

    override fun getUserByUsername(username: String): User = userRepository.findUserByUsername(username) ?: throw UsernameNotFoundException("User $username not found")

    private fun checkPassword(user: CreateUserRequestDTO) {
        if (user.password.length < 8) throw IllegalArgumentException("Password must be at least 8 characters long")
    }
}