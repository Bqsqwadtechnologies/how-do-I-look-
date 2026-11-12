package com.bqsqwad.howdoilook.feature.rating.analysis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import com.bqsqwad.howdoilook.core.domain.model.LookRating
import com.bqsqwad.howdoilook.core.domain.model.RatingMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Gives deterministic feedback about measurable photo characteristics only.
 * It does not score a person's face, body, attractiveness, grooming, or worth.
 */
@Singleton
class LookAnalyzer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    @Suppress("UNUSED_PARAMETER")
    suspend fun analyze(uri: Uri, mode: RatingMode): LookRating = withContext(Dispatchers.Default) {
        val bitmap = loadBitmap(uri)
            ?: throw IllegalArgumentException("The selected image could not be opened. Please choose another photo.")

        try {
            val metrics = measure(bitmap)
            val subScores = linkedMapOf(
                "Lighting" to scoreLighting(metrics.averageBrightness),
                "Contrast" to scoreContrast(metrics.brightnessDeviation),
                "Color balance" to scoreColorBalance(metrics.averageSaturation),
                "Image detail" to scoreDetail(metrics.averageEdgeChange),
                "Crop readiness" to scoreCrop(bitmap.width, bitmap.height)
            )
            val tips = buildTips(subScores, metrics.averageBrightness, metrics.brightnessDeviation)

            LookRating(
                id = UUID.randomUUID().toString(),
                mode = mode,
                overallScore = subScores.values.average().toFloat().coerceIn(1f, 10f),
                subScores = subScores,
                tips = tips,
                photoUri = uri.toString(),
                createdAt = System.currentTimeMillis()
            )
        } finally {
            bitmap.recycle()
        }
    }

    private fun loadBitmap(uri: Uri): Bitmap? = try {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { it2 ->
                BitmapFactory.decodeStream(it2, null, bounds)
            }
            val longestEdge = max(bounds.outWidth, bounds.outHeight)
            val sample = when {
                longestEdge <= 1600 -> 1
                longestEdge <= 3200 -> 2
                longestEdge <= 6400 -> 4
                else -> 8
            }
            val options = BitmapFactory.Options().apply { inSampleSize = sample }
            BitmapFactory.decodeStream(stream, null, options)
        }
    } catch (_: Exception) {
        null
    }

    private data class PhotoMetrics(
        val averageBrightness: Float,
        val brightnessDeviation: Float,
        val averageSaturation: Float,
        val averageEdgeChange: Float
    )

    private fun measure(bitmap: Bitmap): PhotoMetrics {
        val step = max(1, min(bitmap.width, bitmap.height) / 80)
        var count = 0
        var brightnessSum = 0.0
        var brightnessSquaredSum = 0.0
        var saturationSum = 0.0
        var edgeChangeSum = 0.0
        var edgeCount = 0

        var y = 0
        while (y < bitmap.height) {
            var x = 0
            while (x < bitmap.width) {
                val pixel = bitmap.getPixel(x, y)
                val brightness = (Color.red(pixel) * 0.299 +
                    Color.green(pixel) * 0.587 +
                    Color.blue(pixel) * 0.114)
                val hsv = FloatArray(3)
                Color.colorToHSV(pixel, hsv)
                brightnessSum += brightness
                brightnessSquaredSum += brightness * brightness
                saturationSum += hsv[1] * 100.0
                count++

                if (x + step < bitmap.width) {
                    val next = bitmap.getPixel(x + step, y)
                    val nextBrightness = (Color.red(next) * 0.299 +
                        Color.green(next) * 0.587 +
                        Color.blue(next) * 0.114)
                    edgeChangeSum += abs(brightness - nextBrightness)
                    edgeCount++
                }
                x += step
            }
            y += step
        }

        if (count == 0) {
            throw IllegalArgumentException("The selected image has no readable pixels.")
        }
        val average = brightnessSum / count
        val variance = (brightnessSquaredSum / count - average * average).coerceAtLeast(0.0)
        return PhotoMetrics(
            averageBrightness = average.toFloat(),
            brightnessDeviation = sqrt(variance).toFloat(),
            averageSaturation = (saturationSum / count).toFloat(),
            averageEdgeChange = if (edgeCount == 0) 0f else (edgeChangeSum / edgeCount).toFloat()
        )
    }

    private fun scoreLighting(brightness: Float): Float =
        (10f - abs(brightness - 137f) / 24f).coerceIn(1f, 10f)

    private fun scoreContrast(deviation: Float): Float =
        (10f - abs(deviation - 48f) / 13f).coerceIn(1f, 10f)

    private fun scoreColorBalance(saturation: Float): Float =
        (10f - abs(saturation - 52f) / 16f).coerceIn(1f, 10f)

    private fun scoreDetail(edgeChange: Float): Float =
        (4f + (edgeChange / 16f)).coerceIn(1f, 10f)

    private fun scoreCrop(width: Int, height: Int): Float {
        val ratio = width.toFloat() / height.coerceAtLeast(1)
        return when {
            ratio in 0.55f..1.25f -> 8.5f
            ratio in 0.4f..1.6f -> 7f
            else -> 6f
        }
    }

    private fun buildTips(
        scores: Map<String, Float>,
        brightness: Float,
        contrast: Float
    ): List<String> {
        val tips = mutableListOf<String>()
        if (brightness < 6.5f) {
            tips += if (brightness < 4f) {
                "Try a brighter, evenly lit place and avoid strong backlighting."
            } else {
                "A little more even light may make the photo easier to see."
            }
        } else {
            tips += "The photo has a useful amount of light."
        }
        if ((scores["Contrast"] ?: 10f) < 6.5f) {
            tips += "Try separating the subject from the background with softer, clearer lighting."
        }
        if ((scores["Image detail"] ?: 10f) < 6.5f) {
            tips += "Keep the camera steady and tap to focus before taking the next photo."
        }
        if ((scores["Color balance"] ?: 10f) < 6.5f) {
            tips += "Neutral daylight can help the camera capture colors more consistently."
        }
        if ((scores["Crop readiness"] ?: 10f) < 6.5f) {
            tips += "Leave a little space around the subject so the image is easier to crop."
        }
        if (tips.size < 3 && contrast >= 0f) {
            tips += "These notes describe photo characteristics only, not your appearance."
        }
        return tips.take(4)
    }
}
