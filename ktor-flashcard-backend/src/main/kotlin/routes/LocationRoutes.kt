package com.example.routes

import com.example.model.DTO.response.ErrorResponse
import com.example.models.DTO.request.LocationDTO
import com.example.viewmodel.LocationService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.locationRoutes(locationService: LocationService) {
    get("/locations") {
        val userId = call.request.queryParameters["userId"]?.toIntOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing userId"))

        val locations = locationService.getLocations(userId)
        call.respond(locations)
    }

    post("/locations") {
        try {
            val request = call.receive<LocationDTO>()
            val location = locationService.createLocation(request)
            call.respond(HttpStatusCode.Created, location)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid data"))
        }
    }

    put("/locations/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing or invalid ID"))

        try {
            val request = call.receive<LocationDTO>()
            val location = locationService.updateLocation(id, request)
            call.respond(location)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid data"))
        }
    }
} 