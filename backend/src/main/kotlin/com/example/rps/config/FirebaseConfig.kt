package com.example.rps.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.io.File
import java.io.FileInputStream

@Configuration
class FirebaseConfig {

    private val logger = LoggerFactory.getLogger(FirebaseConfig::class.java)

    @Value("\${firebase.config-path:}")
    private val configPath: String = ""

    @Value("\${firebase.project-id:rps-game-firebase}")
    private val projectId: String = "rps-game-firebase"

    @PostConstruct
    fun initializeFirebase() {
        if (FirebaseApp.getApps().isNotEmpty()) {
            logger.info("Firebase application already initialized.")
            return
        }

        try {
            val options = if (configPath.isNotBlank() && File(configPath).exists()) {
                logger.info("Initializing Firebase with credentials from path: {}", configPath)
                val serviceAccount = FileInputStream(configPath)
                FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(projectId)
                    .build()
            } else {
                logger.info("Initializing Firebase with default project configuration: {}", projectId)
                FirebaseOptions.builder()
                    .setProjectId(projectId)
                    .build()
            }

            FirebaseApp.initializeApp(options)
            logger.info("Firebase successfully initialized.")
        } catch (e: Exception) {
            logger.warn("Firebase initialization warning (running in standalone mode without active cloud credentials): {}", e.message)
        }
    }
}
