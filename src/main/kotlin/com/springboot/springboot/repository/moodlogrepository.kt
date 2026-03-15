package com.springboot.springboot.repository

import com.springboot.springboot.entity.MoodLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface MoodLogRepository : JpaRepository<MoodLog, Int> {

    // Used to check if a user already logged a specific slot today (for UPDATES)
    fun findByUserIdAndLogDateAndTimeOfDay(userId: Int, logDate: LocalDate, timeOfDay: String): MoodLog?

    // The Weighted Daily Math: (Morning*1 + Evening*1 + Night*2) / 4
    @Query("""
    SELECT SUM(
        CASE 
            WHEN m.timeOfDay = 'MORNING' THEN m.moodScore * 1
            WHEN m.timeOfDay = 'EVENING' THEN m.moodScore * 1
            WHEN m.timeOfDay = 'NIGHT' THEN m.moodScore * 2
            ELSE 0 
        END
    ) * 1.0 / NULLIF(SUM(
        CASE 
            WHEN m.timeOfDay = 'MORNING' THEN 1
            WHEN m.timeOfDay = 'EVENING' THEN 1
            WHEN m.timeOfDay = 'NIGHT' THEN 2
            ELSE 0 
        END
    ), 0)
    FROM MoodLog m 
    WHERE m.userId = :userId AND m.logDate = :date
""")
    fun getDailyWeightedRawScore(@Param("userId") userId: Int, @Param("date") date: LocalDate): Double?
    // The Monthly Average of those daily raw scores
    @Query("""
    SELECT SUM(
        CASE 
            WHEN m.timeOfDay = 'MORNING' THEN m.moodScore * 1
            WHEN m.timeOfDay = 'EVENING' THEN m.moodScore * 1
            WHEN m.timeOfDay = 'NIGHT' THEN m.moodScore * 2
            ELSE 0 
        END
    ) * 1.0 / NULLIF(SUM(
        CASE 
            WHEN m.timeOfDay = 'MORNING' THEN 1
            WHEN m.timeOfDay = 'EVENING' THEN 1
            WHEN m.timeOfDay = 'NIGHT' THEN 2
            ELSE 0 
        END
    ), 0)
    FROM MoodLog m 
    WHERE m.userId = :userId AND m.logDate >= :startDate
""")
    fun getMonthlyWeightedRawScore(@Param("userId") userId: Int, @Param("startDate") startDate: LocalDate): Double?}