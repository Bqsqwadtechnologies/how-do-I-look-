package com.bqsqwad.howdoilook.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bqsqwad.howdoilook.core.data.repository.LookRepository
import com.bqsqwad.howdoilook.core.domain.model.LookRating
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: LookRepository
) : ViewModel() {

    val ratings: StateFlow<List<LookRating>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun delete(id: String) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }
}
