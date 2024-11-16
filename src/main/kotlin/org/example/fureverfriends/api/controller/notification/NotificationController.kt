package org.example.fureverfriends.api.controller.notification

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.fureverfriends.api.dto.notification.NotificationDTO
import org.example.fureverfriends.processing.service.notification.NotificationService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/notification")
@Tag(name = "Notification Controller", description = "Operations related to notifications. Authentication is required")
class NotificationController(
    private val notificationService: NotificationService
) {

    @Operation(summary = "Get unresolved notifications for the authenticated user")
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getNotificationsForUser(
        principal: Principal
    ): List<NotificationDTO> = notificationService.loadNotificationByUser(principal.name)

}