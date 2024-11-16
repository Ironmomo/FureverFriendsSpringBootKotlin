package org.example.fureverfriends.api.uriprovider

import org.example.fureverfriends.config.url.UrlProperties
import org.springframework.stereotype.Component

abstract class UriProvider(
    private val subUri: String,
    private val urlProperties: UrlProperties
) {
    val baseUri: String
        get() = "/api/v1$subUri"

    fun toExternalUri(uri: String) = "${urlProperties.externalUrl}$uri"
}

@Component
class UserUriProviderImpl(urlProperties: UrlProperties) : UriProvider("/user", urlProperties) {
    fun getCreateUserUri(): String = "$baseUri/create"

    fun getSearchUserUri(searchString: String): String = "$baseUri/search/$searchString"
}
