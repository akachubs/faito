package com.springboot.springboot.repository

import com.springboot.springboot.entity.DailyQuote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuoteRepository : JpaRepository<DailyQuote, Int>