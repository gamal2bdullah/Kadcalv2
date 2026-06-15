package com.example.ui.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.SolarApplication
import com.example.data.LoadEntity
import com.example.domain.usecase.*
import com.example.core.logging.SolarLogger
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * SharedViewModel for maintaining globally synchronized states
 * across all modules of the KADcal space sizing system.
 */
class SharedViewModel(
    private val getLoadsUseCase: GetLoadsUseCase,
    private val addLoadUseCase: AddLoadUseCase,
    private val updateLoadUseCase: UpdateLoadUseCase,
    private val deleteLoadUseCase: DeleteLoadUseCase,
    private val clearAllLoadsUseCase: ClearAllLoadsUseCase,
    private val loadPresetUseCase: LoadPresetUseCase,
    private val sharedPrefs: SharedPreferences
) : ViewModel() {

    private val tag = "SharedViewModel"

    private val _sidebarExpanded = MutableStateFlow(true)
    val sidebarExpanded: StateFlow<Boolean> = _sidebarExpanded.asStateFlow()

    private val _loadsList = MutableStateFlow<List<LoadEntity>>(emptyList())
    val loadsList: StateFlow<List<LoadEntity>> = _loadsList.asStateFlow()

    private val _projectName = MutableStateFlow(sharedPrefs.getString("project_name", "My Solar Project") ?: "My Solar Project")
    val projectName: StateFlow<String> = _projectName.asStateFlow()

    private val _expertLevel = MutableStateFlow(sharedPrefs.getString("expert_level", "Professional") ?: "Professional")
    val expertLevel: StateFlow<String> = _expertLevel.asStateFlow()

    private val _highContrast = MutableStateFlow(sharedPrefs.getBoolean("high_contrast_v3", false))
    val highContrast: StateFlow<Boolean> = _highContrast.asStateFlow()

    private val _appLanguage = MutableStateFlow(sharedPrefs.getString("app_language_v3", "ar") ?: "ar")
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    private val _fontSizeAdjustment = MutableStateFlow(sharedPrefs.getString("font_size_v3", "Normal") ?: "Normal")
    val fontSizeAdjustment: StateFlow<String> = _fontSizeAdjustment.asStateFlow()

    // Overlay modal states
    val activeEditingLoad = MutableStateFlow<LoadEntity?>(null)
    val isLibraryModalOpen = MutableStateFlow(false)

    private val _onboardingShown = MutableStateFlow(sharedPrefs.getBoolean("onboarding_shown_v3", false))
    val onboardingShown: StateFlow<Boolean> = _onboardingShown.asStateFlow()

    fun dismissOnboarding() {
        _onboardingShown.value = true
        sharedPrefs.edit().putBoolean("onboarding_shown_v3", true).apply()
    }

    init {
        SolarLogger.i(tag, "SharedViewModel initialized. Subscribed to database stream.")
        viewModelScope.launch {
            getLoadsUseCase().collectLatest { list ->
                _loadsList.value = list
                
                // Seed on first run if database is completely empty
                val isFirstRun = sharedPrefs.getBoolean("is_first_run_v3", true)
                if (isFirstRun && list.isEmpty()) {
                    SolarLogger.i(tag, "Seeding default appliance templates for first-time use...")
                    loadPresetUseCase("basic")
                    sharedPrefs.edit().putBoolean("is_first_run_v3", false).apply()
                }
            }
        }
    }

    fun toggleSidebar() {
        _sidebarExpanded.value = !_sidebarExpanded.value
    }

    fun updateProjectName(name: String) {
        _projectName.value = name
        sharedPrefs.edit().putString("project_name", name).apply()
    }

    fun updateHighContrast(enabled: Boolean) {
        _highContrast.value = enabled
        sharedPrefs.edit().putBoolean("high_contrast_v3", enabled).apply()
    }

    fun updateAppLanguage(lang: String) {
        _appLanguage.value = lang
        sharedPrefs.edit().putString("app_language_v3", lang).apply()
    }

    fun updateFontSizeAdjustment(size: String) {
        _fontSizeAdjustment.value = size
        sharedPrefs.edit().putString("font_size_v3", size).apply()
    }

    fun resetOnboarding() {
        _onboardingShown.value = false
        sharedPrefs.edit().putBoolean("onboarding_shown_v3", false).apply()
    }

    fun updateExpertLevel(level: String) {
        _expertLevel.value = level
        sharedPrefs.edit().putString("expert_level", level).apply()
    }

    fun addLoad(load: LoadEntity) {
        viewModelScope.launch {
            SolarLogger.i(tag, "Adding load configuration: ${load.loadTag}")
            addLoadUseCase(load)
        }
    }

    fun updateLoad(load: LoadEntity) {
        viewModelScope.launch {
            SolarLogger.i(tag, "Updating load configuration: ${load.loadTag}")
            updateLoadUseCase(load)
        }
    }

    fun duplicateLoad(load: LoadEntity) {
        viewModelScope.launch {
            val count = _loadsList.value.size + 1
            val duplicate = load.copy(
                id = "id-${UUID.randomUUID().toString().take(12)}",
                loadId = "LD-${String.format("%04d", count)}",
                loadTag = "${load.loadTag}-CPY"
            )
            SolarLogger.i(tag, "Duplicating load configuration into: ${duplicate.loadTag}")
            addLoadUseCase(duplicate)
        }
    }

    fun deleteLoad(load: LoadEntity) {
        viewModelScope.launch {
            SolarLogger.i(tag, "Deleting load configuration: ${load.loadTag}")
            deleteLoadUseCase(load)
        }
    }

    fun clearAllLoads() {
        viewModelScope.launch {
            SolarLogger.w(tag, "Purging all loads in local project database...")
            clearAllLoadsUseCase()
        }
    }

    fun loadPreset(type: String) {
        viewModelScope.launch {
            SolarLogger.i(tag, "Loading preset configuration template: $type")
            loadPresetUseCase(type)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as SolarApplication
                val container = application.appContainer
                return SharedViewModel(
                    getLoadsUseCase = container.getLoadsUseCase,
                    addLoadUseCase = container.addLoadUseCase,
                    updateLoadUseCase = container.updateLoadUseCase,
                    deleteLoadUseCase = container.deleteLoadUseCase,
                    clearAllLoadsUseCase = container.clearAllLoadsUseCase,
                    loadPresetUseCase = container.loadPresetUseCase,
                    sharedPrefs = container.sharedPreferences
                ) as T
            }
        }
    }
}
