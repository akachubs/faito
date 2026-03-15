package com.springboot.springboot.repository

import com.springboot.springboot.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Int> {
    // This is magic: Spring will automatically create a query to find a user by username!
    fun findByUsername(username: String): User?
}