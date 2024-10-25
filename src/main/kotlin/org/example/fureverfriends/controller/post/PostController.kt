package org.example.fureverfriends.controller.post

import org.example.fureverfriends.dto.post.LatestPostsDTO
import org.example.fureverfriends.service.post.PostService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/post")
class PostController(
    private val postService: PostService
) {
    @GetMapping("/latest")
    fun getLatestPosts(
        @RequestParam(value = "page", defaultValue = "0") pageIndex: Int
    ): ResponseEntity<LatestPostsDTO> {
        val latestPosts = postService.findLatestPosts(pageIndex)
        return ResponseEntity.ok(latestPosts)
    }
}