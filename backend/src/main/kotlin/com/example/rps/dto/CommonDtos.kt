package com.example.rps.dto

import java.time.LocalDateTime

data class ApiResponse<T>(
    val success: Boolean = true,
    val message: String = "Operation successful",
    val data: T? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

data class ErrorResponse(
    val success: Boolean = false,
    val message: String,
    val status: Int,
    val errors: List<String> = emptyList(),
    val path: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

data class PagedResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val isLast: Boolean
)
