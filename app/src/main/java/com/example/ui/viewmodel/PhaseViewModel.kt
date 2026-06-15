package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.SolarApplication
import com.example.domain.PhaseBalancer
import com.example.domain.usecase.GetLoadsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PhaseViewModel(
    private val getLoadsUseCase: GetLoadsUseCase
) : ViewModel() {

    val balancingResult: StateFlow<PhaseBalancer.BalancingResult> = getLoadsUseCase()
        .map { list ->
            PhaseBalancer.balancePhases(list)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PhaseBalancer.balancePhases(emptyList())
        )

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as SolarApplication
                return PhaseViewModel(application.appContainer.getLoadsUseCase) as T
            }
        }
    }
}
