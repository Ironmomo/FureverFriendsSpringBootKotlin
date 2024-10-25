package org.example.fureverfriends.config.post

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("pagination")
data class PaginationProperties(
    val pageSize: Int
)
