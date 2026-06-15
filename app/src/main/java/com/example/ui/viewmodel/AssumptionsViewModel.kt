package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

data class AssumptionsState(
    val searchQuery: String = "",
    val expandedPolicyId: String? = null
)

sealed interface AssumptionsEvent {
    data class SetSearchQuery(val query: String) : AssumptionsEvent
    data class ToggleExpandPolicy(val id: String) : AssumptionsEvent
}

class AssumptionsViewModel : BaseViewModel<AssumptionsState, AssumptionsEvent>(AssumptionsState()) {

    override fun onEvent(event: AssumptionsEvent) {
        when (event) {
            is AssumptionsEvent.SetSearchQuery -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
            }
            is AssumptionsEvent.ToggleExpandPolicy -> {
                val current = _uiState.value.expandedPolicyId
                _uiState.value = _uiState.value.copy(
                    expandedPolicyId = if (current == event.id) null else event.id
                )
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
                return AssumptionsViewModel() as T
            }
        }
    }
}
