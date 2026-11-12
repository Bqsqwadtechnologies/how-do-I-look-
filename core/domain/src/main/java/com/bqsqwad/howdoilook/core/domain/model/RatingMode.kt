package com.bqsqwad.howdoilook.core.domain.model

enum class RatingMode(val displayName: String, val categories: List<String>) {
    SELFIE(
        "Selfie",
        listOf("Lighting", "Contrast", "Color balance", "Image detail", "Crop readiness")
    ),
    OUTFIT(
        "Outfit",
        listOf("Lighting", "Contrast", "Color balance", "Image detail", "Crop readiness")
    ),
    HAIR(
        "Hair",
        listOf("Lighting", "Contrast", "Color balance", "Image detail", "Crop readiness")
    ),
    MAKEUP(
        "Makeup",
        listOf("Lighting", "Contrast", "Color balance", "Image detail", "Crop readiness")
    ),
    CASUAL(
        "Casual",
        listOf("Lighting", "Contrast", "Color balance", "Image detail", "Crop readiness")
    ),
    FORMAL(
        "Formal",
        listOf("Lighting", "Contrast", "Color balance", "Image detail", "Crop readiness")
    ),
    DATE_NIGHT(
        "Date Night",
        listOf("Lighting", "Contrast", "Color balance", "Image detail", "Crop readiness")
    ),
    INTERVIEW(
        "Interview",
        listOf("Lighting", "Contrast", "Color balance", "Image detail", "Crop readiness")
    );

    companion object {
        fun fromName(name: String): RatingMode =
            entries.find { it.name.equals(name, ignoreCase = true) } ?: SELFIE
    }
}
