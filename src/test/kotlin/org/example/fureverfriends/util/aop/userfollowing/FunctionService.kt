package org.example.fureverfriends.util.aop.userfollowing

import org.springframework.stereotype.Component

@Component
class FunctionService {
    @CheckFollower
    fun returnNumberTwoArg(followerId: String, followingId: String): Int {
        return 5
    }

    @CheckFollower
    fun returnNumberThreeArg(followerId: String, followingId: String, toReturn: Int): Int {
        return toReturn
    }
}