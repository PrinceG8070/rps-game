package com.example.rps.controller

import com.example.rps.dto.ApiResponse
import com.example.rps.dto.AuthResponse
import com.example.rps.dto.LoginLogDto
import com.example.rps.dto.LoginRequest
import com.example.rps.dto.PagedResponse
import com.example.rps.dto.RegisterRequest
import com.example.rps.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "Authentication", description = "Endpoints for user registration, authentication, and login audit logs")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register", "/api/auth/register")
    @Operation(summary = "Register a new user", description = "Creates a new user profile and initializes default game score")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val response = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login", "/api/auth/login")
    @Operation(summary = "User login", description = "Authenticates user credentials and logs access attempt")
    fun login(
        @Valid @RequestBody request: LoginRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<AuthResponse> {
        val ipAddress = httpRequest.remoteAddr
        val response = authService.login(request, ipAddress)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/api/auth/logs")
    @Operation(summary = "Get login audit logs", description = "Returns paginated list of login attempts")
    fun getLogs(
        @RequestParam(required = false) username: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<PagedResponse<LoginLogDto>>> {
        val logs = authService.getLoginLogs(username, PageRequest.of(page, size))
        return ResponseEntity.ok(ApiResponse(data = logs))
    }
}
