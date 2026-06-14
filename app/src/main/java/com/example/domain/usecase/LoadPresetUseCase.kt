package com.example.domain.usecase

import com.example.data.LoadRepository
import com.example.domain.ApplianceLibrary
import com.example.core.logging.SolarLogger

class LoadPresetUseCase(private val repository: LoadRepository) {
    suspend operator fun invoke(presetType: String) {
        SolarLogger.d("LoadPresetUseCase", "Replacing current loads with preset: $presetType")
        repository.deleteAllLoads()
        val presetLoads = ApplianceLibrary.makePresetLoads(presetType)
        repository.insertLoads(presetLoads)
    }
}
