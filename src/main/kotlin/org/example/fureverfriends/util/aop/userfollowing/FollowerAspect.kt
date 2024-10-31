package org.example.fureverfriends.util.aop.userfollowing

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.example.fureverfriends.model.userfollowing.UserRelationStatus.ACCEPTED
import org.example.fureverfriends.repository.userfollowing.UserFollowingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Aspect
@Component
class FollowerAspect @Autowired constructor(private val userFollowingRepository: UserFollowingRepository) {

    @Around("@annotation(CheckFollower) && args(followerId, followingId, ..)")
    fun checkFollower(joinPoint: ProceedingJoinPoint, followerId: String, followingId: String): Any? {
        if (userFollowingRepository.findUserFollowingByIdFollowerAndIdFollowingAndStatus(followerId, followingId, ACCEPTED) == null) {
            throw IllegalAccessException("User $followerId is not a follower of user $followingId")
        }
        return joinPoint.proceed()
    }
}