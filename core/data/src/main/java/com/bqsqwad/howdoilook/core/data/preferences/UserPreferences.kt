package com.bqsqwad.howdoilook.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val AGE_CONFIRMED = booleanPreferencesKey("age_confirmed")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val STREAK_DAYS = intPreferencesKey("streak_days")
        val LAST_RATING_DAY = longPreferencesKey("last_rating_day")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val REDUCE_MOTION = booleanPreferencesKey("reduce_motion")
        val DEEP_ANALYSIS_UNLOCKED_UNTIL = longPreferencesKey("deep_analysis_until")
        val COMPARE_UNLOCKED_UNTIL = longPreferencesKey("compare_until")
    }

    val ageConfirmed: Flow<Boolean> = context.dataStore.data.map { it[Keys.AGE_CONFIRMED] ?: false }
    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { it[Keys.ONBOARDING_DONE] ?: false }
    val streakDays: Flow<Int> = context.dataStore.data.map { it[Keys.STREAK_DAYS] ?: 0 }
    val themeMode: Flow<String> = context.dataStore.data.map { it[Keys.THEME_MODE] ?: "system" }
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.NOTIFICATIONS_ENABLED] ?: false }
    val reduceMotion: Flow<Boolean> = context.dataStore.data.map { it[Keys.REDUCE_MOTION] ?: false }

    suspend fun setAgeConfirmed(confirmed: Boolean) {
        context.dataStore.edit { it[Keys.AGE_CONFIRMED] = confirmed }
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_DONE] = done }
    }

    suspend fun updateStreak(todayEpochDay: Long) {
        context.dataStore.edit { prefs ->
            val last = prefs[Keys.LAST_RATING_DAY] ?: -1L
            val current = prefs[Keys.STREAK_DAYS] ?: 0
            when {
                last == todayEpochDay -> { }
                last == todayEpochDay - 1 -> prefs[Keys.STREAK_DAYS] = current + 1
                else -> prefs[Keys.STREAK_DAYS] = 1
            }
            prefs[Keys.LAST_RATING_DAY] = todayEpochDay
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setReduceMotion(enabled: Boolean) {
        context.dataStore.edit { it[Keys.REDUCE_MOTION] = enabled }
    }

    suspend fun unlockDeepAnalysis(untilMillis: Long) {
        context.dataStore.edit { it[Keys.DEEP_ANALYSIS_UNLOCKED_UNTIL] = untilMillis }
    }

    suspend fun unlockCompare(untilMillis: Long) {
        context.dataStore.edit { it[Keys.COMPARE_UNLOCKED_UNTIL] = untilMillis }
    }

    val isDeepAnalysisUnlocked: Flow<Boolean> = context.dataStore.data.map {
        (it[Keys.DEEP_ANALYSIS_UNLOCKED_UNTIL] ?: 0L) > System.currentTimeMillis()
    }

    val isCompareUnlocked: Flow<Boolean> = context.dataStore.data.map {
        (it[Keys.COMPARE_UNLOCKED_UNTIL] ?: 0L) > System.currentTimeMillis()
    }
}
