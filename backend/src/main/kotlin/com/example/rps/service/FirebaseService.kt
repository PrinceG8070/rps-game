package com.example.rps.service

import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FirebaseService {

    private val logger = LoggerFactory.getLogger(FirebaseService::class.java)

    fun sendPushNotification(token: String, title: String, body: String, data: Map<String, String> = emptyMap()): Boolean {
        if (FirebaseApp.getApps().isEmpty()) {
            logger.info("[Mock/FCM] Push Notification: Title='{}', Body='{}' to token='{}'", title, body, token)
            return true
        }

        return try {
            val messageBuilder = Message.builder()
                .setToken(token)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build()
                )

            data.forEach { (k, v) -> messageBuilder.putData(k, v) }

            val response = FirebaseMessaging.getInstance().send(messageBuilder.build())
            logger.info("Successfully sent FCM message: {}", response)
            true
        } catch (e: Exception) {
            logger.error("Error sending Firebase push notification: {}", e.message)
            false
        }
    }

    fun sendTopicNotification(topic: String, title: String, body: String): Boolean {
        if (FirebaseApp.getApps().isEmpty()) {
            logger.info("[Mock/FCM] Topic Notification to '{}': Title='{}', Body='{}'", topic, title, body)
            return true
        }

        return try {
            val message = Message.builder()
                .setTopic(topic)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build()
                )
                .build()

            val response = FirebaseMessaging.getInstance().send(message)
            logger.info("Successfully sent FCM topic message: {}", response)
            true
        } catch (e: Exception) {
            logger.error("Error sending Firebase topic notification: {}", e.message)
            false
        }
    }
}
