package org.example.fureverfriends.api.controller.post

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.fureverfriends.api.dto.error.ErrorResponseDTO
import org.example.fureverfriends.api.dto.post.LatestPostsDTO
import org.example.fureverfriends.processing.service.post.PostService
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
@RequestMapping("/api/v1/post")
@Tag(name = "Post Controller", description = "Operations related to posts. Authentication is required")
class PostController(
    private val postService: PostService
) {

    @Operation(summary = "Fetches a list of recent posts from users the authenticated user follows, ordered by most recent first. Supports pagination for easier navigation through a large number of posts")
    @GetMapping("/latest", produces = ["application/json"])
    fun getLatestPosts(
        principal: Principal,
        @RequestParam(value = "page", defaultValue = "0") pageIndex: Int
    ): ResponseEntity<LatestPostsDTO> {
        val latestPosts = postService.findLatestPosts(principal.name, pageIndex)
        return ResponseEntity.ok(latestPosts)
    }

    @Operation(summary = "Likes a post from a user the authenticated user follows")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "409",
                description = "Post not found",
                content = [Content(schema = Schema(implementation = ErrorResponseDTO::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "User is not authorized to like this post because he is not following the author of the post",
                content = [Content(schema = Schema(implementation = ErrorResponseDTO::class))]
            )
        ]
    )
    @PostMapping("/like")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun likePost(
        principal: Principal,
        @RequestParam(value = "postId") postId: Long
    ) {
        postService.likePost(principal.name, postId)
    }
}