package com.example.rps.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "login_logs", indexes = [
    Index(name = "idx_login_username", columnList = "username"),
    Index(name = "idx_login_time", columnList = "loginTime")
])
class LoginLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, length = 100)
    var username: String,

    @Column(nullable = false)
    var success: Boolean,

    @Column(length = 100)
    var ipAddress: String? = null,

    @Column(nullable = false)
    var loginTime: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null
)
