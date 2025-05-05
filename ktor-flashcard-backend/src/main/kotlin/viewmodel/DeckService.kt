package com.example.viewmodel

import com.example.model.DTO.request.DeckDTO
import com.example.model.entities.Deck
import com.example.models.DTO.response.DeckResponse
import com.example.models.entities.Flashcards
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class DeckService {

    fun getDecks(userId: Int): List<DeckResponse> {
        require(userId > 0) { "Invalid user ID" }

        return transaction {
            val decks = Deck.select { Deck.userId eq userId }.toList()

            if (decks.isEmpty()) {
                return@transaction emptyList()
            }

            decks.map { deckRow ->
                val flashcardCount = Flashcards
                    .select { Flashcards.deckId eq deckRow[Deck.id].value }
                    .count()

                DeckResponse(
                    id = deckRow[Deck.id].value,
                    name = deckRow[Deck.name],
                    description = deckRow[Deck.description],
                    flashcardCount = flashcardCount.toInt(),
                    userId = deckRow[Deck.userId]
                )
            }
        }
    }

    fun getOthersDecks(userId: Int): List<DeckResponse> {
        require(userId > 0) { "Invalid user ID" }

        return transaction {
            val decks = Deck.select { Deck.userId neq userId }.toList()

            if (decks.isEmpty()) {
                return@transaction emptyList()
            }

            decks.map { deckRow ->
                val flashcardCount = Flashcards
                    .select { Flashcards.deckId eq deckRow[Deck.id].value }
                    .count()

                DeckResponse(
                    id = deckRow[Deck.id].value,
                    name = deckRow[Deck.name],
                    description = deckRow[Deck.description],
                    flashcardCount = flashcardCount.toInt(),
                    userId = deckRow[Deck.userId]
                )
            }
        }
    }


    fun getDeck(id: Int): DeckResponse? {
        require(id > 0) { "Invalid deck ID" }

        return transaction {
            Deck.select { Deck.id eq id }
                .map { it.toDeckResponse() }
                .firstOrNull()
        }
    }

    fun createDeck(request: DeckDTO, userId: Int): HttpStatusCode {
        require(userId > 0) { "Invalid user ID" }  // 👈 ADICIONE ESTA LINHA
        val validatedRequest = request.validate()

        transaction {
            Deck.insert {
                it[name] = validatedRequest.deck_name
                it[description] = validatedRequest.deck_description
                it[Deck.userId] = userId
            }
        }
        return HttpStatusCode.Created
    }


    fun updateDeck(id: Int, request: DeckDTO): HttpStatusCode {
        require(id > 0) { "Invalid deck ID" }
        val validatedRequest = request.validate()

        transaction {
            Deck.update({ Deck.id eq id }) {
                it[name] = validatedRequest.deck_name
                it[description] = validatedRequest.deck_description
            }
        }
        return HttpStatusCode.OK
    }

    fun deleteDeck(id: Int): HttpStatusCode {
        require(id > 0) { "Invalid deck ID" }

        transaction {
            Deck.deleteWhere { Deck.id eq id }
        }
        return HttpStatusCode.OK
    }

    private fun ResultRow.toDeckResponse(): DeckResponse {
        val flashcardCount = transaction {
            Flashcards.select { Flashcards.deckId eq this@toDeckResponse[Deck.id].value }
                .count()
        }

        return DeckResponse(
            id = this[Deck.id].value,
            name = this[Deck.name],
            description = this[Deck.description],
            flashcardCount = flashcardCount.toInt(),
            userId = this[Deck.userId]
        )
    }

    private fun DeckDTO.validate(): DeckDTO {
        require(deck_name.isNotBlank()) { "Deck name cannot be blank" }
        return this
    }
} 