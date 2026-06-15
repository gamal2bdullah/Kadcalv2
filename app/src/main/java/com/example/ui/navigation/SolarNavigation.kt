package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.data.LoadEntity
import com.example.ui.screens.*
import com.example.ui.viewmodel.*

object SolarDestinations {
    const val DASHBOARD = "dashboard"
    const val INVENTORY = "inventory"
    const val SCHEDULE = "schedule"
    const val ANALYSIS = "analysis"
    const val PHASE = "phase"
    const val VALIDATION = "validation"
    const val ASSUMPTIONS = "assumptions"
    const val REPORTS = "reports"
    const val LIBRARY = "library"
    const val TESTS = "tests"
    const val DOCS = "docs"
    const val ABOUT = "about"
    const val SETTINGS = "settings"
}

@Composable
fun SolarNavHost(
    navController: NavHostController,
    loads: List<LoadEntity>,
    sharedViewModel: SharedViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = SolarDestinations.DASHBOARD,
        modifier = modifier
    ) {
        composable(SolarDestinations.DASHBOARD) {
            val dashboardViewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory)
            DashboardScreen(loads = loads, dashboardViewModel = dashboardViewModel, sharedViewModel = sharedViewModel)
        }
        composable(SolarDestinations.INVENTORY) {
            val inventoryViewModel: InventoryViewModel = viewModel(factory = InventoryViewModel.Factory)
            InventoryScreen(loads = loads, inventoryViewModel = inventoryViewModel, sharedViewModel = sharedViewModel)
        }
        composable(SolarDestinations.SCHEDULE) {
            val scheduleViewModel: ScheduleViewModel = viewModel(factory = ScheduleViewModel.Factory)
            ScheduleScreen(loads = loads, scheduleViewModel = scheduleViewModel)
        }
        composable(SolarDestinations.ANALYSIS) {
            val analysisViewModel: AnalysisViewModel = viewModel(factory = AnalysisViewModel.Factory)
            val dashboardViewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory)
            AnalysisScreen(loads = loads, analysisViewModel = analysisViewModel, dashboardViewModel = dashboardViewModel)
        }
        composable(SolarDestinations.PHASE) {
            val phaseViewModel: PhaseViewModel = viewModel(factory = PhaseViewModel.Factory)
            PhaseScreen(loads = loads, phaseViewModel = phaseViewModel)
        }
        composable(SolarDestinations.VALIDATION) {
            val validationViewModel: ValidationViewModel = viewModel(factory = ValidationViewModel.Factory)
            ValidationScreen(validationViewModel = validationViewModel)
        }
        composable(SolarDestinations.ASSUMPTIONS) {
            val assumptionsViewModel: AssumptionsViewModel = viewModel(factory = AssumptionsViewModel.Factory)
            AssumptionsScreen(assumptionsViewModel = assumptionsViewModel)
        }
        composable(SolarDestinations.REPORTS) {
            val reportsViewModel: ReportsViewModel = viewModel(factory = ReportsViewModel.Factory)
            val dashboardViewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory)
            ReportsScreen(loads = loads, reportsViewModel = reportsViewModel, dashboardViewModel = dashboardViewModel, sharedViewModel = sharedViewModel)
        }
        composable(SolarDestinations.LIBRARY) {
            val libraryViewModel: LibraryViewModel = viewModel(factory = LibraryViewModel.Factory)
            LibraryScreen(loads = loads, libraryViewModel = libraryViewModel, sharedViewModel = sharedViewModel)
        }
        composable(SolarDestinations.TESTS) {
            val testsViewModel: TestsViewModel = viewModel(factory = TestsViewModel.Factory)
            TestsScreen(testsViewModel = testsViewModel)
        }
        composable(SolarDestinations.DOCS) {
            DocsScreen()
        }
        composable(SolarDestinations.ABOUT) {
            AboutScreen()
        }
        composable(SolarDestinations.SETTINGS) {
            SettingsScreen(sharedViewModel = sharedViewModel)
        }
    }
}
