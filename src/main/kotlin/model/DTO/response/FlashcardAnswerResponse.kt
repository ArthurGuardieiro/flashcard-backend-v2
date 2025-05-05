package com.example.models.DTO.response

import kotlinx.serialization.Serializable

@Serializable
data class FlashcardAnswerResponse(
    val quality: Int,
    val isCorrect: Boolean,
    val correctAnswer: String? = null
)
