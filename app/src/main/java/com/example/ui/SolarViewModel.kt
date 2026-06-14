package com.example.ui

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.SolarApplication
import com.example.data.LoadEntity
import com.example.domain.Calculations
import com.example.domain.TestSuite
import com.example.domain.ValidationRules
import com.example.domain.usecase.*
import com.example.core.logging.SolarLogger
import com.example.core.result.SolarResult
import com.example.core.result.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class SolarViewModel(
    private val getLoadsUseCase: GetLoadsUseCase,
    private val addLoadUseCase: AddLoadUseCase,
    private val updateLoadUseCase: UpdateLoadUseCase,
    private val deleteLoadUseCase: DeleteLoadUseCase,
    private val clearAllLoadsUseCase: ClearAllLoadsUseCase,
    private val loadPresetUseCase: LoadPresetUseCase,
    private val getSummaryUseCase: GetSummaryUseCase,
    private val validateAllLoadsUseCase: ValidateAllLoadsUseCase,
    private val sharedPrefs: SharedPreferences
) : ViewModel() {

    private val tag = "SolarViewModel"

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

    // Caching/Memoization StateFlow Layer
    val summaryState: StateFlow<UiState<Calculations.Summary>> = _loadsList
        .map { list ->
            SolarLogger.d(tag, "Recalculating cached Solar Summary profile for ${list.size} loads...")
            if (list.isEmpty()) {
                UiState.Empty
            } else {
                when (val result = getSummaryUseCase(list)) {
                    is SolarResult.Success -> UiState.Success(result.data)
                    is SolarResult.Failure -> UiState.Error(result.error)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    val validationIssuesState: StateFlow<List<ValidationRules.RuleIssue>> = _loadsList
        .map { list ->
            SolarLogger.d(tag, "Recalculating Validation and compliance rules for ${list.size} loads...")
            validateAllLoadsUseCase(list)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val validationMatrixState: StateFlow<ValidationRules.ValidationMatrix> = _loadsList
        .map { list ->
            SolarLogger.d(tag, "Recalculating cached compliance matrix for ${list.size} loads...")
            ValidationRules.getValidationMatrix(list)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ValidationRules.ValidationMatrix(0, emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        )

    init {
        SolarLogger.i(tag, "ViewModel initialized. Launching database collector flow.")
        // Observe database
        viewModelScope.launch {
            getLoadsUseCase().collectLatest { list ->
                _loadsList.value = list
                
                // Seed only if it is the very first run
                val isFirstRun = sharedPrefs.getBoolean("is_first_run_v3", true)
                if (isFirstRun && list.isEmpty()) {
                    SolarLogger.i(tag, "First run with empty database detected. Seeding template active...")
                    loadPresetUseCase("basic")
                    sharedPrefs.edit().putBoolean("is_first_run_v3", false).apply()
                }
            }
        }
    }

    fun navigateTo(view: String) {
        SolarLogger.d(tag, "Navigation requested to: $view")
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
            SolarLogger.i(tag, "Adding load into project calculations: ${l.loadTag}")
            addLoadUseCase(l)
        }
    }

    fun updateLoad(l: LoadEntity) {
        viewModelScope.launch {
            SolarLogger.i(tag, "Updating existing load parameter configuration: ${l.loadTag}")
            updateLoadUseCase(l)
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
            SolarLogger.i(tag, "Duplicating load configuration into: ${duplicate.loadTag}")
            addLoadUseCase(duplicate)
        }
    }

    fun deleteLoad(l: LoadEntity) {
        viewModelScope.launch {
            SolarLogger.i(tag, "Removing load configuration: ${l.loadTag}")
            deleteLoadUseCase(l)
        }
    }

    fun clearAllLoads() {
        viewModelScope.launch {
            SolarLogger.w(tag, "User-triggered purge of all loads in database.")
            clearAllLoadsUseCase()
        }
    }

    fun loadPreset(type: String) {
        viewModelScope.launch {
            SolarLogger.i(tag, "Loading appliance scenario preset template: $type")
            loadPresetUseCase(type)
        }
    }

    fun runTestSuite() {
        viewModelScope.launch {
            SolarLogger.d(tag, "Executing local computational unit tests verification suite...")
            testSuiteResult.value = TestSuite.runAllTests()
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
                return SolarViewModel(
                    getLoadsUseCase = container.getLoadsUseCase,
                    addLoadUseCase = container.addLoadUseCase,
                    updateLoadUseCase = container.updateLoadUseCase,
                    deleteLoadUseCase = container.deleteLoadUseCase,
                    clearAllLoadsUseCase = container.clearAllLoadsUseCase,
                    loadPresetUseCase = container.loadPresetUseCase,
                    getSummaryUseCase = container.getSummaryUseCase,
                    validateAllLoadsUseCase = container.validateAllLoadsUseCase,
                    sharedPrefs = container.sharedPreferences
                ) as T
            }
        }
    }
}
