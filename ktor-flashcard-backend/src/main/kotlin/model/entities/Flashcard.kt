package com.example.models.entities

import com.example.model.entities.Deck
import com.example.models.FlashcardType
import org.jetbrains.exposed.dao.id.IntIdTable

object Flashcards : IntIdTable() {
    val question = varchar("question", 255)
    val answer = varchar("answer", 255)
    val type = enumerationByName("type", 50, FlashcardType::class)
    val options = text("options").nullable()
    val userId = integer("user_id").references(Users.id)
    val deckId = integer("deck_id").references(Deck.id)
    val nextRepetition = text("next_repetition")
    val repetitions = integer("repetitions")
    val easinessFactor = float("easiness_factor")
    val interval = integer("interval")
}