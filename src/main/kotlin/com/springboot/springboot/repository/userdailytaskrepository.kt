package com.springboot.springboot.repository

import com.springboot.springboot.entity.UserDailyTask
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface UserDailyTaskRepository : JpaRepository<UserDailyTask, Int> {

    // This finds the 3 tasks already assigned to a specific user for today
    fun findByUserIdAndAssignedDate(userId: Int, assignedDate: LocalDate): List<UserDailyTask>

}