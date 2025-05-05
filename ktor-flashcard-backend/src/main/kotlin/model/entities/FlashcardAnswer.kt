package com.example.models.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import java.time.Instant

object FlashcardAnswer : IntIdTable() {
    val flashcardId = integer("flashcard_id").references(Flashcards.id)
    val userId = integer("user_id").references(Users.id)
    val locationId = integer("location_id").references(Locations.id)
    val responseTimeMs = long("response_time_ms")
    val userAnswer = text("user_answer").nullable()
    val quality = integer("quality")
    val isCorrect = bool("is_correct")
    val createdAt = varchar("created_at", 50).clientDefault {
        Instant.now().toString()  // Formato ISO-8601 automático
    }
}