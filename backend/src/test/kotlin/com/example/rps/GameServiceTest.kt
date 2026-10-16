package com.example.rps

import com.example.rps.dto.PlayMoveRequest
import com.example.rps.service.GameService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
class GameServiceTest {

    @Autowired
    private lateinit var gameService: GameService

    @Test
    @DisplayName("Should process play move and update score")
    fun shouldPlayMoveSuccessfully() {
        val username = "testGamer"
        val request = PlayMoveRequest(playerMove = "rock", username = username)

        val response = gameService.playMove(request)

        assertTrue(response.success)
        assertEquals("rock", response.playerMove)
        assertTrue(listOf("rock", "paper", "scissor").contains(response.computerMove))
        assertTrue(listOf("Tie", "you won", "you lose").contains(response.result))
        assertEquals(1, response.score.totalGames)

        val score = gameService.getScore(username)
        assertEquals(1, score.totalGames)
    }

    @Test
    @DisplayName("Should reset score for a user")
    fun shouldResetScoreSuccessfully() {
        val username = "gamerToReset"
        gameService.playMove(PlayMoveRequest(playerMove = "paper", username = username))
        gameService.playMove(PlayMoveRequest(playerMove = "scissor", username = username))

        val scoreBefore = gameService.getScore(username)
        assertEquals(2, scoreBefore.totalGames)

        val resetScore = gameService.resetScore(username)
        assertEquals(0, resetScore.wins)
        assertEquals(0, resetScore.losses)
        assertEquals(0, resetScore.ties)
        assertEquals(0, resetScore.totalGames)
    }
}
