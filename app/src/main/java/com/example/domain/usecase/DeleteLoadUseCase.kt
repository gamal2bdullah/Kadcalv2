package com.example.domain.usecase

import com.example.data.LoadEntity
import com.example.data.LoadRepository
import com.example.core.logging.SolarLogger

class DeleteLoadUseCase(private val repository: LoadRepository) {
    suspend operator fun invoke(load: LoadEntity) {
        SolarLogger.d("DeleteLoadUseCase", "Deleting load: ${load.loadTag}")
        repository.deleteLoad(load)
    }
}
