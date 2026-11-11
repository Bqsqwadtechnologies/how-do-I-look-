package com.bqsqwad.howdoilook.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.bqsqwad.howdoilook.core.domain.model.LookRating
import com.bqsqwad.howdoilook.core.domain.model.RatingMode
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "look_ratings")
@TypeConverters(Converters::class)
data class LookRatingEntity(
    @PrimaryKey val id: String,
    val mode: String,
    val overallScore: Float,
    val subScoresJson: String,
    val tipsJson: String,
    val photoUri: String?,
    val createdAt: Long,
    val isFavorite: Boolean = false,
    val occasionTag: String? = null,
    val notes: String? = null
) {
    fun toDomain(): LookRating = LookRating(
        id = id,
        mode = RatingMode.fromName(mode),
        overallScore = overallScore,
        subScores = Json.decodeFromString(subScoresJson),
        tips = Json.decodeFromString(tipsJson),
        photoUri = photoUri,
        createdAt = createdAt,
        isFavorite = isFavorite,
        occasionTag = occasionTag,
        notes = notes
    )

    companion object {
        fun fromDomain(rating: LookRating) = LookRatingEntity(
            id = rating.id,
            mode = rating.mode.name,
            overallScore = rating.overallScore,
            subScoresJson = Json.encodeToString(rating.subScores),
            tipsJson = Json.encodeToString(rating.tips),
            photoUri = rating.photoUri,
            createdAt = rating.createdAt,
            isFavorite = rating.isFavorite,
            occasionTag = rating.occasionTag,
            notes = rating.notes
        )
    }
}

class Converters {
    @TypeConverter
    fun fromStringList(value: String): List<String> =
        try { Json.decodeFromString(value) } catch (_: Exception) { emptyList() }

    @TypeConverter
    fun toStringList(list: List<String>): String = Json.encodeToString(list)
}
