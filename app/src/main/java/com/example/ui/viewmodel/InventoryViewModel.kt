package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

data class InventoryState(
    val searchQuery: String = "",
    val categoryFilter: String = "All"
)

sealed interface InventoryEvent {
    data class SetSearchQuery(val query: String) : InventoryEvent
    data class SetCategoryFilter(val category: String) : InventoryEvent
}

class InventoryViewModel : BaseViewModel<InventoryState, InventoryEvent>(InventoryState()) {

    override fun onEvent(event: InventoryEvent) {
        when (event) {
            is InventoryEvent.SetSearchQuery -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
            }
            is InventoryEvent.SetCategoryFilter -> {
                _uiState.value = _uiState.value.copy(categoryFilter = event.category)
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
                return InventoryViewModel() as T
            }
        }
    }
}
