package com.springboot.springboot.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false, unique = true)
    val username: String = "",

    @Column(nullable = false)
    val password: String = "",

    @Column(name = "created_at", insertable = false, updatable = false)
    val createdAt: LocalDateTime? = null
)