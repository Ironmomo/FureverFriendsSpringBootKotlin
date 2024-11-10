package org.example.fureverfriends.service.post

import org.example.fureverfriends.dto.post.LatestPostsDTO

interface PostService {

    fun findLatestPosts(currentUsername: String, index: Int): LatestPostsDTO

    fun likePost(currentUsername: String, postId: Long)
}