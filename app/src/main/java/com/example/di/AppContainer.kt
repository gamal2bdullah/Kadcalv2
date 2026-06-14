package com.example.di

import android.content.Context
import android.content.SharedPreferences
import com.example.data.AppDatabase
import com.example.data.LoadRepository
import com.example.domain.usecase.*

interface AppContainer {
    val repository: LoadRepository
    val getLoadsUseCase: GetLoadsUseCase
    val addLoadUseCase: AddLoadUseCase
    val updateLoadUseCase: UpdateLoadUseCase
    val deleteLoadUseCase: DeleteLoadUseCase
    val clearAllLoadsUseCase: ClearAllLoadsUseCase
    val loadPresetUseCase: LoadPresetUseCase
    val getSummaryUseCase: GetSummaryUseCase
    val validateAllLoadsUseCase: ValidateAllLoadsUseCase
    val sharedPreferences: SharedPreferences
}

class AppContainerImpl(private val context: Context) : AppContainer {
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val repository: LoadRepository by lazy {
        LoadRepository(database.loadDao())
    }

    override val getLoadsUseCase: GetLoadsUseCase by lazy {
        GetLoadsUseCase(repository)
    }

    override val addLoadUseCase: AddLoadUseCase by lazy {
        AddLoadUseCase(repository)
    }

    override val updateLoadUseCase: UpdateLoadUseCase by lazy {
        UpdateLoadUseCase(repository)
    }

    override val deleteLoadUseCase: DeleteLoadUseCase by lazy {
        DeleteLoadUseCase(repository)
    }

    override val clearAllLoadsUseCase: ClearAllLoadsUseCase by lazy {
        ClearAllLoadsUseCase(repository)
    }

    override val loadPresetUseCase: LoadPresetUseCase by lazy {
        LoadPresetUseCase(repository)
    }

    override val getSummaryUseCase: GetSummaryUseCase by lazy {
        GetSummaryUseCase()
    }

    override val validateAllLoadsUseCase: ValidateAllLoadsUseCase by lazy {
        ValidateAllLoadsUseCase()
    }

    override val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("itel_solar_prefs", Context.MODE_PRIVATE)
    }
}
