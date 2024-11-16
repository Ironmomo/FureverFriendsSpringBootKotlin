package org.example.fureverfriends.controller.userfollowing

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.fureverfriends.dto.error.ErrorResponseDTO
import org.example.fureverfriends.dto.user.FoundUsersDTO
import org.example.fureverfriends.dto.userfollowing.UserFollowingRequestDTO
import org.example.fureverfriends.service.userfollowing.UserFollowingService
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/relation")
@Tag(name = "UserFollowing Controller", description = "Operations related to user followings. Authentication is required.")
class UserFollowingController(
    private val userFollowingService: UserFollowingService
) {

    @GetMapping("/followers")
    @ResponseStatus(FOUND)
    fun getFollowers(
        principal: Principal,
        @RequestParam(value = "page", defaultValue = "0") pageIndex: Int
    ): FoundUsersDTO {
        return userFollowingService.findFollowers(principal.name, pageIndex)
    }

    @GetMapping("/followings")
    @ResponseStatus(FOUND)
    fun getFollowings(
        principal: Principal,
        @RequestParam(value = "page", defaultValue = "0") pageIndex: Int
    ): FoundUsersDTO {
        return userFollowingService.findFollowings(principal.name, pageIndex)
    }

    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "409",
                description = "User does not exist",
                content = [Content(schema = Schema(implementation = ErrorResponseDTO::class))]
            )
        ]
    )
    @PostMapping("/follow")
    @ResponseStatus(CREATED)
    fun followingRequest(
        principal: Principal,
        @RequestBody followingRequest: UserFollowingRequestDTO
    ) {
        userFollowingService.followingRequest(followerId = principal.name, followingId = followingRequest.userToFollow)
    }

    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "409",
                description = "There is no pending UserRelation",
                content = [Content(schema = Schema(implementation = ErrorResponseDTO::class))]
            )
        ]
    )
    @PostMapping("/accept")
    @ResponseStatus(CREATED)
    fun acceptingRequest(
        principal: Principal,
        @RequestBody followingRequest: UserFollowingRequestDTO
    ) {
        userFollowingService.acceptFollowingRequest(followerId = principal.name, followingId = followingRequest.userToFollow)
    }

    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "409",
                description = "There is no pending UserRelation",
                content = [Content(schema = Schema(implementation = ErrorResponseDTO::class))]
            )
        ]
    )
    @PostMapping("/reject")
    @ResponseStatus(OK)
    fun rejectingRequest(
        principal: Principal,
        @RequestBody followingRequest: UserFollowingRequestDTO
    ) {
        userFollowingService.rejectFollowingRequest(followerId = principal.name, followingId = followingRequest.userToFollow)
    }
}