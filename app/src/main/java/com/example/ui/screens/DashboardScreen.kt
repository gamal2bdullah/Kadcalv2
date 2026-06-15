package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LoadEntity
import com.example.domain.Calculations
import com.example.ui.viewmodel.DashboardViewModel
import com.example.ui.viewmodel.SharedViewModel
import com.example.ui.theme.*
import com.example.ui.SimpleAreaLineChart
import com.example.ui.SimpleDonutChart

@Composable
fun DashboardScreen(
    loads: List<LoadEntity>,
    dashboardViewModel: DashboardViewModel,
    sharedViewModel: SharedViewModel
) {
    val summaryState by dashboardViewModel.summaryState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (val state = summaryState) {
            is com.example.core.result.UiState.Loading -> {
                com.example.ui.ShimmerDashboard()
            }
            is com.example.core.result.UiState.Empty -> {
                com.example.ui.EmptyState(
                    icon = Icons.Default.Info,
                    title = "No Electrical Loads Configured",
                    description = "Add electrical load profiles under your Inventory list to generate professional diurnal solar curves and diagnostic assessments.",
                    actionLabel = "Add Custom Load",
                    onAction = {
                        sharedViewModel.activeEditingLoad.value = com.example.domain.ApplianceLibrary.createLoadFromTemplate(
                            com.example.domain.ApplianceLibrary.APPLIANCE_LIBRARY[0],
                            loads.size + 1
                        )
                    }
                )
            }
            is com.example.core.result.UiState.Error -> {
                com.example.ui.ErrorState(
                    error = state.error.message,
                    onRetry = {
                        // Simply touching project triggers cascade state refresh
                        sharedViewModel.updateProjectName(sharedViewModel.projectName.value)
                    }
                )
            }
            is com.example.core.result.UiState.Success -> {
                val summary = state.data
                // Hero Section
                HeroBanner(sharedViewModel, summary)

                // KPI Summary Matrix
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiMiniCard(
                        label = "Connected Power",
                        value = Calculations.fmtW(summary.totalConnectedLoadW, 1),
                        sub = "Sum of all ratings",
                        modifier = Modifier.weight(1f),
                        indicatorColor = CosmicOrange
                    )
                    KpiMiniCard(
                        label = "Max Active Demand",
                        value = Calculations.fmtW(summary.maximumDemandW, 1),
                        sub = "Coincident Peak",
                        modifier = Modifier.weight(1f),
                        indicatorColor = CosmicAmber
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiMiniCard(
                        label = "Daily Consumption",
                        value = Calculations.fmtWh(summary.totalDailyEnergyWh, 1),
                        sub = "Estimated 24h",
                        modifier = Modifier.weight(1f),
                        indicatorColor = CosmicGreen
                    )
                    KpiMiniCard(
                        label = "Annual Aggregated",
                        value = String.format("%.0f kWh", summary.annualEnergyKWh),
                        sub = "Normalized seasonal",
                        modifier = Modifier.weight(1f),
                        indicatorColor = CosmicBlue
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiMiniCard(
                        label = "Full Starting Current",
                        value = Calculations.fmtA(summary.estimatedMaxCurrentA, 1),
                        sub = "Average per phase",
                        modifier = Modifier.weight(1f),
                        indicatorColor = CosmicPink
                    )
                    KpiMiniCard(
                        label = "System Load Factor",
                        value = Calculations.fmtPct(summary.loadFactor, 1),
                        sub = "Avg/Max Demand ratio",
                        modifier = Modifier.weight(1f),
                        indicatorColor = CosmicCyan
                    )
                }

                // Charts & Graph Plot panels
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "24-Hour Cumulative Load Curve",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Diurnal load sequence in kW · Peak demand: ${String.format("%.2f", summary.maximumDemandW / 1000.0)} kW",
                            color = CosmicMute,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )

                        val labels = List(24) { "$it:00" }
                        val graphData = summary.hourlyProfile.map { it / 1000.0 } // kW
                        SimpleAreaLineChart(
                            data = graphData,
                            labels = labels,
                            unit = " kW"
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Category distribution card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                        border = BorderStroke(1.dp, CosmicBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Energy Allocation",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Text(
                                text = "Aggregated daily energy share",
                                color = CosmicMute,
                                fontSize = 11.sp,
                                modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp)
                            )

                            if (summary.byCategory.isNotEmpty()) {
                                val donutItems = summary.byCategory.map { it.name to it.value }
                                val dColors = summary.byCategory.map { Color(android.graphics.Color.parseColor(it.colorHex)) }
                                SimpleDonutChart(items = donutItems, colors = dColors, modifier = Modifier.size(140.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    summary.byCategory.forEach { c ->
                                        val color = Color(android.graphics.Color.parseColor(c.colorHex))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(color)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(text = c.name, fontSize = 11.sp, color = CosmicText)
                                            }
                                            Text(
                                                text = Calculations.fmtWh(c.value, 0),
                                                fontSize = 11.sp,
                                                color = CosmicMute,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier.height(120.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "No energy registrations available", color = CosmicMute, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Daily energy splits
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Operational Classifications",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        val barItems = listOf(
                            Triple("Daytime Loading (8h-18h)", summary.dayEnergyWh, CosmicAmber),
                            Triple("Night Loading (18h-8h)", summary.nightEnergyWh, CosmicBlue),
                            Triple("Critical & Emergency Loads", summary.criticalLoadWh, CosmicRed),
                            Triple("Deferrable Flexible Loads", summary.deferrableLoadWh, CosmicGreen),
                            Triple("Standby / Phantom Leakage", summary.phantomLossWh, CosmicPurple)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            barItems.forEach { (lbl, valWh, color) ->
                                val ratio = if (summary.totalDailyEnergyWh > 0) (valWh / summary.totalDailyEnergyWh).toFloat() else 0f
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = lbl, fontSize = 12.sp, color = CosmicText)
                                        Text(
                                            text = "${Calculations.fmtWh(valWh, 1)} (${String.format("%.1f", ratio * 100)}%)",
                                            fontSize = 11.sp,
                                            color = CosmicMute,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(CircleShape)
                                            .background(CosmicBorder)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(ratio.coerceAtLeast(0.01f))
                                                .fillMaxHeight()
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Top 5 critical insights
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "System Diagnostics Insights",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        val pfLimitOk = loads.all { it.powerFactor >= 0.8 || it.powerFactor <= 0.0 }
                        val startSurgeRisk = summary.maximumSurgeKW > (summary.peakDemandKW * 5.0)
                        val standbyFraction = if (summary.totalDailyEnergyWh > 0) summary.phantomLossWh / summary.totalDailyEnergyWh else 0.0

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DiagnosticRow(
                                ok = pfLimitOk,
                                title = "Power Factor Compliance",
                                desc = if (pfLimitOk) "Satisfactorily optimized above utility thresholds." else "Caution: sub-optimum inductive loads require power factor correction."
                            )
                            DiagnosticRow(
                                ok = !startSurgeRisk,
                                title = "Starting Surge Capacitance",
                                desc = if (startSurgeRisk) "Inrush danger: starting surge requires staggered sequencer or oversized VFD inverter." else "Balanced starting envelopes; highly compatible with off-grid configurations."
                            )
                            DiagnosticRow(
                                ok = standbyFraction < 0.15,
                                title = "Standby Parasitic Leak",
                                desc = "Standby leaks consume ${String.format("%.1f", standbyFraction * 100)}% of total daily energy budget."
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun HeroBanner(viewModel: SharedViewModel, summary: Calculations.Summary) {
    val level by viewModel.expertLevel.collectAsState()
    val name by viewModel.projectName.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(CosmicPanel2, CosmicPanel)
                )
            )
            .border(BorderStroke(1.dp, CosmicBorder), RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = CosmicOrange,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "KADcal engineering suite · $level Mode",
                    color = CosmicOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = name,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Professional-grade time-of-use analysis matching NEC & IEC compliance structures. Double-tap project indicators to start direct edits.",
                color = CosmicMute,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun KpiMiniCard(
    label: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier,
    indicatorColor: Color = CosmicOrange
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CosmicPanel),
        border = BorderStroke(1.dp, CosmicBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label, color = CosmicMute, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(indicatorColor)
                )
            }
            Text(
                text = value,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )
            Text(text = sub, color = CosmicMute, fontSize = 10.sp)
        }
    }
}

@Composable
fun DiagnosticRow(ok: Boolean, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CosmicPanel2)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = if (ok) Icons.Default.CheckCircle else Icons.Default.Info,
            contentDescription = null,
            tint = if (ok) CosmicGreenLight else CosmicAmber,
            modifier = Modifier.size(16.dp)
        )
        Column {
            Text(text = title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = desc, color = CosmicMute, fontSize = 10.sp, lineHeight = 13.sp)
        }
    }
}
