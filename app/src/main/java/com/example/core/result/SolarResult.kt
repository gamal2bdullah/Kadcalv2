package com.example.core.result

sealed interface SolarResult<out T> {
    data class Success<out T>(val data: T) : SolarResult<T>
    data class Failure(val error: SolarError) : SolarResult<Nothing>
}

sealed interface SolarError {
    val message: String

    data class DatabaseError(override val message: String, val cause: Throwable? = null) : SolarError
    data class ValidationError(override val message: String, val ruleId: String? = null) : SolarError
    data class CalculationError(override val message: String, val formulaName: String? = null) : SolarError
    data class UnknownError(override val message: String, val cause: Throwable? = null) : SolarError
}

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    object Empty : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val error: SolarError) : UiState<Nothing>
}
