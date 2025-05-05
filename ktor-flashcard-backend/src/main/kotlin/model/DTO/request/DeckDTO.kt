package com.example.model.DTO.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeckDTO(
    @SerialName("name") val deck_name: String,
    @SerialName("description") val deck_description: String
)