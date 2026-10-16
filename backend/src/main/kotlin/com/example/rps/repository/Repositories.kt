package com.example.rps.repository

import com.example.rps.model.GameRound
import com.example.rps.model.GameScore
import com.example.rps.model.LoginLog
import com.example.rps.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): Optional<User>
    fun existsByUsername(username: String): Boolean
    fun findByUsernameContainingIgnoreCase(username: String, pageable: Pageable): Page<User>
}

@Repository
interface LoginLogRepository : JpaRepository<LoginLog, Long> {
    fun findByUsernameOrderByLoginTimeDesc(username: String, pageable: Pageable): Page<LoginLog>
    fun findAllByOrderByLoginTimeDesc(pageable: Pageable): Page<LoginLog>
}

@Repository
interface GameScoreRepository : JpaRepository<GameScore, Long> {
    fun findByUser(user: User): Optional<GameScore>
    fun findByUserUsername(username: String): Optional<GameScore>
    
    @Query("SELECT gs FROM GameScore gs JOIN FETCH gs.user ORDER BY gs.wins DESC, gs.totalGames ASC")
    fun findTopLeaderboard(pageable: Pageable): List<GameScore>
}

@Repository
interface GameRoundRepository : JpaRepository<GameRound, Long> {
    fun findByUserOrderByPlayedAtDesc(user: User, pageable: Pageable): Page<GameRound>
    fun findByUserUsernameOrderByPlayedAtDesc(username: String, pageable: Pageable): Page<GameRound>
}
