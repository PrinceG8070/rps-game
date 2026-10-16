package com.example.rps.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import java.time.LocalDateTime

data class PlayMoveRequest(
    @field:NotBlank(message = "Move cannot be blank")
    @field:Pattern(
        regexp = "^(?i)(rock|paper|scissor|scissors)$",
        message = "Move must be 'rock', 'paper', or 'scissor'"
    )
    val playerMove: String = "",

    val username: String? = null
)

data class ScoreDto(
    val wins: Int,
    val losses: Int,
    val ties: Int,
    val totalGames: Int,
    val winRate: Double
)

data class PlayMoveResponse(
    val success: Boolean = true,
    val playerMove: String,
    val computerMove: String,
    val result: String, // "you won", "you lose", "Tie"
    val message: String,
    val score: ScoreDto
)

data class GameRoundDto(
    val id: Long,
    val playerMove: String,
    val computerMove: String,
    val result: String,
    val playedAt: LocalDateTime
)

data class ResetScoreRequest(
    val username: String? = null
)

data class LeaderboardEntryDto(
    val rank: Int,
    val username: String,
    val wins: Int,
    val losses: Int,
    val ties: Int,
    val totalGames: Int,
    val winRate: Double
)
