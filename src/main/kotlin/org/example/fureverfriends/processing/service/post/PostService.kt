package org.example.fureverfriends.processing.service.post

import org.example.fureverfriends.api.dto.post.LatestPostsDTO

interface PostService {

    fun findLatestPosts(currentUsername: String, index: Int): LatestPostsDTO

    fun likePost(currentUsername: String, postId: Long)
}