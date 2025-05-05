package com.example.models.DTO.request

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class FlashcardAnswerDTO(
    val flashcardId: Int,
    val userId: Int,
    val responseTimeMs: Long,
    val userAnswer: String? = null,
    val locationId: Int,
    val quality: Int,
    val isCorrect: Boolean,
    @Contextual
    val createdAt: Instant? = Instant.now()
) {
    init {
        require(responseTimeMs > 0) { "Tempo de resposta inválido" }
        require(flashcardId > 0 && userId > 0) { "IDs devem ser positivos" }
        require(locationId > 0) { "Localização inválida" }
        require(quality in 0..5) { "Qualidade deve ser entre 0-5" } // Exemplo para escala Anki
    }
}
