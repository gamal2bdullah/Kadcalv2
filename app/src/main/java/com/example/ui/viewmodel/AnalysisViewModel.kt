package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

data class AnalysisState(
    val analysisSubView: String = "hourly" // hourly, seasonal, category, surges
)

sealed interface AnalysisEvent {
    data class SetSubView(val view: String) : AnalysisEvent
}

class AnalysisViewModel : BaseViewModel<AnalysisState, AnalysisEvent>(AnalysisState()) {

    override fun onEvent(event: AnalysisEvent) {
        when (event) {
            is AnalysisEvent.SetSubView -> {
                _uiState.value = _uiState.value.copy(analysisSubView = event.view)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return AnalysisViewModel() as T
            }
        }
    }
}
