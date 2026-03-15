package com.springboot.springboot.entity

import jakarta.persistence.*
import com.fasterxml.jackson.annotation.JsonProperty

@Entity
@Table(name = "journal_entries")
class JournalEntry(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @JsonProperty("userId") // This forces Jackson to look for "userId" in the JSON
    @Column(name = "user_id")
    val userId: Int,

    @JsonProperty("content")
    var content: String,

    @Column(name = "created_at")
    var createdAt: java.sql.Timestamp? = java.sql.Timestamp(System.currentTimeMillis())
)