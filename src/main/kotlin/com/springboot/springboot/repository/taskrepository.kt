package com.springboot.springboot.repository

import org.springframework.data.jpa.repository.JpaRepository
import com.springboot.springboot.entity.Task
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Int>