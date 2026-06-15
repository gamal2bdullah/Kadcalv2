package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

data class ScheduleState(
    val searchQuery: String = "",
    val categoryFilter: String = "All",
    val displayAdvanced: Boolean = false
)

sealed interface ScheduleEvent {
    data class SetSearchQuery(val query: String) : ScheduleEvent
    data class SetCategoryFilter(val category: String) : ScheduleEvent
    object ToggleDisplayAdvanced : ScheduleEvent
}

class ScheduleViewModel : BaseViewModel<ScheduleState, ScheduleEvent>(ScheduleState()) {

    override fun onEvent(event: ScheduleEvent) {
        when (event) {
            is ScheduleEvent.SetSearchQuery -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
            }
            is ScheduleEvent.SetCategoryFilter -> {
                _uiState.value = _uiState.value.copy(categoryFilter = event.category)
            }
            is ScheduleEvent.ToggleDisplayAdvanced -> {
                _uiState.value = _uiState.value.copy(displayAdvanced = !_uiState.value.displayAdvanced)
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
                return ScheduleViewModel() as T
            }
        }
    }
}
