package com.example.models.entities

import org.jetbrains.exposed.sql.Table

object FlashcardLocationPriority : Table() {
    val flashcardId = integer("flashcard_id").references(Flashcards.id)
    val locationId = integer("location_id").references(Locations.id)
    val priority = integer("priority")  // Quanto menor, maior a prioridade

    init {
        uniqueIndex(flashcardId, locationId)  // Chave única composta
    }
}