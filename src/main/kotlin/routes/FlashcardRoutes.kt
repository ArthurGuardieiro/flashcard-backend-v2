package com.example.routes

import com.example.model.DTO.response.ErrorResponse
import com.example.models.DTO.request.CreateFlashcardDTO
import com.example.models.DTO.request.ReviewDTO
import com.example.viewmodel.FlashcardService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.flashcardRoutes(flashcardService: FlashcardService) {
    get("/flashcards/user") {
        val userId = call.request.queryParameters["userId"]?.toIntOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing userId"))

        val flashcards = flashcardService.getFlashcardsByUserId(userId)
        call.respond(flashcards)
    }

    get("/flashcards/deck") {
        val deckId = call.request.queryParameters["deckId"]?.toIntOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing userId"))

        val flashcards = flashcardService.getFlashcardsByDeckId(deckId)
        call.respond(flashcards)
    }

    post("/flashcards") {
        try {
            val request = call.receive<CreateFlashcardDTO>()
            val status = flashcardService.createFlashcard(request)
            call.respond(status)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid data"))
        }
    }

    put("/flashcards/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing or invalid ID"))

        try {
            val request = call.receive<CreateFlashcardDTO>()
            val status = flashcardService.updateFlashcard(id, request)
            call.respond(status)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid data"))
        }
    }

    post("/flashcards/{id}/review") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing or invalid ID"))

        val userId = call.request.queryParameters["userId"]?.toIntOrNull()
            ?: return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing userId"))

        try {
            val review = call.receive<ReviewDTO>()
            val status = flashcardService.reviewFlashcard(id, userId, review)
            call.respond(status)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid data"))
        }
    }
}