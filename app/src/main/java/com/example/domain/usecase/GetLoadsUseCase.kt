package com.example.domain.usecase

import com.example.data.LoadEntity
import com.example.data.LoadRepository
import kotlinx.coroutines.flow.Flow

class GetLoadsUseCase(private val repository: LoadRepository) {
    operator fun invoke(): Flow<List<LoadEntity>> {
        return repository.allLoadsFlow
    }
}
