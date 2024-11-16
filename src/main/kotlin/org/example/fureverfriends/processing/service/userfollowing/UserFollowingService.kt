package org.example.fureverfriends.processing.service.userfollowing

import org.example.fureverfriends.api.dto.user.FoundUsersDTO

interface UserFollowingService {

    fun followingRequest(followerId: String, followingId: String)

    fun acceptFollowingRequest(followerId: String, followingId: String)

    fun rejectFollowingRequest(followerId: String, followingId: String)

    fun findFollowings(currentUsername: String, index: Int): org.example.fureverfriends.api.dto.user.FoundUsersDTO

    fun findFollowers(currentUsername: String, index: Int): org.example.fureverfriends.api.dto.user.FoundUsersDTO
}