package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.data.LoadEntity
import com.example.ui.*
import com.example.ui.screens.*

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
    viewModel: SolarViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = SolarDestinations.DASHBOARD,
        modifier = modifier
    ) {
        composable(SolarDestinations.DASHBOARD) {
            DashboardScreen(loads = loads, viewModel = viewModel)
        }
        composable(SolarDestinations.INVENTORY) {
            InventoryScreen(loads = loads, viewModel = viewModel)
        }
        composable(SolarDestinations.SCHEDULE) {
            ScheduleScreen(loads = loads, viewModel = viewModel)
        }
        composable(SolarDestinations.ANALYSIS) {
            AnalysisScreen(loads = loads, viewModel = viewModel)
        }
        composable(SolarDestinations.PHASE) {
            PhaseScreen(loads = loads, viewModel = viewModel)
        }
        composable(SolarDestinations.VALIDATION) {
            ValidationScreen(loads = loads, viewModel = viewModel)
        }
        composable(SolarDestinations.ASSUMPTIONS) {
            AssumptionsScreen(viewModel = viewModel)
        }
        composable(SolarDestinations.REPORTS) {
            ReportsScreen(loads = loads, viewModel = viewModel)
        }
        composable(SolarDestinations.LIBRARY) {
            LibraryScreen(loads = loads, viewModel = viewModel)
        }
        composable(SolarDestinations.TESTS) {
            TestsScreen(viewModel = viewModel)
        }
        composable(SolarDestinations.DOCS) {
            DocsScreen()
        }
        composable(SolarDestinations.ABOUT) {
            AboutScreen()
        }
        composable(SolarDestinations.SETTINGS) {
            SettingsScreen(viewModel = viewModel)
        }
    }
}
