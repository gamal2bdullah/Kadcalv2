package com.example.domain.usecase

import com.example.data.LoadEntity
import com.example.data.LoadRepository
import com.example.core.logging.SolarLogger

class AddLoadUseCase(private val repository: LoadRepository) {
    suspend operator fun invoke(load: LoadEntity) {
        SolarLogger.d("AddLoadUseCase", "Inserting new load: ${load.loadTag}")
        repository.insertLoad(load)
    }
}
