package org.example.fureverfriends.security.configs

import org.example.fureverfriends.repository.user.UserRepository
import org.example.fureverfriends.security.configs.jwt.JwtAuthenticationProvider
import org.example.fureverfriends.security.configs.jwt.JwtProperties
import org.example.fureverfriends.security.service.CustomUserDetailsService
import org.example.fureverfriends.security.service.TokenService
import org.springframework.context.annotation.Configuration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class Configuration {
    @Bean
    fun userDetailsService(userRepository: UserRepository): UserDetailsService = CustomUserDetailsService(userRepository)

    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(
        config: AuthenticationConfiguration,
        userRepository: UserRepository,
        tokenService: TokenService
    ): AuthenticationManager =
        ProviderManager(listOf(
            DaoAuthenticationProvider()
                .also {
                    it.setUserDetailsService(userDetailsService(userRepository))
                    it.setPasswordEncoder(encoder())
                },
            JwtAuthenticationProvider(
                userDetailsService = userDetailsService(userRepository),
                tokenService = tokenService
            )
        ))
}