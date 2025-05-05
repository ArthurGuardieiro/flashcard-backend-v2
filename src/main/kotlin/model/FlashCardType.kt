package com.example.models

import kotlinx.serialization.Serializable

@Serializable
enum class FlashcardType {
    FILL_IN_THE_BLANK,
    MULTIPLE_CHOICE,
    FLASHCARD,
    TYPED_ANSWER
}
