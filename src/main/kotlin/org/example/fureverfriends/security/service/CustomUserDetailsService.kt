package org.example.fureverfriends.security.service

import org.example.fureverfriends.model.user.User
import org.example.fureverfriends.repository.user.UserRepository
import org.springframework.security.core.userdetails.User as UserDetailsImpl
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails =
    userRepository.findUserByUsername(username)
    ?.toUserDetails()
    ?: throw UsernameNotFoundException("Username $username not found")

    private fun User.toUserDetails() = UserDetailsImpl
        .builder()
        .username(username)
        .password(password)
        .roles(role.name)
        .build()
}


