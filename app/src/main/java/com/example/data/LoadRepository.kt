package com.example.data

import kotlinx.coroutines.flow.Flow

class LoadRepository(private val loadDao: LoadDao) {

    val allLoadsFlow: Flow<List<LoadEntity>> = loadDao.getAllLoadsFlow()

    suspend fun getAllLoads(): List<LoadEntity> {
        return loadDao.getAllLoads()
    }

    suspend fun insertLoad(load: LoadEntity) {
        loadDao.insertLoad(load)
    }

    suspend fun insertLoads(loads: List<LoadEntity>) {
        loadDao.insertLoads(loads)
    }

    suspend fun updateLoad(load: LoadEntity) {
        loadDao.updateLoad(load)
    }

    suspend fun deleteLoad(load: LoadEntity) {
        loadDao.deleteLoad(load)
    }

    suspend fun deleteLoadById(id: String) {
        loadDao.deleteLoadById(id)
    }

    suspend fun deleteAllLoads() {
        loadDao.deleteAllLoads()
    }
}
