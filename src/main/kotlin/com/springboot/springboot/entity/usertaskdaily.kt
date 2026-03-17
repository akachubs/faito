package com.springboot.springboot.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "user_daily_tasks")
class UserDailyTask(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val userId: Int,
    val taskId: Int,
    var completed: Boolean = false,
    val assignedDate: LocalDate = LocalDate.now(),

)