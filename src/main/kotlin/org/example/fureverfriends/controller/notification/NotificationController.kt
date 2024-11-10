package org.example.fureverfriends.controller.notification

import org.example.fureverfriends.dto.notification.NotificationDTO
import org.example.fureverfriends.service.notification.NotificationService
import org.springframework.http.HttpStatus.FOUND
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/notification")
class NotificationController(
    private val notificationService: NotificationService
) {

    @GetMapping
    @ResponseStatus(FOUND)
    fun getNotificationsForUser(
        principal: Principal
    ): List<NotificationDTO> = notificationService.loadNotificationByUser(principal.name)

}