package com.springboot.springboot


import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.sql.DataSource
import java.sql.Connection

@RestController
class TestController(val dataSource: DataSource) {

    @GetMapping("/db-check")
    fun checkConnection(): String {
        return try {
            // Using a raw connection check to be 100% sure
            val connection: Connection = dataSource.connection
            val isValid = connection.isValid(5)
            connection.close()

            if (isValid) "✅ Success! Spring Boot is talking to Neon!"
            else "⚠️ Connected, but connection is invalid."
        } catch (e: Exception) {
            "❌ Database Connection Failed: ${e.message}"
        }
    }
}