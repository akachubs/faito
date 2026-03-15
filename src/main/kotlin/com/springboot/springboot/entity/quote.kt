package com.springboot.springboot.entity

import jakarta.persistence.*

@Entity
@Table(name = "daily_quotes")
class DailyQuote(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "quote_text", nullable = false, columnDefinition = "TEXT")
    val quoteText: String = "",

    @Column(nullable = false, length = 100)
    val author: String = "Unknown"
)