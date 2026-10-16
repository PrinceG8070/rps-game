package com.example.rps.controller

import com.example.rps.dto.ApiResponse
import com.example.rps.service.FirebaseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class PushNotificationRequest(
    val token: String? = null,
    val topic: String? = null,
    val title: String,
    val body: String,
    val data: Map<String, String> = emptyMap()
)

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Push Notifications (Firebase)", description = "Endpoints for dispatching FCM notifications to mobile / web clients")
class NotificationController(
    private val firebaseService: FirebaseService
) {

    @PostMapping("/send")
    @Operation(summary = "Send FCM notification", description = "Sends a push notification to a device token or topic via Firebase Cloud Messaging")
    fun sendNotification(@RequestBody request: PushNotificationRequest): ResponseEntity<ApiResponse<Boolean>> {
        val success = if (!request.token.isNullOrBlank()) {
            firebaseService.sendPushNotification(request.token, request.title, request.body, request.data)
        } else if (!request.topic.isNullOrBlank()) {
            firebaseService.sendTopicNotification(request.topic, request.title, request.body)
        } else {
            firebaseService.sendTopicNotification("general", request.title, request.body)
        }

        return ResponseEntity.ok(ApiResponse(message = "Notification dispatched", data = success))
    }
}
