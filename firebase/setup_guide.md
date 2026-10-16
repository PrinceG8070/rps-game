# Firebase Integration Guide

This application includes built-in Firebase Admin SDK support in the Kotlin Spring Boot backend for:
- Firebase Cloud Messaging (FCM) push notifications when moves are played or tournaments conclude
- Firebase Authentication verification

## Backend Integration
The Kotlin backend has `com.google.firebase:firebase-admin:9.4.3` included in `backend/build.gradle.kts`.
The service is located at:
`backend/src/main/kotlin/com/example/rps/service/FirebaseService.kt`
And the controller is at:
`backend/src/main/kotlin/com/example/rps/controller/NotificationController.kt`

## How to activate Firebase:
1. Go to [Firebase Console](https://console.firebase.google.com/) and create a project.
2. Go to **Project Settings > Service accounts > Generate new private key**.
3. Save the downloaded JSON file as:
   `backend/src/main/resources/firebase-service-account.json`
4. Set the environment variable or `application.properties`:
   ```properties
   firebase.config-path=classpath:firebase-service-account.json
   firebase.project-id=your-firebase-project-id
   ```
5. Endpoints available:
   - `POST /api/notifications/fcm/token` (registers device token)
   - `POST /api/notifications/fcm/send` (dispatches notification)
