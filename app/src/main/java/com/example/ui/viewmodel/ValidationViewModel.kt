package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.SolarApplication
import com.example.domain.ValidationRules
import com.example.domain.usecase.GetLoadsUseCase
import com.example.domain.usecase.ValidateAllLoadsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ValidationViewModel(
    private val getLoadsUseCase: GetLoadsUseCase,
    private val validateAllLoadsUseCase: ValidateAllLoadsUseCase
) : ViewModel() {

    val validationIssues: StateFlow<List<ValidationRules.RuleIssue>> = getLoadsUseCase()
        .map { list ->
            validateAllLoadsUseCase(list)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val validationMatrix: StateFlow<ValidationRules.ValidationMatrix> = getLoadsUseCase()
        .map { list ->
            ValidationRules.getValidationMatrix(list)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ValidationRules.ValidationMatrix(0, emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        )

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as SolarApplication
                val container = application.appContainer
                return ValidationViewModel(
                    getLoadsUseCase = container.getLoadsUseCase,
                    validateAllLoadsUseCase = container.validateAllLoadsUseCase
                ) as T
            }
        }
    }
}
