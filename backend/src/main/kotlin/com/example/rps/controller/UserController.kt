package com.example.rps.controller

import com.example.rps.dto.ApiResponse
import com.example.rps.dto.PagedResponse
import com.example.rps.dto.UserDto
import com.example.rps.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users Management", description = "Endpoints for managing user accounts and profiles")
class UserController(
    private val userService: UserService
) {

    @GetMapping
    @Operation(summary = "List users", description = "Retrieves a paginated and searchable list of registered users")
    fun getUsers(
        @RequestParam(required = false) search: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<PagedResponse<UserDto>>> {
        val users = userService.getUsers(search, PageRequest.of(page, size))
        return ResponseEntity.ok(ApiResponse(data = users))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves user details for a given user ID")
    fun getUserById(@PathVariable id: Long): ResponseEntity<ApiResponse<UserDto>> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(ApiResponse(data = user))
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username", description = "Retrieves user details by username")
    fun getUserByUsername(@PathVariable username: String): ResponseEntity<ApiResponse<UserDto>> {
        val user = userService.getUserByUsername(username)
        return ResponseEntity.ok(ApiResponse(data = user))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user account by ID")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<ApiResponse<Boolean>> {
        val deleted = userService.deleteUser(id)
        return ResponseEntity.ok(ApiResponse(message = "User deleted successfully", data = deleted))
    }
}
