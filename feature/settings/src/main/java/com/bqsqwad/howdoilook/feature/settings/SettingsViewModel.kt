package com.bqsqwad.howdoilook.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bqsqwad.howdoilook.core.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: UserPreferences
) : ViewModel() {

    val notificationsEnabled: StateFlow<Boolean> = preferences.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val reduceMotion: StateFlow<Boolean> = preferences.reduceMotion
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch { preferences.setNotificationsEnabled(enabled) }
    }

    fun setReduceMotion(enabled: Boolean) {
        viewModelScope.launch { preferences.setReduceMotion(enabled) }
    }
}
