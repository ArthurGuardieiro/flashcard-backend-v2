package com.example.models.DTO.response
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val message: String,
    val userId: Int?
)
