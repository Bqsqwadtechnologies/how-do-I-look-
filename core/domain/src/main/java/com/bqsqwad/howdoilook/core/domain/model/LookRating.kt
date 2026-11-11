package com.bqsqwad.howdoilook.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LookRating(
    val id: String,
    val mode: RatingMode,
    val overallScore: Float,          // 0.0 – 10.0
    val subScores: Map<String, Float>,
    val tips: List<String>,
    val photoUri: String?,            // local content URI or file path; null if user hid photo
    val createdAt: Long,
    val isFavorite: Boolean = false,
    val occasionTag: String? = null,
    val notes: String? = null
) {
    val overallRounded: Int get() = overallScore.toInt().coerceIn(0, 10)
}

@Serializable
data class StyleProfile(
    val primaryStyle: String,
    val secondaryStyles: List<String>,
    val preferredColors: List<String>,
    val summary: String,
    val createdAt: Long
)
