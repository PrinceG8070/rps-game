package com.example.rps.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "game_rounds", indexes = [
    Index(name = "idx_round_user_played", columnList = "user_id, playedAt")
])
class GameRound(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(nullable = false, length = 20)
    var playerMove: String,

    @Column(nullable = false, length = 20)
    var computerMove: String,

    @Column(nullable = false, length = 20)
    var result: String,

    @Column(nullable = false)
    var playedAt: LocalDateTime = LocalDateTime.now()
)
