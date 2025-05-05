package com.example.viewmodel

import com.example.models.DTO.request.LocationDTO
import com.example.models.DTO.response.LocationResponse
import com.example.models.entities.Locations
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class LocationService {
    fun getLocations(userId: Int): List<LocationResponse> {
        require(userId > 0) { "Invalid user ID" }

        return transaction {
            Locations.select { Locations.userId eq userId }
                .map { it.toLocationResponse() }
        }
    }

    fun createLocation(request: LocationDTO): LocationResponse {
        require(request.name.isNotBlank()) { "Location name cannot be blank" }
        require(request.userId > 0) { "Invalid user ID" }

        return transaction {
            val id = Locations.insert {
                it[name] = request.name
                it[userId] = request.userId
            } get Locations.id

            Locations.select { Locations.id eq id }
                .single()
                .toLocationResponse()
        }
    }

    fun updateLocation(id: Int, request: LocationDTO): LocationResponse {
        require(id > 0) { "Invalid location ID" }
        require(request.name.isNotBlank()) { "Location name cannot be blank" }
        require(request.userId > 0) { "Invalid user ID" }

        return transaction {
            Locations.update({ Locations.id eq id }) {
                it[name] = request.name
                it[userId] = request.userId
            }

            Locations.select { Locations.id eq id }
                .single()
                .toLocationResponse()
        }
    }

    fun getDefaultLocation(userId: Int): Int {
        return transaction {
            Locations.select { Locations.userId eq userId }
                .firstOrNull()?.get(Locations.id)?.value ?: 1
        }
    }

    private fun ResultRow.toLocationResponse(): LocationResponse {
        return LocationResponse(
            id = this[Locations.id].value,
            name = this[Locations.name],
            userId = this[Locations.userId]
        )
    }
}