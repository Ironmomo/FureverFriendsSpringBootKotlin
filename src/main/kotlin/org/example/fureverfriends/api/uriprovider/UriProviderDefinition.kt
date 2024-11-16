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

@Component
class UserFollowingUriProviderImpl(urlProperties: UrlProperties) : UriProvider("/relation", urlProperties) {
    fun getFollowingRequestUri() = "$baseUri/follow"
    fun getFollowersUri(): String = "$baseUri/followers"
    fun getFollowingsUri(): String = "$baseUri/followings"
    fun getAcceptingRequestUri(): String = "$baseUri/accept"
    fun getRejectingRequestUri(): String = "$baseUri/reject"
}

@Component
class PostUriProviderImpl(urlProperties: UrlProperties) : UriProvider("/post", urlProperties) {
    fun getLatestPostsUri(): String = "$baseUri/latest"
    fun getLikePostUri(): String = "$baseUri/like"
}