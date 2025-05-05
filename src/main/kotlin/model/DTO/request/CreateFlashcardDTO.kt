package com.example.models.DTO.request

import com.example.models.FlashcardType
import kotlinx.serialization.Serializable

@Serializable
data class CreateFlashcardDTO(
    val question: String,
    val answer: String,
    val type: FlashcardType,
    val options: List<String>? = null,
    val userId: Int,
    val locationId: Int,
    val deckId: Int
) 