package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

data class ReportsState(
    val reportViewType: String = "exec" // exec, demand, seasonal, compliance, custom
)

sealed interface ReportsEvent {
    data class SetViewType(val type: String) : ReportsEvent
}

class ReportsViewModel : BaseViewModel<ReportsState, ReportsEvent>(ReportsState()) {

    override fun onEvent(event: ReportsEvent) {
        when (event) {
            is ReportsEvent.SetViewType -> {
                _uiState.value = _uiState.value.copy(reportViewType = event.type)
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
                return ReportsViewModel() as T
            }
        }
    }
}
