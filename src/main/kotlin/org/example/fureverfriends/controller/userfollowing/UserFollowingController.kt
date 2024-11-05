package org.example.fureverfriends.controller.userfollowing

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
class UserFollowingController(
    private val userFollowingService: UserFollowingService
) {

    @GetMapping("/followings")
    @ResponseStatus(FOUND)
    fun getFollowings(
        principal: Principal,
        @RequestParam(value = "page", defaultValue = "0") pageIndex: Int
    ): FoundUsersDTO {
        return userFollowingService.findFollowings(principal.name, pageIndex)
    }

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

    @PostMapping("/reject")
    @ResponseStatus(OK)
    fun rejectingRequest(
        principal: Principal,
        @RequestBody followingRequest: UserFollowingRequestDTO
    ) {
        userFollowingService.rejectFollowingRequest(followerId = principal.name, followingId = followingRequest.userToFollow)
    }
}