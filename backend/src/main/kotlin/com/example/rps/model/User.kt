package com.example.rps.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users", indexes = [Index(name = "idx_user_username", columnList = "username", unique = true)])
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, length = 100)
    var username: String,

    @Column(nullable = false)
    var password: String,

    @Column(length = 150)
    var email: String? = null,

    @Column(length = 255)
    var avatarUrl: String? = null,

    @Column(length = 50)
    var role: String = "ROLE_USER",

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    var score: GameScore? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var loginLogs: MutableList<LoginLog> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var gameRounds: MutableList<GameRound> = mutableListOf()
)
