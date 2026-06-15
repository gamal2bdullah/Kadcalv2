package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.domain.ApplianceLibrary
import com.example.ui.navigation.SolarDestinations
import com.example.ui.navigation.SolarNavHost
import com.example.ui.theme.*
import com.example.ui.viewmodel.SharedViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarAppLayout(sharedViewModel: SharedViewModel) {
    val sidebarExpanded by sharedViewModel.sidebarExpanded.collectAsState()
    val loads by sharedViewModel.loadsList.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val activeEditing by sharedViewModel.activeEditingLoad.collectAsState()
    val isLibraryOpen by sharedViewModel.isLibraryModalOpen.collectAsState()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    
    // Derived single source of truth for current active view route
    val currentView = navBackStackEntry?.destination?.route ?: SolarDestinations.DASHBOARD

    // Listen for custom globally routed toast events
    LaunchedEffect(Unit) {
        _globalToastChannel.collectLatest { (message, kind) ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useSidebar = maxWidth >= 768.dp

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = CosmicBg,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = sharedViewModel.projectName.collectAsState().value,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.clickable {
                                        _globalToastChannel.tryEmit(Pair("Rename the project in Settings view!", "info"))
                                    }
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(CosmicGreen)
                                        .size(6.dp)
                                )
                            }
                            Text(
                                text = "Compliance Solar calculator",
                                fontSize = 10.sp,
                                color = CosmicMute
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { sharedViewModel.toggleSidebar() }) {
                            Icon(
                                imageVector = if (sidebarExpanded) Icons.Default.Menu else Icons.AutoMirrored.Default.List,
                                contentDescription = "Sidebar Toggle",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf("Basic", "Pro" to "Professional", "Comm" to "Commercial", "Expert").forEach { opt ->
                                val (label, value) = if (opt is Pair<*,*>) {
                                    (opt.first as String) to (opt.second as String)
                                } else {
                                    (opt as String) to opt
                                }
                                val active = sharedViewModel.expertLevel.collectAsState().value == value
                                FilterChip(
                                    selected = active,
                                    onClick = { sharedViewModel.updateExpertLevel(value) },
                                    label = { Text(text = label, fontSize = 10.sp, color = if (active) Color.White else CosmicMute) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CosmicOrange,
                                        selectedLabelColor = Color.White,
                                        containerColor = CosmicPanel
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(enabled = true, selected = active, borderColor = CosmicBorder, selectedBorderColor = CosmicOrange)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = CosmicPanel,
                        titleContentColor = Color.White
                    ),
                    modifier = Modifier.statusBarsPadding()
                )
            },
            bottomBar = {
                if (!useSidebar) {
                    NavigationBar(
                        containerColor = CosmicPanel,
                        tonalElevation = 8.dp,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        listOf(
                            Triple(SolarDestinations.DASHBOARD, "Dashboard", Icons.Default.Home),
                            Triple(SolarDestinations.INVENTORY, "Inventory", Icons.Default.List),
                            Triple(SolarDestinations.SCHEDULE, "Schedule", Icons.Default.FavoriteBorder),
                            Triple(SolarDestinations.PHASE, "Phases", Icons.Default.Send),
                            Triple(SolarDestinations.SETTINGS, "Settings", Icons.Default.Settings)
                        ).forEach { (viewKey, label, icon) ->
                            val active = currentView == viewKey
                            NavigationBarItem(
                                selected = active,
                                onClick = { 
                                    if (currentView != viewKey) {
                                        navController.navigate(viewKey) {
                                            popUpTo(SolarDestinations.DASHBOARD) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = { Icon(imageVector = icon, contentDescription = label, tint = if (active) CosmicOrange else CosmicMute) },
                                label = { Text(text = label, color = if (active) Color.White else CosmicMute, fontSize = 9.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = CosmicOrange.copy(alpha = 0.2f)
                                )
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Expanded screen desktop sidebar layout
                if (useSidebar && sidebarExpanded) {
                    Column(
                        modifier = Modifier
                            .width(220.dp)
                            .fillMaxHeight()
                            .background(CosmicPanel)
                            .border(BorderStroke(1.dp, CosmicBorder))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "ENGINEERING MODULES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CosmicMute,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )

                        listOf(
                            Triple(SolarDestinations.DASHBOARD, "Dashboard", Icons.Default.Home),
                            Triple(SolarDestinations.INVENTORY, "Inventory", Icons.Default.List),
                            Triple(SolarDestinations.SCHEDULE, "Master Schedule", Icons.Default.ShoppingCart),
                            Triple(SolarDestinations.ANALYSIS, "Sizing Engine", Icons.Default.Call),
                            Triple(SolarDestinations.PHASE, "Phase Optimizer", Icons.Default.Send),
                            Triple(SolarDestinations.VALIDATION, "Validation", Icons.Default.Check),
                            Triple(SolarDestinations.ASSUMPTIONS, "Standard registry", Icons.Default.Lock),
                            Triple(SolarDestinations.REPORTS, "Sizing Report", Icons.Default.Email),
                            Triple(SolarDestinations.LIBRARY, "Appliance Library", Icons.Default.Star),
                            Triple(SolarDestinations.TESTS, "Calculations Test", Icons.Default.PlayArrow),
                            Triple(SolarDestinations.DOCS, "Specifications Info", Icons.Default.Build),
                            Triple(SolarDestinations.ABOUT, "حول التطبيق", Icons.Default.Info),
                            Triple(SolarDestinations.SETTINGS, "Sizing Settings", Icons.Default.Settings)
                        ).forEach { (viewKey, label, icon) ->
                            val active = currentView == viewKey
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { 
                                        if (currentView != viewKey) {
                                            navController.navigate(viewKey) {
                                                popUpTo(SolarDestinations.DASHBOARD) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                color = if (active) CosmicOrange.copy(alpha = 0.15f) else Color.Transparent,
                                border = if (active) BorderStroke(1.dp, CosmicOrange) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (active) CosmicOrange else CosmicMute,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = label,
                                        color = if (active) Color.White else CosmicText,
                                        fontSize = 12.sp,
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                // Primary content router container using NavHost
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    SolarNavHost(
                        navController = navController,
                        loads = loads,
                        sharedViewModel = sharedViewModel
                    )
                }
            }
        }

        // Overlay Interactive Dialog Modals
        activeEditing?.let { editing ->
            EditLoadModal(
                initialLoad = editing,
                onDismiss = { sharedViewModel.activeEditingLoad.value = null },
                onSave = { saved ->
                    sharedViewModel.updateLoad(saved)
                    sharedViewModel.activeEditingLoad.value = null
                    _globalToastChannel.tryEmit(Pair("Specifications saved successfully!", "ok"))
                }
            )
        }

        if (isLibraryOpen) {
            LibraryImportModal(
                onDismiss = { sharedViewModel.isLibraryModalOpen.value = false },
                onSelect = { selectedTempl ->
                    sharedViewModel.isLibraryModalOpen.value = false
                    val index = loads.size + 1
                    val loadEntity = ApplianceLibrary.createLoadFromTemplate(selectedTempl, index)
                    sharedViewModel.activeEditingLoad.value = loadEntity
                }
            )
        }
    }
}
