package com.example.models.evaluation

import kotlin.math.max

object AnswerQualityEvaluator {
    fun evaluateQuality(responseTimeMs: Long, isCorrect: Boolean): Int {
        return when {
            // Respostas corretas
            isCorrect && responseTimeMs < 3000 -> 5  // Perfeita (até 3s)
            isCorrect && responseTimeMs < 8000 -> 4  // Hesitação (3-8s)
            isCorrect -> 3                           // Dificuldade (>8s)

            // Respostas incorretas
            !isCorrect && responseTimeMs < 4000 -> 2 // Erro fácil (até 4s)
            !isCorrect && responseTimeMs < 10000 -> 1// Erro com lembrança (4-10s)
            else -> 0                               // Apagão (>10s ou qualquer erro muito lento)
        }
    }
}