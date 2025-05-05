package com.example.models.entities

import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val username = varchar("username", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 64)
    val defaultLocationId = integer("default_location_id")
        .references(Locations.id)  // Já está correto na sua definição
        .default(1)  // Opcional: define um valor padrão (ex.: ID 1 para "Sala")
}
