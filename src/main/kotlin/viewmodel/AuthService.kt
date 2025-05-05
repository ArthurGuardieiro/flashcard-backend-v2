package com.example.viewmodel

import com.example.models.DTO.request.AuthResponse
import com.example.models.DTO.request.RegisterRequest
import com.example.models.DTO.response.LoginResponse
import com.example.models.entities.Users
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class AuthService {
    fun register(request: RegisterRequest): Pair<AuthResponse, HttpStatusCode> {
        require(request.username.isNotBlank()) { "Username cannot be blank" }
        require(request.password.length >= 6) { "Password must be at least 6 characters" }

        val passwordHash = request.password.hashCode().toString()

        val exists = transaction {
            Users.select { Users.username eq request.username }.any()
        }

        return if (exists) {
            Pair(AuthResponse("Usuário já existe"), HttpStatusCode.Conflict)
        } else {
            transaction {
                Users.insert {
                    it[Users.username] = request.username
                    it[Users.passwordHash] = passwordHash
                }
            }
            Pair(AuthResponse("Usuário registrado com sucesso"), HttpStatusCode.Created)
        }
    }

    fun login(request: RegisterRequest): Pair<LoginResponse, HttpStatusCode> {
        require(request.username.isNotBlank()) { "Username cannot be blank" }
        require(request.password.isNotBlank()) { "Password cannot be blank" }

        val passwordHash = request.password.hashCode().toString()

        val user = transaction {
            Users.select {
                Users.username eq request.username and (Users.passwordHash eq passwordHash)
            }.singleOrNull()
        }

        return if (user != null) {
            Pair(
                LoginResponse(
                    message = "Login bem-sucedido",
                    userId = user[Users.id].value
                ),
                HttpStatusCode.OK
            )
        } else {
            Pair(
                LoginResponse(
                    message = "Credenciais inválidas",
                    userId = null
                ),
                HttpStatusCode.Unauthorized
            )
        }
    }
}