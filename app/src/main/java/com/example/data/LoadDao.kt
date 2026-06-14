package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LoadDao {
    @Query("SELECT * FROM loads")
    fun getAllLoadsFlow(): Flow<List<LoadEntity>>

    @Query("SELECT * FROM loads")
    suspend fun getAllLoads(): List<LoadEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoad(load: LoadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoads(loads: List<LoadEntity>)

    @Update
    suspend fun updateLoad(load: LoadEntity)

    @Delete
    suspend fun deleteLoad(load: LoadEntity)

    @Query("DELETE FROM loads WHERE id = :id")
    suspend fun deleteLoadById(id: String)

    @Query("DELETE FROM loads")
    suspend fun deleteAllLoads()
}
