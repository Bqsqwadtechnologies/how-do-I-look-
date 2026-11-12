package com.bqsqwad.howdoilook.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bqsqwad.howdoilook.core.data.preferences.UserPreferences
import com.bqsqwad.howdoilook.core.data.repository.LookRepository
import com.bqsqwad.howdoilook.core.domain.model.LookRating
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: LookRepository,
    preferences: UserPreferences
) : ViewModel() {

    val recent: StateFlow<List<LookRating>> = repository.observeRecent(8)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val streak: StateFlow<Int> = preferences.streakDays
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}
