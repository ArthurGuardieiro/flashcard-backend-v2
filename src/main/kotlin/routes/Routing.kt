package com.example.routes

import com.example.viewmodel.*
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val authService by inject<AuthService>()
    val flashcardService by inject<FlashcardService>()
    val flashcardAnswerService by inject<FlashcardAnswerService>()
    val locationService by inject<LocationService>()
    val deckService by inject<DeckService>()

    routing {
        route("/api") {
            authRoutes(authService)
            flashcardRoutes(flashcardService)
            flashcardAnswerRoutes(flashcardAnswerService)
            locationRoutes(locationService)
            deckRoutes(deckService)
        }
    }
}