package com.example.rps.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "game_scores", indexes = [
    Index(name = "idx_score_wins", columnList = "wins")
])
class GameScore(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    var user: User,

    @Column(nullable = false)
    var wins: Int = 0,

    @Column(nullable = false)
    var losses: Int = 0,

    @Column(nullable = false)
    var ties: Int = 0,

    @Column(nullable = false)
    var totalGames: Int = 0,

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
