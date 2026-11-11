package com.bqsqwad.howdoilook.feature.rating

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bqsqwad.howdoilook.core.data.preferences.UserPreferences
import com.bqsqwad.howdoilook.core.data.repository.LookRepository
import com.bqsqwad.howdoilook.core.domain.model.LookRating
import com.bqsqwad.howdoilook.core.domain.model.RatingMode
import com.bqsqwad.howdoilook.feature.rating.analysis.LookAnalyzer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class RatingUiState(
    val selectedMode: RatingMode = RatingMode.SELFIE,
    val isAnalyzing: Boolean = false,
    val currentRating: LookRating? = null,
    val errorMessage: String? = null,
    val photoUri: Uri? = null
)

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val analyzer: LookAnalyzer,
    private val repository: LookRepository,
    private val preferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(RatingUiState())
    val uiState: StateFlow<RatingUiState> = _uiState.asStateFlow()

    fun selectMode(mode: RatingMode) {
        _uiState.update { it.copy(selectedMode = mode, currentRating = null, errorMessage = null) }
    }

    fun onPhotoSelected(uri: Uri) {
        _uiState.update { it.copy(photoUri = uri, currentRating = null, errorMessage = null) }
        analyze(uri)
    }

    private fun analyze(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true, errorMessage = null) }
            try {
                val rating = analyzer.analyze(uri, _uiState.value.selectedMode)
                _uiState.update {
                    it.copy(isAnalyzing = false, currentRating = rating)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAnalyzing = false,
                        errorMessage = "Something went wrong analysing the photo. Please try another one."
                    )
                }
            }
        }
    }

    fun saveToWardrobe() {
        val rating = _uiState.value.currentRating ?: return
        viewModelScope.launch {
            repository.save(rating)
            preferences.updateStreak(LocalDate.now().toEpochDay())
        }
    }

    fun toggleFavorite() {
        val rating = _uiState.value.currentRating ?: return
        val updated = rating.copy(isFavorite = !rating.isFavorite)
        _uiState.update { it.copy(currentRating = updated) }
        viewModelScope.launch {
            repository.save(updated)
        }
    }

    fun clearResult() {
        _uiState.update {
            it.copy(currentRating = null, photoUri = null, isAnalyzing = false, errorMessage = null)
        }
    }
}
