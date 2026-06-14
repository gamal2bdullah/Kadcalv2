package com.example.domain.usecase

import com.example.data.LoadRepository
import com.example.core.logging.SolarLogger

class ClearAllLoadsUseCase(private val repository: LoadRepository) {
    suspend operator fun invoke() {
        SolarLogger.d("ClearAllLoadsUseCase", "Clearing all loads from DB")
        repository.deleteAllLoads()
    }
}
