package com.example.viewmodel

import com.example.models.DTO.request.CreateFlashcardDTO
import com.example.models.DTO.request.FlashcardDTO
import com.example.models.DTO.request.ReviewDTO
import com.example.models.DTO.response.FlashcardResponse
import com.example.models.algorithm.SpacedRepetition
import com.example.models.entities.Flashcards
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class FlashcardService(private val spacedRepetition: SpacedRepetition) {
    fun getFlashcardsByUserId(userId: Int): List<FlashcardResponse> {
        require(userId > 0) { "Invalid user ID" }

        return transaction {
            Flashcards.select { Flashcards.userId eq userId }
                .map { it.toFlashcardResponse() }
        }
    }

    fun getFlashcardsByDeckId(deckId: Int): List<FlashcardResponse> {
        require(deckId > 0) { "Invalid user ID" }

        return transaction {
            Flashcards.select { Flashcards.deckId eq deckId }
                .map { it.toFlashcardResponse() }
        }
    }

    fun getFlashcard(id: Int): FlashcardDTO? {
        require(id > 0) { "Invalid flashcard ID" }

        return transaction {
            Flashcards.select { Flashcards.id eq id }
                .map { it.toFlashcardDTO() }
                .firstOrNull()
        }
    }

    fun createFlashcard(request: CreateFlashcardDTO): HttpStatusCode {
        val validatedRequest = request.validate()

        transaction {
            Flashcards.insert {
                it.applyFromCreateDTO(validatedRequest)
            }
        }
        return HttpStatusCode.Created
    }

    fun updateFlashcard(id: Int, request: CreateFlashcardDTO): HttpStatusCode {
        require(id > 0) { "Invalid flashcard ID" }
        val validatedRequest = request.validate()

        transaction {
            Flashcards.update({ Flashcards.id eq id }) {
                it.applyFromCreateDTO(validatedRequest)
            }
        }
        return HttpStatusCode.OK
    }

    fun reviewFlashcard(id: Int, userId: Int, review: ReviewDTO): HttpStatusCode {
        require(id > 0) { "Invalid flashcard ID" }
        require(userId > 0) { "Invalid user ID" }

        val flashcard = getFlashcard(id) ?: throw IllegalArgumentException("Flashcard not found")
        
        val updatedFlashcard = spacedRepetition.calculateRepetition(
            card = flashcard,
            quality = review.quality,
            userId = userId,
            locationId = review.locationId
        )

        transaction {
            Flashcards.update({ Flashcards.id eq id }) {
                it[Flashcards.question] = updatedFlashcard.question
                it[Flashcards.answer] = updatedFlashcard.answer
                it[Flashcards.type] = updatedFlashcard.type
                it[Flashcards.options] = updatedFlashcard.options?.joinToString(";")
                it[Flashcards.userId] = updatedFlashcard.userId
                it[Flashcards.deckId] = updatedFlashcard.deckId
                it[Flashcards.nextRepetition] = updatedFlashcard.nextRepetition.toString()
                it[Flashcards.repetitions] = updatedFlashcard.repetitions
                it[Flashcards.easinessFactor] = updatedFlashcard.easinessFactor
                it[Flashcards.interval] = updatedFlashcard.interval
            }
        }
        return HttpStatusCode.OK
    }

    private fun ResultRow.toFlashcardResponse(): FlashcardResponse {
        return FlashcardResponse(
            id = this[Flashcards.id].value,
            question = this[Flashcards.question],
            answer = this[Flashcards.answer],
            type = this[Flashcards.type].toString(),
            options = this[Flashcards.options]?.split(";") ?: emptyList(),
            nextRepetition = this[Flashcards.nextRepetition],
            repetitions = this[Flashcards.repetitions],
            easinessFactor = this[Flashcards.easinessFactor],
            interval = this[Flashcards.interval]
        )
    }

    private fun ResultRow.toFlashcardDTO(): FlashcardDTO {
        return FlashcardDTO(
            id = this[Flashcards.id].value,
            question = this[Flashcards.question],
            answer = this[Flashcards.answer],
            type = this[Flashcards.type],
            options = this[Flashcards.options]?.split(";"),
            userId = this[Flashcards.userId],
            locationId = 0, // This will be set by the spaced repetition algorithm
            deckId = this[Flashcards.deckId],
            nextRepetition = LocalDateTime.parse(this[Flashcards.nextRepetition]),
            repetitions = this[Flashcards.repetitions],
            easinessFactor = this[Flashcards.easinessFactor],
            interval = this[Flashcards.interval]
        )
    }

    private fun CreateFlashcardDTO.validate(): CreateFlashcardDTO {
        require(question.isNotBlank()) { "Question cannot be blank" }
        require(answer.isNotBlank()) { "Answer cannot be blank" }
        require(userId > 0) { "Invalid user ID" }
        require(locationId > 0) { "Invalid location ID" }
        require(deckId > 0) { "Invalid deck ID" }
        return this
    }

    private fun InsertStatement<Number>.applyFromCreateDTO(dto: CreateFlashcardDTO) {
        this[Flashcards.question] = dto.question
        this[Flashcards.answer] = dto.answer
        this[Flashcards.type] = dto.type
        this[Flashcards.options] = dto.options?.joinToString(";")
        this[Flashcards.userId] = dto.userId
        this[Flashcards.deckId] = dto.deckId
        // Initialize algorithm fields with default values
        this[Flashcards.nextRepetition] = LocalDateTime.now().toString()
        this[Flashcards.repetitions] = 0
        this[Flashcards.easinessFactor] = 2.5.toFloat()
        this[Flashcards.interval] = 1
    }

    private fun UpdateStatement.applyFromCreateDTO(dto: CreateFlashcardDTO) {
        this[Flashcards.question] = dto.question
        this[Flashcards.answer] = dto.answer
        this[Flashcards.type] = dto.type
        this[Flashcards.options] = dto.options?.joinToString(";")
        this[Flashcards.userId] = dto.userId
        this[Flashcards.deckId] = dto.deckId
        // Don't update algorithm fields during regular updates
    }
}