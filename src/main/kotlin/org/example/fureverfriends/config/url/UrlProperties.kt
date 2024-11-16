package org.example.fureverfriends.config.url

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("url")
data class UrlProperties(
    val externalUrl: String
)