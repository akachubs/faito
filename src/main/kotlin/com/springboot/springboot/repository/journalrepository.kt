package com.springboot.springboot.repository

import com.springboot.springboot.entity.JournalEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JournalRepository : JpaRepository<JournalEntry, Int> {
    // "Desc" stands for Descending (Newest -> Oldest)
    fun findByUserIdOrderByCreatedAtDesc(userId: Int): List<JournalEntry>
}