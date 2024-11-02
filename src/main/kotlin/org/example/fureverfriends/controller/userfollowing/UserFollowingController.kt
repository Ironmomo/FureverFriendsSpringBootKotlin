package org.example.fureverfriends.controller.userfollowing

import org.example.fureverfriends.dto.userfollowing.UserFollowingRequestDTO
import org.example.fureverfriends.service.userfollowing.UserFollowingService
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/relation")
class UserFollowingController(
    private val userFollowingService: UserFollowingService
) {

    @PostMapping("/follow")
    @ResponseStatus(CREATED)
    fun followingRequest(
        principal: Principal,
        @RequestBody followingRequest: UserFollowingRequestDTO
    ) {
        userFollowingService.followingRequest(followerId = principal.name, followingId = followingRequest.userToFollow)
    }

    @PostMapping("/accept")
    @ResponseStatus(CREATED)
    fun acceptingRequest(
        principal: Principal,
        @RequestBody followingRequest: UserFollowingRequestDTO
    ) {
        userFollowingService.acceptFollowingRequest(followerId = principal.name, followingId = followingRequest.userToFollow)
    }
}