package com.example.models.DTO.response

import kotlinx.serialization.Serializable

@Serializable
data class DeckResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val flashcardCount: Int,
    val userId: Int
) 