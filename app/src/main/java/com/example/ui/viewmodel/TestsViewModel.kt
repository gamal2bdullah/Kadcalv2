package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.domain.TestSuite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TestsViewModel : ViewModel() {

    private val _testSuiteResult = MutableStateFlow<TestSuite.SuiteResult?>(null)
    val testSuiteResult: StateFlow<TestSuite.SuiteResult?> = _testSuiteResult.asStateFlow()

    fun runTestSuite() {
        viewModelScope.launch {
            _testSuiteResult.value = TestSuite.runAllTests()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return TestsViewModel() as T
            }
        }
    }
}
