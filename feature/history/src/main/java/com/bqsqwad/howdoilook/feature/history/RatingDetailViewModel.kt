package com.bqsqwad.howdoilook.feature.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bqsqwad.howdoilook.core.data.repository.LookRepository
import com.bqsqwad.howdoilook.core.domain.model.LookRating
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RatingDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: LookRepository
) : ViewModel() {
    private val _rating = MutableStateFlow<LookRating?>(null)
    val rating: StateFlow<LookRating?> = _rating.asStateFlow()

    init {
        val ratingId = savedStateHandle.get<String>("ratingId")
        if (!ratingId.isNullOrBlank()) {
            viewModelScope.launch {
                _rating.value = repository.getById(ratingId)
            }
        }
    }
}
