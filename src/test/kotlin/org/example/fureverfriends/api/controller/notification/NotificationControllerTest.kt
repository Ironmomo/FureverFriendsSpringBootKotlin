package org.example.fureverfriends.api.controller.notification

import org.example.fureverfriends.api.dto.notification.NotificationDTO
import org.example.fureverfriends.api.dto.notification.NotificationStatusDTO.NEW
import org.example.fureverfriends.api.dto.notification.NotificationTypeDTO.LikeNotification
import org.example.fureverfriends.processing.service.notification.NotificationService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @MockBean
    private lateinit var notificationService: NotificationService

    @Test
    fun `should return 403 on missing authentication`() {
        mockMvc.perform(get("/api/notification"))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(username = "someUser")
    fun `should return dto`() {
        val notificationDTO = NotificationDTO(
            payload = "somePayload", status = NEW, type = LikeNotification
        )
        whenever(notificationService.loadNotificationByUser("someUser")).thenReturn(listOf(notificationDTO))

        mockMvc.perform(get("/api/notification")
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].payload").value("somePayload"))
            .andExpect(jsonPath("$[0].status").value("NEW"))
            .andExpect(jsonPath("$[0].type").value("LikeNotification"))
        verify(notificationService).loadNotificationByUser("someUser")
    }
}