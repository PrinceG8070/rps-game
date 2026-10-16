package com.example.rps.service

import com.example.rps.dto.*
import com.example.rps.exception.ResourceNotFoundException
import com.example.rps.model.GameRound
import com.example.rps.model.GameScore
import com.example.rps.model.User
import com.example.rps.repository.GameRoundRepository
import com.example.rps.repository.GameScoreRepository
import com.example.rps.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class GameService(
    private val userRepository: UserRepository,
    private val gameScoreRepository: GameScoreRepository,
    private val gameRoundRepository: GameRoundRepository,
    private val firebaseService: FirebaseService
) {

    private val validMoves = listOf("rock", "paper", "scissor")

    @Transactional
    fun playMove(request: PlayMoveRequest): PlayMoveResponse {
        var playerMove = request.playerMove.trim().lowercase()
        if (playerMove == "scissors") {
            playerMove = "scissor"
        }

        // Generate computer move
        val computerMove = validMoves[Random.nextInt(validMoves.size)]

        // Determine result matching existing game rules
        val result = when {
            playerMove == computerMove -> "Tie"
            (playerMove == "rock" && computerMove == "scissor") ||
            (playerMove == "scissor" && computerMove == "paper") ||
            (playerMove == "paper" && computerMove == "rock") -> "you won"
            else -> "you lose"
        }

        val username = request.username?.trim()?.ifBlank { null } ?: "guest"
        val user = getOrCreateUser(username)

        var score = gameScoreRepository.findByUser(user).orElseGet {
            val newScore = GameScore(user = user)
            gameScoreRepository.save(newScore)
        }

        when (result) {
            "you won" -> score.wins += 1
            "you lose" -> score.losses += 1
            else -> score.ties += 1
        }
        score.totalGames = score.wins + score.losses + score.ties
        score.updatedAt = LocalDateTime.now()
        score = gameScoreRepository.save(score)

        // Save round history
        val round = GameRound(
            user = user,
            playerMove = playerMove,
            computerMove = computerMove,
            result = result,
            playedAt = LocalDateTime.now()
        )
        gameRoundRepository.save(round)

        // Firebase Push notification on victory milestones
        if (score.wins > 0 && score.wins % 5 == 0 && result == "you won") {
            firebaseService.sendTopicNotification(
                topic = "game-milestones",
                title = "🎉 Achievement Unlocked!",
                body = "Player $username just hit ${score.wins} wins in Rock Paper Scissors!"
            )
        }

        val message = when (result) {
            "you won" -> "You Won!"
            "you lose" -> "You Lose"
            else -> "It's a Tie"
        }

        return PlayMoveResponse(
            success = true,
            playerMove = playerMove,
            computerMove = computerMove,
            result = result,
            message = message,
            score = toScoreDto(score)
        )
    }

    @Transactional(readOnly = true)
    fun getScore(username: String): ScoreDto {
        val user = getOrCreateUser(username)
        val score = gameScoreRepository.findByUser(user).orElseGet {
            GameScore(user = user)
        }
        return toScoreDto(score)
    }

    @Transactional
    fun resetScore(username: String): ScoreDto {
        val user = getOrCreateUser(username)
        val score = gameScoreRepository.findByUser(user).orElseGet {
            GameScore(user = user)
        }
        score.wins = 0
        score.losses = 0
        score.ties = 0
        score.totalGames = 0
        score.updatedAt = LocalDateTime.now()
        val saved = gameScoreRepository.save(score)
        return toScoreDto(saved)
    }

    @Transactional(readOnly = true)
    fun getHistory(username: String, pageable: Pageable): PagedResponse<GameRoundDto> {
        val page = gameRoundRepository.findByUserUsernameOrderByPlayedAtDesc(username, pageable)
        val dtoList = page.content.map {
            GameRoundDto(
                id = it.id ?: 0L,
                playerMove = it.playerMove,
                computerMove = it.computerMove,
                result = it.result,
                playedAt = it.playedAt
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

    @Transactional(readOnly = true)
    fun getLeaderboard(limit: Int = 10): List<LeaderboardEntryDto> {
        val scores = gameScoreRepository.findTopLeaderboard(PageRequest.of(0, limit))
        return scores.mapIndexed { index, sc ->
            val total = sc.totalGames
            val winRate = if (total > 0) (sc.wins.toDouble() / total) * 100.0 else 0.0
            LeaderboardEntryDto(
                rank = index + 1,
                username = sc.user.username,
                wins = sc.wins,
                losses = sc.losses,
                ties = sc.ties,
                totalGames = total,
                winRate = Math.round(winRate * 100.0) / 100.0
            )
        }
    }

    private fun getOrCreateUser(username: String): User {
        return userRepository.findByUsername(username).orElseGet {
            val newUser = User(
                username = username,
                password = "{noop}tempPassword"
            )
            val score = GameScore(user = newUser)
            newUser.score = score
            userRepository.save(newUser)
        }
    }

    private fun toScoreDto(score: GameScore): ScoreDto {
        val total = score.totalGames
        val winRate = if (total > 0) (score.wins.toDouble() / total) * 100.0 else 0.0
        return ScoreDto(
            wins = score.wins,
            losses = score.losses,
            ties = score.ties,
            totalGames = total,
            winRate = Math.round(winRate * 100.0) / 100.0
        )
    }
}
