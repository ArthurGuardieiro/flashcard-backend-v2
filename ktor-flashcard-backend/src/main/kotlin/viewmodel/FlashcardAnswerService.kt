package com.example.viewmodel

import com.example.models.DTO.request.FlashcardAnswerDTO
import com.example.models.DTO.response.FlashcardAnswerResponse
import com.example.models.DTO.response.FlashcardResponse
import com.example.models.entities.FlashcardAnswer
import com.example.models.entities.Flashcards
import com.example.models.evaluation.AnswerQualityEvaluator
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class FlashcardAnswerService {
    fun processAnswer(request: FlashcardAnswerDTO): Pair<FlashcardAnswerResponse, HttpStatusCode> {
        // 1. Busca o flashcard completo
        val flashcard = transaction {
            Flashcards.select { Flashcards.id eq request.flashcardId }
                .singleOrNull()
        } ?: throw IllegalArgumentException("Flashcard não encontrado")

        // 2. Validação da resposta
        val correctAnswer = flashcard[Flashcards.answer]
        val isUserAnswerCorrect = request.userAnswer.equals(correctAnswer, ignoreCase = true)

        // 3. Calcula a qualidade
        val calculatedQuality = AnswerQualityEvaluator.evaluateQuality(
            responseTimeMs = request.responseTimeMs,
            isCorrect = isUserAnswerCorrect
        )

        // 4. Salva a resposta
        transaction {
            FlashcardAnswer.insert {
                it[flashcardId] = request.flashcardId
                it[userId] = request.userId
                it[responseTimeMs] = request.responseTimeMs
                it[userAnswer] = request.userAnswer
                it[quality] = calculatedQuality
                it[isCorrect] = isUserAnswerCorrect
                it[locationId] = request.locationId
                it[createdAt] = request.createdAt?.toString() ?: Instant.now().toString()
            }
        }

        // 5. Retorna a resposta com feedback
        return Pair(
            FlashcardAnswerResponse(
                isCorrect = isUserAnswerCorrect,
                quality = calculatedQuality,
                correctAnswer = if (!isUserAnswerCorrect) correctAnswer else null
            ),
            HttpStatusCode.OK
        )
    }

    fun getByUserId(userId: Int) : List<FlashcardAnswerResponse> {
        require(userId > 0) { "Invalid user ID" }

        return transaction {
            FlashcardAnswer.select { FlashcardAnswer.userId eq userId }
                .map { it.toFlashcardAnwser() }
        }
    }

    private fun ResultRow.toFlashcardAnwser(): FlashcardAnswerResponse {
        return FlashcardAnswerResponse(
            quality = this[FlashcardAnswer.quality],
            isCorrect = this[FlashcardAnswer.isCorrect],
        )
    }

}