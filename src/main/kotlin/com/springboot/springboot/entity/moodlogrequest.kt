package com.springboot.springboot.entity

// This is the DTO that matches the JSON from your Android app
data class MoodLogRequest(
    val userId: Int,
    val moodScore: Int,
    val timeOfDay: String,
    val logDate: String
)