package com.example

import com.example.di.appModule
import com.example.models.entities.FlashcardAnswer
import com.example.models.entities.FlashcardLocationPriority
import io.ktor.serialization.kotlinx.json.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.Database
import com.example.models.entities.Flashcards
import com.example.models.entities.Locations
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import org.slf4j.event.Level
import com.example.models.entities.Users
import com.example.routes.configureRouting
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.module() {

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    configureHTTP()
    configureSerialization()
    configureMonitoring()
    configureRouting()
    configureDatabase()
    install(ContentNegotiation) {
        json()
    }
    install(CallLogging) {
        level = Level.INFO
    }
}

fun Application.configureDatabase() {
    Database.connect("jdbc:sqlite:data.db", driver = "org.sqlite.JDBC")

    transaction {
        SchemaUtils.create(Flashcards)
        SchemaUtils.create(FlashcardAnswer)
        SchemaUtils.create(FlashcardLocationPriority)
        SchemaUtils.create(Users)
        SchemaUtils.create(Locations)
    }
}
