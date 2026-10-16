package com.example.rps.service

import com.example.rps.dto.*
import com.example.rps.exception.InvalidCredentialsException
import com.example.rps.exception.UserAlreadyExistsException
import com.example.rps.model.GameScore
import com.example.rps.model.LoginLog
import com.example.rps.model.User
import com.example.rps.repository.LoginLogRepository
import com.example.rps.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val loginLogRepository: LoginLogRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        val trimmedUsername = request.username.trim()

        if (userRepository.existsByUsername(trimmedUsername)) {
            throw UserAlreadyExistsException("Username '$trimmedUsername' is already taken")
        }

        val encodedPassword = passwordEncoder.encode(request.password)
        val user = User(
            username = trimmedUsername,
            password = encodedPassword,
            email = request.email?.trim()
        )

        val score = GameScore(
            user = user,
            wins = 0,
            losses = 0,
            ties = 0,
            totalGames = 0,
            updatedAt = LocalDateTime.now()
        )
        user.score = score

        val savedUser = userRepository.save(user)

        return AuthResponse(
            success = true,
            message = "Registration Successful",
            token = generateSessionToken(savedUser.username),
            user = toUserDto(savedUser)
        )
    }

    @Transactional
    fun login(request: LoginRequest, ipAddress: String? = null): AuthResponse {
        val trimmedUsername = request.username.trim()

        // Support fallback admin/1234 or database user
        val userOpt = userRepository.findByUsername(trimmedUsername)

        val isValid: Boolean
        val user: User?

        if (userOpt.isPresent) {
            user = userOpt.get()
            isValid = passwordEncoder.matches(request.password, user.password) ||
                    request.password == user.password // backwards compatibility if plain text in dev
        } else if (trimmedUsername == "admin" && request.password == "1234") {
            // Auto-provision admin user if logging in first time with default admin credentials
            val newAdmin = User(
                username = "admin",
                password = passwordEncoder.encode("1234"),
                role = "ROLE_ADMIN"
            )
            val score = GameScore(user = newAdmin, wins = 0, losses = 0, ties = 0, totalGames = 0, updatedAt = LocalDateTime.now())
            newAdmin.score = score
            user = userRepository.save(newAdmin)
            isValid = true
        } else {
            user = null
            isValid = false
        }

        // Record login audit log
        val log = LoginLog(
            username = trimmedUsername,
            success = isValid,
            ipAddress = ipAddress,
            loginTime = LocalDateTime.now(),
            user = user
        )
        loginLogRepository.save(log)

        if (!isValid) {
            throw InvalidCredentialsException("Invalid Username or Password")
        }

        return AuthResponse(
            success = true,
            message = "Login Successful",
            token = generateSessionToken(trimmedUsername),
            user = user?.let { toUserDto(it) }
        )
    }

    @Transactional(readOnly = true)
    fun getLoginLogs(username: String?, pageable: Pageable): PagedResponse<LoginLogDto> {
        val page = if (username.isNullOrBlank()) {
            loginLogRepository.findAllByOrderByLoginTimeDesc(pageable)
        } else {
            loginLogRepository.findByUsernameOrderByLoginTimeDesc(username, pageable)
        }

        val dtoList = page.content.map {
            LoginLogDto(
                id = it.id ?: 0L,
                username = it.username,
                success = it.success,
                ipAddress = it.ipAddress,
                loginTime = it.loginTime
            )
        }

        return PagedResponse(
            content = dtoList,
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            isLast = page.isLast
        )
    }

    private fun generateSessionToken(username: String): String {
        return "rps_token_" + UUID.randomUUID().toString().replace("-", "") + "_" + username
    }

    private fun toUserDto(user: User): UserDto {
        return UserDto(
            id = user.id ?: 0L,
            username = user.username,
            email = user.email,
            role = user.role,
            avatarUrl = user.avatarUrl,
            createdAt = user.createdAt
        )
    }
}
