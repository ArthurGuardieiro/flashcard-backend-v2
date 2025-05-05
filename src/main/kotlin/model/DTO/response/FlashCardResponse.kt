package com.example.models.DTO.response
import kotlinx.serialization.Serializable

@Serializable
data class FlashcardResponse(
    val id: Int,
    val question: String,
    val answer: String,
    val type: String,
    val options: List<String>,
    val nextRepetition: String,
    val repetitions: Int,
    val easinessFactor: Float,
    val interval: Int,
)



