package com.springboot.springboot.controller

import com.springboot.springboot.entity.*
import com.springboot.springboot.repository.*
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import java.time.LocalDate
import kotlin.collections.take
import com.springboot.springboot.repository.MoodLogRepository


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["*"])
class MentalHealthController(
    private val userRepository: UserRepository,
    private val quoteRepository: QuoteRepository,
    private val taskRepository: TaskRepository,
    private val userDailyTaskRepository: UserDailyTaskRepository,
    private val journalRepository: JournalRepository,
    private val repository: MoodLogRepository
) {
    // --- LOGIN & REGISTER ---
    @PostMapping("/login")
    fun loginUser(@RequestBody loginRequest: User): ResponseEntity<Any> {
        val user = userRepository.findByUsername(loginRequest.username)
        return if (user != null && user.password == loginRequest.password) {
            ResponseEntity.ok(mapOf("message" to "Success", "username" to user.username, "id" to user.id))
        } else {
            ResponseEntity.status(401).body("Invalid username or password")
        }
    }

    @PostMapping("/register")
    fun registerUser(@RequestBody user: User): ResponseEntity<Any> = try {
        ResponseEntity.ok(userRepository.save(user))
    } catch (e: Exception) {
        println("Registration Error: ${e.message}")
        ResponseEntity.status(400).body("Username already taken!")
    }

    // --- TASKS ---
    @GetMapping("/tasks/daily/{userId}")
    fun getDailyTasks(@PathVariable userId: Int): List<UserDailyTask> {
        val today = LocalDate.now()
        val existingTasks = userDailyTaskRepository.findByUserIdAndAssignedDate(userId, today)

        if (existingTasks.isNotEmpty()) return existingTasks

        val allLibraryTasks = taskRepository.findAll()
        val randomThree = allLibraryTasks.shuffled().take(3)

        val newAssignments = randomThree.map { libraryTask ->
            UserDailyTask(
                userId = userId,
                taskId = libraryTask.id,
                completed = false,
                assignedDate = today
            )
        }
        return userDailyTaskRepository.saveAll(newAssignments)
    }

    @PutMapping("/tasks/toggle/{id}/{userId}")
    fun toggleTask(@PathVariable id: Int,@PathVariable userId: Int,
        @RequestParam completed: Boolean): UserDailyTask {
        val task = userDailyTaskRepository.findById(id).orElseThrow { RuntimeException("Task not found") }
        task.completed = completed
        return userDailyTaskRepository.save(task)
    }

    @GetMapping("/tasks/library")
    fun getTaskLibrary(): List<Task> {
        return taskRepository.findAll()
    }

    // --- QUOTES ---
    @GetMapping("/quotes")
    fun getRandomQuote(): DailyQuote? {
        val allQuotes = quoteRepository.findAll()
        return if (allQuotes.isNotEmpty()) allQuotes.random() else null
    }


    // --- JOURNAL ---
    @GetMapping("/journal/user/{userId}")
    fun getJournalHistory(@PathVariable userId: Int): List<JournalEntry> {
        // Change findByUserId to findByUserIdOrderByCreatedAtDesc
        return journalRepository.findByUserIdOrderByCreatedAtDesc(userId)
    }

    @PostMapping("/journal")
    fun saveJournal(@RequestBody entry: JournalEntry): ResponseEntity<Any> = try {
        ResponseEntity.ok(journalRepository.save(entry))
    } catch (e: Exception) {
        ResponseEntity.status(500).body("Error: ${e.message}")
    }

    @PutMapping("/journal/{id}")
    fun updateJournal(@PathVariable id: Int, @RequestBody entry: JournalEntry): ResponseEntity<Any> {
        val existing = journalRepository.findById(id).orElse(null)
            ?: return ResponseEntity.status(404).body("Note not found")

        existing.content = entry.content


        return ResponseEntity.ok(journalRepository.save(existing))
    }

    // THIS MUST BE INSIDE THE CLASS BRACES
    @DeleteMapping("/journal/{id}")
    fun deleteJournalEntry(@PathVariable id: Int): ResponseEntity<Any> {
        return if (journalRepository.existsById(id)) {
            journalRepository.deleteById(id)
            ResponseEntity.ok(mapOf("message" to "Deleted successfully"))
        } else {
            ResponseEntity.status(404).body("Entry not found")
        }
    }

    @PostMapping("/mood/log")
    fun logMood(@RequestBody request: MoodLogRequest): ResponseEntity<Any> {
        val date = LocalDate.parse(request.logDate)

        // Check if an entry already exists for this slot to avoid duplicates
        val existingLog = repository.findByUserIdAndLogDateAndTimeOfDay(
            request.userId,
            date,
            request.timeOfDay
        )

        return if (existingLog != null) {
            existingLog.moodScore = request.moodScore
            repository.save(existingLog)
            ResponseEntity.ok(mapOf("status" to "updated"))
        } else {
            val newLog = MoodLog(
                userId = request.userId,
                moodScore = request.moodScore,
                timeOfDay = request.timeOfDay,
                logDate = date
            )
            repository.save(newLog)
            ResponseEntity.ok(mapOf("status" to "created"))
        }
    }
    @GetMapping("/mood/summary/{userId}")
    fun getSummary(@PathVariable userId: Int): Map<String, Any> {
        val today = LocalDate.now()
        val thirtyDaysAgo = today.minusDays(30)

        // Capture the raw results WITHOUT the ?: 0.0 yet
        val dailyRaw = repository.getDailyWeightedRawScore(userId, today)
        val monthlyRaw = repository.getMonthlyWeightedRawScore(userId, thirtyDaysAgo)

        // Handle Daily Percentage
        val dailyPercent = if (dailyRaw == null) {
            0 // Show 0% if nothing is logged
        } else {
            ((dailyRaw + 1.0) / 2.0 * 100.0).toInt().coerceIn(0, 100)
        }

        // Handle Monthly Percentage
        val monthlyPercent = if (monthlyRaw == null) {
            0
        } else {
            ((monthlyRaw + 1.0) / 2.0 * 100.0).toInt().coerceIn(0, 100)
        }

        return mapOf(
            "dailyPercentage" to dailyPercent,
            "monthlyPercentage" to monthlyPercent,
            "label" to if (dailyRaw == null) "No logs today" else determineLabel(dailyPercent)
        )
    }

    private fun determineLabel(p: Int): String {
        return when {
            p >= 85 -> "Excellent"
            p >= 65 -> "Mostly Positive"
            p >= 40 -> "Steady / Balanced"
            p >= 20 -> "A Bit Tough"
            else -> "Rough"
        }
    }
    @DeleteMapping("/mood/remove")
    fun removeMood(
        @RequestParam userId: Int,
        @RequestParam date: String,
        @RequestParam timeOfDay: String
    ): ResponseEntity<Map<String, String>> {

        val localDate = LocalDate.parse(date)
        val existingLog = repository.findByUserIdAndLogDateAndTimeOfDay(userId, localDate, timeOfDay)

        return if (existingLog != null) {
            repository.delete(existingLog)
            ResponseEntity.ok(mapOf("message" to "Mood entry removed successfully"))
        } else {
            ResponseEntity.ok(mapOf("message" to "No entry found for this slot"))
        }
    }}