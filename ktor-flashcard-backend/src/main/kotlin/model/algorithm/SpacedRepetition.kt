package com.example.models.algorithm

import com.example.models.DTO.request.FlashcardDTO
import kotlin.math.roundToInt
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.logging.Logger

class SpacedRepetition(private val locationAwareScheduler: LocationAwareScheduler) {
    fun calculateRepetition(card: FlashcardDTO, quality: Int, userId: Int, locationId: Int): FlashcardDTO {
        validateQualityFactorInput(quality)

        // Atualiza as prioridades de localização
        card.id?.let { id ->
            locationAwareScheduler.updateLocationPriority(id, locationId)
        }

        val easiness = calculateEasinessFactor(card.easinessFactor, quality)
        val repetitions = calculateRepetitions(quality, card.repetitions)
        val interval = calculateInterval(repetitions, card.interval, easiness)

        // Obtém o próximo local de revisão baseado na prioridade
        val nextLocationId = card.id?.let { id ->
            locationAwareScheduler.getNextReviewLocation(id, userId)
        } ?: locationId  // Use current locationId as fallback

        val cardAfterRepetition = card.withUpdatedRepetitionProperties(
            newRepetitions = repetitions,
            newEasinessFactor = easiness,
            newNextRepetitionDate = calculateNextPracticeDate(interval),
            newInterval = interval,
            newLocationId = nextLocationId
        )
        log.info(cardAfterRepetition.toString())
        return cardAfterRepetition
    }
    private fun validateQualityFactorInput(quality: Int) {
        log.info("Input quality: $quality")
        if (quality < 0 || quality > 5) {
            throw IllegalArgumentException("Provided quality value is invalid ($quality)")
        }
    }

    private fun calculateEasinessFactor(easiness: Float, quality: Int) =
        Math.max(1.3, easiness + 0.1 - (5.0 - quality) * (0.08 + (5.0 - quality) * 0.02)).toFloat()


    private fun calculateRepetitions(quality: Int, cardRepetitions: Int) = if (quality < 3) {
        0
    } else {
        cardRepetitions + 1
    }

    private fun calculateInterval(repetitions: Int, cardInterval: Int, easiness: Float) = when {
        repetitions <= 1 -> 1
        repetitions == 2 -> 6
        else -> (cardInterval * easiness).roundToInt()
    }

    private fun calculateNextPracticeDate(interval: Int): LocalDateTime {
        val now = System.currentTimeMillis()
        val nextPracticeDate = now + dayInMs * interval
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(nextPracticeDate), ZoneId.systemDefault())
    }

    private companion object {
        private val dayInMs = Duration.ofDays(1).toMillis()
        private val log: Logger = Logger.getLogger(SpacedRepetition::class.java.name)
    }
}