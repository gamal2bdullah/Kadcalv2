package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

data class LibraryState(
    val searchQuery: String = ""
)

sealed interface LibraryEvent {
    data class SetSearchQuery(val query: String) : LibraryEvent
}

class LibraryViewModel : BaseViewModel<LibraryState, LibraryEvent>(LibraryState()) {

    override fun onEvent(event: LibraryEvent) {
        when (event) {
            is LibraryEvent.SetSearchQuery -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
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
                return LibraryViewModel() as T
            }
        }
    }
}
