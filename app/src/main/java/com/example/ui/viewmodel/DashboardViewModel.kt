package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.SolarApplication
import com.example.core.logging.SolarLogger
import com.example.core.result.SolarResult
import com.example.core.result.UiState
import com.example.domain.Calculations
import com.example.domain.usecase.GetLoadsUseCase
import com.example.domain.usecase.GetSummaryUseCase
import kotlinx.coroutines.flow.*

/**
 * Dashboard screen viewmodel. Recalculates and emits the cached solar summary
 * as the underlying database entities fluctuate.
 */
class DashboardViewModel(
    private val getLoadsUseCase: GetLoadsUseCase,
    private val getSummaryUseCase: GetSummaryUseCase
) : BaseViewModel<UiState<Calculations.Summary>, Unit>(UiState.Loading) {

    private val tag = "DashboardViewModel"

    val summaryState: StateFlow<UiState<Calculations.Summary>> = getLoadsUseCase()
        .map { list ->
            SolarLogger.d(tag, "Recalculating cached solar summary for ${list.size} loads...")
            if (list.isEmpty()) {
                UiState.Empty
            } else {
                when (val result = getSummaryUseCase(list)) {
                    is SolarResult.Success -> UiState.Success(result.data)
                    is SolarResult.Failure -> UiState.Error(result.error)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    override fun onEvent(event: Unit) {
        // No user triggerable events on the dashboard at this level
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as SolarApplication
                val container = application.appContainer
                return DashboardViewModel(
                    getLoadsUseCase = container.getLoadsUseCase,
                    getSummaryUseCase = container.getSummaryUseCase
                ) as T
            }
        }
    }
}
