package com.example.models.DTO.request

import kotlinx.serialization.Serializable

@Serializable
data class LocationDTO(
    val name: String,
    val userId: Int
)
