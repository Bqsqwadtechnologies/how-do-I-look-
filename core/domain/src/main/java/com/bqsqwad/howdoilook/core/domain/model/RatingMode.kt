package com.bqsqwad.howdoilook.core.domain.model

enum class RatingMode(val displayName: String, val categories: List<String>) {
    SELFIE(
        "Selfie",
        listOf("Grooming", "Expression", "Lighting", "Framing", "Confidence")
    ),
    OUTFIT(
        "Outfit",
        listOf("Color harmony", "Fit", "Style match", "Accessories", "Overall polish")
    ),
    HAIR(
        "Hair",
        listOf("Style", "Condition", "Shape", "Color", "Finishing")
    ),
    MAKEUP(
        "Makeup",
        listOf("Blend", "Color match", "Coverage", "Definition", "Finish")
    ),
    CASUAL(
        "Casual",
        listOf("Comfort", "Color harmony", "Fit", "Vibe", "Photo quality")
    ),
    FORMAL(
        "Formal",
        listOf("Sharpness", "Color match", "Fit", "Grooming", "Presence")
    ),
    DATE_NIGHT(
        "Date Night",
        listOf("Appeal", "Color harmony", "Confidence", "Detail", "Atmosphere")
    ),
    INTERVIEW(
        "Interview",
        listOf("Professionalism", "Grooming", "Fit", "Color", "Confidence")
    );

    companion object {
        fun fromName(name: String): RatingMode =
            entries.find { it.name.equals(name, ignoreCase = true) } ?: SELFIE
    }
}
