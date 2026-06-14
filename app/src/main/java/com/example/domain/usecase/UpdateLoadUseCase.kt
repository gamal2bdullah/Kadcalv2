package com.example.domain.usecase

import com.example.data.LoadEntity
import com.example.data.LoadRepository
import com.example.core.logging.SolarLogger

class UpdateLoadUseCase(private val repository: LoadRepository) {
    suspend operator fun invoke(load: LoadEntity) {
        SolarLogger.d("UpdateLoadUseCase", "Updating load: ${load.loadTag}")
        repository.updateLoad(load)
    }
}
