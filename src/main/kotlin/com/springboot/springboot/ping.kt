package com.springboot.springboot

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {
    @GetMapping("/ping")
    fun ping() = "Backend is awake!"
}