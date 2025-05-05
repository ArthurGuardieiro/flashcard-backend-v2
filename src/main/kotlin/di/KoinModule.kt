package com.example.di

import com.example.models.algorithm.LocationAwareScheduler
import com.example.models.algorithm.SpacedRepetition
import com.example.viewmodel.*
import org.koin.dsl.module

val appModule = module {
    single { AuthService() }
    single { LocationService() }
    single { LocationAwareScheduler(get()) }
    single { SpacedRepetition(get()) }
    single { FlashcardService(get()) }
    single { FlashcardAnswerService() }
    single { DeckService() }

    // Adicione outros serviços/repositórios aqui conforme necessário
}