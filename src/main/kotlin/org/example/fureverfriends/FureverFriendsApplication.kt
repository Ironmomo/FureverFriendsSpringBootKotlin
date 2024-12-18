package org.example.fureverfriends

import org.example.fureverfriends.config.post.PaginationProperties
import org.example.fureverfriends.config.url.UrlProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(PaginationProperties::class, UrlProperties::class)
class FureverFriendsApplication

fun main(args: Array<String>) {
    runApplication<FureverFriendsApplication>(*args)
}
