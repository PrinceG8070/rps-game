package com.example.rps.controller

import com.example.rps.dto.*
import com.example.rps.service.GameService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/game")
@Tag(name = "Rock Paper Scissors Game", description = "Endpoints for gameplay, moves, scores, and match histories")
class GameController(
    private val gameService: GameService
) {

    @PostMapping("/play")
    @Operation(summary = "Play a move", description = "Submits player move (rock, paper, scissor), plays against computer, updates score, and returns result")
    fun playMove(@Valid @RequestBody request: PlayMoveRequest): ResponseEntity<PlayMoveResponse> {
        val response = gameService.playMove(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/score")
    @Operation(summary = "Get user score", description = "Fetches the current win/loss/tie score for a given user")
    fun getScore(@RequestParam(defaultValue = "guest") username: String): ResponseEntity<ApiResponse<ScoreDto>> {
        val score = gameService.getScore(username)
        return ResponseEntity.ok(ApiResponse(data = score))
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset user score", description = "Resets win, loss, and tie counts to 0 for a user")
    fun resetScore(@RequestBody(required = false) request: ResetScoreRequest?): ResponseEntity<ApiResponse<ScoreDto>> {
        val username = request?.username?.ifBlank { "guest" } ?: "guest"
        val score = gameService.resetScore(username)
        return ResponseEntity.ok(ApiResponse(message = "Score reset successfully", data = score))
    }

    @GetMapping("/history")
    @Operation(summary = "Get game rounds history", description = "Retrieves paginated history of games played by a user")
    fun getHistory(
        @RequestParam(defaultValue = "guest") username: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<PagedResponse<GameRoundDto>>> {
        val history = gameService.getHistory(username, PageRequest.of(page, size))
        return ResponseEntity.ok(ApiResponse(data = history))
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Get game leaderboard", description = "Retrieves top players sorted by wins")
    fun getLeaderboard(
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<ApiResponse<List<LeaderboardEntryDto>>> {
        val leaderboard = gameService.getLeaderboard(limit)
        return ResponseEntity.ok(ApiResponse(data = leaderboard))
    }
}
