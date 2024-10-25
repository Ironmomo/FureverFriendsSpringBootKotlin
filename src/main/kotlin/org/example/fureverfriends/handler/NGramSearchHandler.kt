package org.example.fureverfriends.handler

import org.example.fureverfriends.model.user.User
import org.example.fureverfriends.repository.user.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class NGramSearchHandler(
    private val userRepository: UserRepository
) {

   fun nGramSearch(searchPattern: String): Set<User> {
       val subStrings = splitString(searchPattern)
       var matchingUsersMap = mutableMapOf<String, Int>()
       val maxNumberOfMatchingUsers = 500
       val maxNumberOfMatchedUsers = 5
       val pageSize = 100
       subStrings.forEach { subString ->
           do {
               var currentIndex = 0
               val page = PageRequest.of(currentIndex, pageSize)
               val pageable = userRepository.findByUsernameContaining(subString, page)
               pageable.content.map(User::username).forEach { username ->
                   val appearance = matchingUsersMap.getOrDefault(username, 0) + 1
                   matchingUsersMap.put(username, appearance)
               }
               currentIndex += 1
           } while (pageable.hasNext())
           if (matchingUsersMap.size >= maxNumberOfMatchingUsers) {
               matchingUsersMap = matchingUsersMap.entries.sortedByDescending { it.value }.take(50).associate { (key, value) -> key to value }.toMutableMap()
           }
       }
       val foundUsernames = matchingUsersMap.entries.sortedByDescending { it.value }.map { it.key }.take(maxNumberOfMatchedUsers)
       return userRepository.findAllByUsernameIn(foundUsernames)
   }

    private fun splitString(input: String): List<String> {
        val chunks = mutableListOf<String>()
        var i = 0
        while (i < input.length) {
            if (input.length - i <= 2) {
                break
            } else {
                chunks.add(input.substring(i, i + 3))
                i += 3
            }
        }
        return chunks
    }

}