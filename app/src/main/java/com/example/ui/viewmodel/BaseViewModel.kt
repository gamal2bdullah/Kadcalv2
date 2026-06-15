package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Base ViewModel class for implementing strict MVI (Model-View-Intent) pattern.
 * Enables clean unidirectional data flow and type-safe UI State management.
 */
abstract class BaseViewModel<State, Event>(initialState: State) : ViewModel() {

    protected val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    /**
     * Handles incoming MVI events from the UI.
     */
    abstract fun onEvent(event: Event)
}
