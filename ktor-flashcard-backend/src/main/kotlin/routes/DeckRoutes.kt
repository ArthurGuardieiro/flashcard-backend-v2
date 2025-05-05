package com.example.routes

import com.example.model.DTO.request.DeckDTO
import com.example.model.DTO.response.ErrorResponse
import com.example.viewmodel.DeckService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.deckRoutes(deckService: DeckService) {
    get("/decks") {
        val userId = call.request.queryParameters["userId"]?.toIntOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing or invalid userId"))

        val decks = deckService.getDecks(userId)
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Usuário inválido: ID deve ser maior que 0."))

        call.respond(decks)
    }


    get("/decks/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing or invalid ID"))

        val deck = deckService.getDeck(id)
            ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("Deck not found"))

        call.respond(deck)
    }

    get("/decks/others") {
        val id = call.queryParameters["userId"]?.toIntOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing or invalid ID"))

        val deck = deckService.getOthersDecks(id)
            ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("Deck not found"))

        call.respond(deck)
    }

    post("/decks") {
        try {
            val request = call.receive<DeckDTO>()
            val userId = call.request.queryParameters["userId"]?.toIntOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing userId"))

            val status = deckService.createDeck(request, userId)
            call.respond(status)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid data"))
        } catch (e: Exception) {
            e.printStackTrace()  // Importante para depuração
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Internal server error: ${e.message}"))
        }
    }


    put("/decks/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing or invalid ID"))

        try {
            val request = call.receive<DeckDTO>()
            val status = deckService.updateDeck(id, request)
            call.respond(status)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid data"))
        }
    }

    delete("/decks/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing or invalid ID"))

        val status = deckService.deleteDeck(id)
        call.respond(status)
    }
} 