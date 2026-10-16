package com.example.rps.service

import com.example.rps.dto.PagedResponse
import com.example.rps.dto.UserDto
import com.example.rps.exception.ResourceNotFoundException
import com.example.rps.model.User
import com.example.rps.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun getUsers(search: String?, pageable: Pageable): PagedResponse<UserDto> {
        val page = if (search.isNullOrBlank()) {
            userRepository.findAll(pageable)
        } else {
            userRepository.findByUsernameContainingIgnoreCase(search.trim(), pageable)
        }

        val dtoList = page.content.map { toUserDto(it) }
        return PagedResponse(
            content = dtoList,
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            isLast = page.isLast
        )
    }

    @Transactional(readOnly = true)
    fun getUserById(id: Long): UserDto {
        val user = userRepository.findById(id).orElseThrow {
            ResourceNotFoundException("User with id $id not found")
        }
        return toUserDto(user)
    }

    @Transactional(readOnly = true)
    fun getUserByUsername(username: String): UserDto {
        val user = userRepository.findByUsername(username).orElseThrow {
            ResourceNotFoundException("User with username '$username' not found")
        }
        return toUserDto(user)
    }

    @Transactional
    fun deleteUser(id: Long): Boolean {
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException("User with id $id not found")
        }
        userRepository.deleteById(id)
        return true
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
