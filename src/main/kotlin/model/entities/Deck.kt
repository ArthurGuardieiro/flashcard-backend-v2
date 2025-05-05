package com.example.model.entities

import com.example.models.entities.FlashcardAnswer.nullable
import com.example.models.entities.Users
import org.jetbrains.exposed.dao.id.IntIdTable

object Deck: IntIdTable() {
    val name = text("deck_name")
    val description = text("deck_description").nullable()
    val userId = integer("user_id").references(Users.id)
}