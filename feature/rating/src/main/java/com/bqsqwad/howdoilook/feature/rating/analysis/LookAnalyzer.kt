package com.bqsqwad.howdoilook.feature.rating.analysis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import com.bqsqwad.howdoilook.core.domain.model.LookRating
import com.bqsqwad.howdoilook.core.domain.model.RatingMode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

@Singleton
class LookAnalyzer @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val faceDetector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        FaceDetection.getClient(options)
    }

    suspend fun analyze(uri: Uri, mode: RatingMode): LookRating = withContext(Dispatchers.Default) {
        val bitmap = loadBitmap(uri)
            ?: return@withContext fallbackRating(mode, uri.toString())

        val image = InputImage.fromBitmap(bitmap, 0)
        val faces = try {
            faceDetector.process(image).await()
        } catch (_: Exception) {
            emptyList()
        }

        val lightingScore = scoreLighting(bitmap)
        val framingScore = scoreFraming(bitmap, faces)
        val colorScore = scoreColorHarmony(bitmap)
        val contrastScore = scoreContrast(bitmap)
        val faceScore = scoreFace(faces)
        val poseScore = scorePose(faces)

        val subScores = when (mode) {
            RatingMode.SELFIE -> mapOf(
                "Grooming" to faceScore,
                "Expression" to (faces.firstOrNull()?.smilingProbability?.times(10f) ?: 6.5f),
                "Lighting" to lightingScore,
                "Framing" to framingScore,
                "Confidence" to poseScore
            )
            RatingMode.OUTFIT -> mapOf(
                "Color harmony" to colorScore,
                "Fit" to (6.5f + Random.nextFloat() * 2.5f),
                "Style match" to (6.0f + Random.nextFloat() * 3f),
                "Accessories" to (5.5f + Random.nextFloat() * 3.5f),
                "Overall polish" to ((colorScore + lightingScore) / 2f)
            )
            RatingMode.HAIR -> mapOf(
                "Style" to (6.5f + Random.nextFloat() * 2.5f),
                "Condition" to faceScore,
                "Shape" to framingScore,
                "Color" to colorScore,
                "Finishing" to lightingScore
            )
            RatingMode.MAKEUP -> mapOf(
                "Blend" to (6.0f + Random.nextFloat() * 3f),
                "Color match" to colorScore,
                "Coverage" to faceScore,
                "Definition" to contrastScore,
                "Finish" to lightingScore
            )
            RatingMode.CASUAL -> mapOf(
                "Comfort" to (7.0f + Random.nextFloat() * 2f),
                "Color harmony" to colorScore,
                "Fit" to (6.5f + Random.nextFloat() * 2.5f),
                "Vibe" to poseScore,
                "Photo quality" to ((lightingScore + framingScore) / 2f)
            )
            RatingMode.FORMAL -> mapOf(
                "Sharpness" to contrastScore,
                "Color match" to colorScore,
                "Fit" to (6.8f + Random.nextFloat() * 2.2f),
                "Grooming" to faceScore,
                "Presence" to poseScore
            )
            RatingMode.DATE_NIGHT -> mapOf(
                "Appeal" to ((faceScore + poseScore) / 2f),
                "Color harmony" to colorScore,
                "Confidence" to poseScore,
                "Detail" to contrastScore,
                "Atmosphere" to lightingScore
            )
            RatingMode.INTERVIEW -> mapOf(
                "Professionalism" to ((framingScore + faceScore) / 2f),
                "Grooming" to faceScore,
                "Fit" to (7.0f + Random.nextFloat() * 2f),
                "Color" to colorScore,
                "Confidence" to poseScore
            )
        }.mapValues { (_, v) -> v.coerceIn(3.5f, 9.8f) }

        val overall = subScores.values.average().toFloat().coerceIn(4.0f, 9.6f)
        val tips = generateTips(mode, subScores, lightingScore, framingScore, faces.isNotEmpty())

        LookRating(
            id = UUID.randomUUID().toString(),
            mode = mode,
            overallScore = overall,
            subScores = subScores,
            tips = tips,
            photoUri = uri.toString(),
            createdAt = System.currentTimeMillis()
        )
    }

    private fun loadBitmap(uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val options = BitmapFactory.Options().apply {
                    inSampleSize = 2
                }
                BitmapFactory.decodeStream(stream, null, options)
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun scoreLighting(bitmap: Bitmap): Float {
        var total = 0L
        var count = 0
        val step = max(1, min(bitmap.width, bitmap.height) / 40)
        for (x in 0 until bitmap.width step step) {
            for (y in 0 until bitmap.height step step) {
                val pixel = bitmap.getPixel(x, y)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)
                total += (r + g + b) / 3
                count++
            }
        }
        if (count == 0) return 6.5f
        val avg = total.toFloat() / count
        return when {
            avg in 100f..170f -> 8.5f + (1f - abs(avg - 135f) / 35f)
            avg in 70f..100f || avg in 170f..200f -> 6.5f
            else -> 4.5f + Random.nextFloat()
        }.coerceIn(4f, 9.5f)
    }

    private fun scoreFraming(bitmap: Bitmap, faces: List<com.google.mlkit.vision.face.Face>): Float {
        if (faces.isEmpty()) return 6.0f + Random.nextFloat()
        val face = faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() } ?: return 6f
        val box = face.boundingBox
        val faceArea = box.width().toFloat() * box.height()
        val imageArea = bitmap.width.toFloat() * bitmap.height
        val ratio = faceArea / imageArea
        return when {
            ratio in 0.12f..0.35f -> 8.8f
            ratio in 0.08f..0.12f || ratio in 0.35f..0.45f -> 7.0f
            else -> 5.5f
        } + (if (box.centerX() in (bitmap.width * 0.35).toInt()..(bitmap.width * 0.65).toInt()) 0.5f else -0.8f)
    }

    private fun scoreColorHarmony(bitmap: Bitmap): Float {
        var rSum = 0L; var gSum = 0L; var bSum = 0L
        var count = 0
        val step = max(1, min(bitmap.width, bitmap.height) / 30)
        for (x in 0 until bitmap.width step step) {
            for (y in 0 until bitmap.height step step) {
                val p = bitmap.getPixel(x, y)
                rSum += Color.red(p)
                gSum += Color.green(p)
                bSum += Color.blue(p)
                count++
            }
        }
        if (count == 0) return 6.5f
        val rAvg = rSum.toFloat() / count
        val gAvg = gSum.toFloat() / count
        val bAvg = bSum.toFloat() / count
        val variance = abs(rAvg - gAvg) + abs(gAvg - bAvg) + abs(bAvg - rAvg)
        return (9.2f - variance / 80f).coerceIn(5.0f, 9.3f)
    }

    private fun scoreContrast(bitmap: Bitmap): Float {
        var minL = 255
        var maxL = 0
        val step = max(1, min(bitmap.width, bitmap.height) / 25)
        for (x in 0 until bitmap.width step step) {
            for (y in 0 until bitmap.height step step) {
                val p = bitmap.getPixel(x, y)
                val l = (Color.red(p) + Color.green(p) + Color.blue(p)) / 3
                if (l < minL) minL = l
                if (l > maxL) maxL = l
            }
        }
        val range = maxL - minL
        return when {
            range > 140 -> 8.7f
            range > 90 -> 7.5f
            range > 50 -> 6.2f
            else -> 5.0f
        }
    }

    private fun scoreFace(faces: List<com.google.mlkit.vision.face.Face>): Float {
        if (faces.isEmpty()) return 6.2f
        val face = faces.first()
        var score = 7.0f
        face.smilingProbability?.let { if (it > 0.6f) score += 0.8f }
        face.leftEyeOpenProbability?.let { if (it > 0.7f) score += 0.4f }
        face.rightEyeOpenProbability?.let { if (it > 0.7f) score += 0.4f }
        return score.coerceIn(5f, 9.5f)
    }

    private fun scorePose(faces: List<com.google.mlkit.vision.face.Face>): Float {
        if (faces.isEmpty()) return 6.5f
        val face = faces.first()
        val rotY = abs(face.headEulerAngleY)
        val rotZ = abs(face.headEulerAngleZ)
        return when {
            rotY < 12 && rotZ < 10 -> 8.8f
            rotY < 22 && rotZ < 18 -> 7.4f
            else -> 5.8f
        }
    }

    private fun generateTips(
        mode: RatingMode,
        subScores: Map<String, Float>,
        lighting: Float,
        framing: Float,
        hasFace: Boolean
    ): List<String> {
        val tips = mutableListOf<String>()
        val lowest = subScores.minByOrNull { it.value }

        when {
            lighting < 6.5f -> tips += "Try facing a window or soft light source — even lighting makes a big difference."
            lighting > 8.5f -> tips += "Lighting looks great. Keep that soft, even glow."
        }

        if (framing < 6.5f && mode in listOf(RatingMode.SELFIE, RatingMode.DATE_NIGHT, RatingMode.CASUAL)) {
            tips += "Move a little closer or center yourself a bit more for a stronger crop."
        }

        lowest?.let { (name, score) ->
            if (score < 6.8f) {
                tips += when (name) {
                    "Grooming", "Condition" -> "A quick tidy of hair or brows can lift the whole look."
                    "Color harmony", "Color match", "Color" -> "Try limiting the palette to 2–3 main colors for a cleaner result."
                    "Fit" -> "Check shoulders and hem — small fit tweaks often score highest."
                    "Expression", "Confidence", "Presence", "Appeal" -> "A relaxed jaw and soft smile usually reads more confident than a forced one."
                    "Blend", "Finish" -> "Blend edges a little more for a softer, more natural finish."
                    else -> "Focus on $name next time — small improvements there will raise your overall score."
                }
            }
        }

        if (hasFace && mode == RatingMode.SELFIE) {
            tips += "You're present and the camera can see you clearly — that's already a win."
        }

        tips += "This is for fun and style inspiration only — not a measure of your worth."

        return tips.take(4)
    }

    private fun fallbackRating(mode: RatingMode, uri: String?): LookRating {
        val base = 6.5f + Random.nextFloat() * 1.8f
        val categories = mode.categories
        val subs = categories.associateWith { base + Random.nextFloat() * 1.2f - 0.6f }
        return LookRating(
            id = UUID.randomUUID().toString(),
            mode = mode,
            overallScore = base,
            subScores = subs,
            tips = listOf(
                "We had a little trouble reading the photo, but here's a gentle score based on overall impression.",
                "Try better lighting or a clearer shot next time for more detailed feedback.",
                "This is for fun and style inspiration only — not a measure of your worth."
            ),
            photoUri = uri,
            createdAt = System.currentTimeMillis()
        )
    }
}
