package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.LoadEntity
import com.example.data.LoadRepository
import com.example.domain.ApplianceLibrary
import com.example.domain.Calculations
import com.example.domain.TestSuite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class SolarViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = LoadRepository(db.loadDao())

    private val sharedPrefs = application.getSharedPreferences("itel_solar_prefs", Context.MODE_PRIVATE)

    // State bindings
    private val _currentView = MutableStateFlow("dashboard")
    val currentView: StateFlow<String> = _currentView.asStateFlow()

    private val _sidebarExpanded = MutableStateFlow(true)
    val sidebarExpanded: StateFlow<Boolean> = _sidebarExpanded.asStateFlow()

    private val _loadsList = MutableStateFlow<List<LoadEntity>>(emptyList())
    val loadsList: StateFlow<List<LoadEntity>> = _loadsList.asStateFlow()

    private val _projectName = MutableStateFlow(sharedPrefs.getString("project_name", "My Solar Project") ?: "My Solar Project")
    val projectName: StateFlow<String> = _projectName.asStateFlow()

    private val _expertLevel = MutableStateFlow(sharedPrefs.getString("expert_level", "Professional") ?: "Professional")
    val expertLevel: StateFlow<String> = _expertLevel.asStateFlow()

    // Interactive selections & filters
    val searchInventory = MutableStateFlow("")
    val filterInventoryCategory = MutableStateFlow("All")

    val searchSchedule = MutableStateFlow("")
    val filterScheduleCategory = MutableStateFlow("All")
    val showAdvScheduleCols = MutableStateFlow(false)
    val sortScheduleKey = MutableStateFlow("loadId")
    val sortScheduleAsc = MutableStateFlow(true)

    val searchLibrary = MutableStateFlow("")
    val filterLibraryCategory = MutableStateFlow("All")
    val libraryGroupBy = MutableStateFlow("category") // category, motor, critical

    val searchPolicies = MutableStateFlow("")
    val filterPoliciesConfidence = MutableStateFlow("All")
    val filterPoliciesSource = MutableStateFlow("All")
    val expandedPolicyId = MutableStateFlow<String?>(null)

    val analysisSubView = MutableStateFlow("hourly") // hourly, seasonal, category, surges, critical

    val reportViewType = MutableStateFlow("exec") // exec, demand, seasonal, compliance, custom

    val docActiveSectionId = MutableStateFlow("architecture")

    val testSuiteResult = MutableStateFlow<TestSuite.SuiteResult?>(null)
    val testSuiteFilter = MutableStateFlow("all")

    // Modals
    val activeEditingLoad = MutableStateFlow<LoadEntity?>(null)
    val isLibraryModalOpen = MutableStateFlow(false)

    init {
        // Observe database
        viewModelScope.launch {
            repository.allLoadsFlow.collectLatest { list ->
                if (list.isEmpty()) {
                    // Prepopulate basic loads to welcome users with a live dashboard
                    val preset = ApplianceLibrary.makePresetLoads("basic")
                    repository.insertLoads(preset)
                } else {
                    _loadsList.value = list
                }
            }
        }
    }

    fun navigateTo(view: String) {
        _currentView.value = view
    }

    fun toggleSidebar() {
        _sidebarExpanded.value = !_sidebarExpanded.value
    }

    fun updateProjectName(name: String) {
        _projectName.value = name
        sharedPrefs.edit().putString("project_name", name).apply()
    }

    fun updateExpertLevel(level: String) {
        _expertLevel.value = level
        sharedPrefs.edit().putString("expert_level", level).apply()
    }

    // Load Actions
    fun addLoad(l: LoadEntity) {
        viewModelScope.launch {
            repository.insertLoad(l)
        }
    }

    fun updateLoad(l: LoadEntity) {
        viewModelScope.launch {
            repository.updateLoad(l)
        }
    }

    fun duplicateLoad(l: LoadEntity) {
        viewModelScope.launch {
            val count = _loadsList.value.size + 1
            val duplicate = l.copy(
                id = "id-${UUID.randomUUID().toString().take(12)}",
                loadId = "LD-${String.format("%04d", count)}",
                loadTag = "${l.loadTag}-CPY"
            )
            repository.insertLoad(duplicate)
        }
    }

    fun deleteLoad(l: LoadEntity) {
        viewModelScope.launch {
            repository.deleteLoad(l)
        }
    }

    fun clearAllLoads() {
        viewModelScope.launch {
            repository.deleteAllLoads()
        }
    }

    fun loadPreset(type: String) {
        viewModelScope.launch {
            repository.deleteAllLoads()
            val preset = ApplianceLibrary.makePresetLoads(type)
            repository.insertLoads(preset)
        }
    }

    fun runTestSuite() {
        viewModelScope.launch {
            testSuiteResult.value = TestSuite.runAllTests()
        }
    }
}
