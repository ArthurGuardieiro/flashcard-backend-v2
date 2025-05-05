package com.example.routes

import com.example.models.DTO.request.RegisterRequest
import com.example.viewmodel.AuthService
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.authRoutes(authService: AuthService) {
    post("/register") {
        val request = call.receive<RegisterRequest>()
        val (response, status) = authService.register(request)
        call.respond(status, response)
    }

    post("/login") {
        val request = call.receive<RegisterRequest>()
        val (response, status) = authService.login(request)
        call.respond(status, response)
    }
}