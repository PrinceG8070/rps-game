package com.example.rps.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class RegisterRequest(
    @field:NotBlank(message = "Username cannot be blank")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String = "",

    @field:NotBlank(message = "Password cannot be blank")
    @field:Size(min = 4, max = 100, message = "Password must be at least 4 characters")
    val password: String = "",

    val email: String? = null
)

data class LoginRequest(
    @field:NotBlank(message = "Username cannot be blank")
    val username: String = "",

    @field:NotBlank(message = "Password cannot be blank")
    val password: String = ""
)

data class UserDto(
    val id: Long,
    val username: String,
    val email: String?,
    val role: String,
    val avatarUrl: String?,
    val createdAt: LocalDateTime
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val user: UserDto? = null
)

data class LoginLogDto(
    val id: Long,
    val username: String,
    val success: Boolean,
    val ipAddress: String?,
    val loginTime: LocalDateTime
)
