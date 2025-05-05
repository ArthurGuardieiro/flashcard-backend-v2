package com.example.models.entities

import org.jetbrains.exposed.dao.id.IntIdTable

object Locations : IntIdTable() {
    val name = varchar("name", 100)
    val userId = integer("user_id").references(Users.id)
}