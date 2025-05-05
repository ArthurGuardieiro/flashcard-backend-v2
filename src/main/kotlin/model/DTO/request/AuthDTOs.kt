package com.example.models.DTO.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(val username: String, val password: String)

@Serializable
data class AuthResponse(val message: String)
