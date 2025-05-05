package com.example.model.DTO.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String,
    val details: String? = null
)
