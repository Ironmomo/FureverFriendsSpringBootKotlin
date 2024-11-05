package org.example.fureverfriends.controller.post

import org.example.fureverfriends.dto.post.LatestPostsDTO
import org.example.fureverfriends.service.post.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/post")
class PostController(
    private val postService: PostService
) {
    @GetMapping("/latest")
    fun getLatestPosts(
        principal: Principal,
        @RequestParam(value = "page", defaultValue = "0") pageIndex: Int
    ): ResponseEntity<LatestPostsDTO> {
        val latestPosts = postService.findLatestPosts(principal.name, pageIndex)
        return ResponseEntity.ok(latestPosts)
    }

    @PostMapping("/like")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun likePost(
        principal: Principal,
        @RequestParam(value = "postId") postId: Long
    ) {
        postService.likePost(principal.name, postId)
    }
}