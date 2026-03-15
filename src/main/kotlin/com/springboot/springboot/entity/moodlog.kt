package com.springboot.springboot.entity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Table
import jakarta.persistence.Id

@Entity
@Table(name = "mood_logs")
class MoodLog(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    val userId: Int,
    var moodScore: Int, // Can change if user updates it

    @Column(name = "time_of_day")
    val timeOfDay: String, // "MORNING", "EVENING", "NIGHT"

    @Column(name = "log_date")
    val logDate: java.time.LocalDate = java.time.LocalDate.now()
)