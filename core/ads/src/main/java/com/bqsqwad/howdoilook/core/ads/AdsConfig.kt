package com.bqsqwad.howdoilook.core.ads

/**
 * Single source of truth for AdMob unit IDs.
 * Replace the test IDs below with your real production IDs when ready.
 * Never hard-code real IDs anywhere else in the project.
 */
object AdsConfig {

    // Banner (adaptive)
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    // Native
    const val NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"

    // Interstitial
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    // Rewarded
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    // App Open
    const val APP_OPEN_AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"

    // Frequency caps (policy-safe)
    const val INTERSTITIAL_MIN_INTERVAL_MS = 3 * 60 * 1000L // 3 minutes
    const val INTERSTITIAL_EVERY_N_RATINGS = 3
    const val APP_OPEN_MIN_BACKGROUND_MS = 4 * 60 * 60 * 1000L // 4 hours
}
