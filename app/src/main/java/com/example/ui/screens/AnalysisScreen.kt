package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LoadEntity
import com.example.domain.Calculations
import com.example.ui.SolarViewModel
import com.example.ui.theme.*
import com.example.ui.SimpleAreaLineChart
import com.example.ui.HorizontalBarsChart

@Composable
fun AnalysisScreen(
    loads: List<LoadEntity>,
    viewModel: SolarViewModel
) {
    val subView by viewModel.analysisSubView.collectAsState()
    val summaryState by viewModel.summaryState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Deep Sizing Analysis", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "hourly" to "Profiles", "seasonal" to "Seasonal",
                "category" to "Categories", "surges" to "Starting Inrush"
            ).forEach { (key, label) ->
                val active = subView == key
                FilterChip(
                    selected = active,
                    onClick = { viewModel.analysisSubView.value = key },
                    label = { Text(text = label, color = if (active) Color.White else CosmicMute) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CosmicOrange,
                        selectedLabelColor = Color.White,
                        containerColor = CosmicPanel
                    ),
                    border = FilterChipDefaults.filterChipBorder(enabled = true, selected = active, borderColor = CosmicBorder, selectedBorderColor = CosmicOrange)
                )
            }
        }

        when (val state = summaryState) {
            is com.example.core.result.UiState.Loading -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    CircularProgressIndicator(color = CosmicOrange)
                }
            }
            is com.example.core.result.UiState.Empty -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Text(text = "No loads to analyze", color = CosmicMute, fontSize = 14.sp)
                }
            }
            is com.example.core.result.UiState.Error -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Text(text = "Error: ${state.error.message}", color = CosmicRed, fontSize = 14.sp)
                }
            }
            is com.example.core.result.UiState.Success -> {
                val summary = state.data
                when (subView) {
                    "hourly" -> {
                        Card(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                            border = BorderStroke(1.dp, CosmicBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "24-Hour Category Aggregate Feed Profiles", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = "kW overlay values", color = CosmicMute, fontSize = 11.sp, modifier = Modifier.padding(bottom = 12.dp))
                                
                                val labels = List(24) { "$it:00" }
                                SimpleAreaLineChart(
                                    data = summary.hourlyProfile.map { it / 1000.0 },
                                    labels = labels,
                                    unit = " kW"
                                )
                            }
                        }
                    }
                    "seasonal" -> {
                        val winterDaily = loads.sumOf { Calculations.calcDailyEnergy(it, "winter") }
                        Card(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                            border = BorderStroke(1.dp, CosmicBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text(text = "Summer vs Winter Daily Analysis", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                
                                HorizontalBarsChart(
                                    items = listOf(
                                        "Summer Loaded Consumption" to summary.totalDailyEnergyWh / 1000.0,
                                        "Winter Loaded Consumption" to winterDaily / 1000.0
                                    ),
                                    color = CosmicAmber,
                                    formatter = { String.format("%.2f kWh/day", it) }
                                )

                                Text(
                                    text = "Seasonal Energy fluctuations are predominantly driven by climate control regimes. System batteries must buffer the critical worst-case month.",
                                    color = CosmicMute,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                    "category" -> {
                        Card(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                            border = BorderStroke(1.dp, CosmicBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Category Demand Intensity", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalBarsChart(
                                    items = summary.byCategory.map { it.name to it.value / 1000.0 },
                                    color = CosmicCyan,
                                    formatter = { String.format("%.2f kWh", it) }
                                )
                            }
                        }
                    }
                    "surges" -> {
                        Card(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                            border = BorderStroke(1.dp, CosmicBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Starting Inrush Envelopes (Motor Spikes)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = "Required transient capacity for off-grid setup", color = CosmicMute, fontSize = 11.sp, modifier = Modifier.padding(bottom = 12.dp))

                                val sortedSurgeLoads = loads.sortedByDescending { Calculations.calcSurgePower(it) }.take(6)
                                HorizontalBarsChart(
                                    items = sortedSurgeLoads.map { (it.loadName.ifEmpty { it.loadId }) to Calculations.calcSurgePower(it) / 1000.0 },
                                    color = CosmicPurple,
                                    formatter = { String.format("%.2f kW", it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
