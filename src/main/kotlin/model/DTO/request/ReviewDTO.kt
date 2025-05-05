package com.example.models.DTO.request

import kotlinx.serialization.Serializable

@Serializable
data class ReviewDTO(
    val quality: Int,
    val locationId: Int
) 