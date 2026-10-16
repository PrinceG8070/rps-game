package com.example.rps

import com.example.rps.dto.LoginRequest
import com.example.rps.dto.RegisterRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application-test.properties"])
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @DisplayName("Should successfully register a new user")
    fun shouldRegisterSuccessfully() {
        val request = RegisterRequest(
            username = "testplayer1",
            password = "password123",
            email = "player1@example.com"
        )

        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.message") { value("Registration Successful") }
            jsonPath("$.user.username") { value("testplayer1") }
        }
    }

    @Test
    @DisplayName("Should fail when registering duplicate username")
    fun shouldFailDuplicateRegistration() {
        val request = RegisterRequest(
            username = "dupplayer",
            password = "password123"
        )

        // First registration
        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
        }

        // Duplicate registration
        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isConflict() }
            jsonPath("$.success") { value(false) }
        }
    }

    @Test
    @DisplayName("Should fail registration with blank fields")
    fun shouldFailRegistrationWithBlankFields() {
        val request = RegisterRequest(
            username = "",
            password = ""
        )

        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.success") { value(false) }
        }
    }

    @Test
    @DisplayName("Should authenticate valid user successfully")
    fun shouldLoginSuccessfully() {
        val reg = RegisterRequest(username = "loginUser", password = "secretPassword")
        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(reg)
        }

        val login = LoginRequest(username = "loginUser", password = "secretPassword")
        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(login)
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.message") { value("Login Successful") }
            jsonPath("$.token") { isNotEmpty() }
        }
    }

    @Test
    @DisplayName("Should reject invalid credentials")
    fun shouldRejectInvalidCredentials() {
        val login = LoginRequest(username = "nonExistentUser", password = "wrongPassword")
        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(login)
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.success") { value(false) }
        }
    }
}
