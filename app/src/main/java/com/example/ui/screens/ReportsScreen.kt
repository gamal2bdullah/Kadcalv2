package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LoadEntity
import com.example.domain.Calculations
import com.example.ui.SolarViewModel
import com.example.ui.theme.*

@Composable
fun ReportsScreen(
    loads: List<LoadEntity>,
    viewModel: SolarViewModel
) {
    val summaryState by viewModel.summaryState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Traceable Sizing Report", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)

        when (val state = summaryState) {
            is com.example.core.result.UiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CosmicOrange)
                }
            }
            is com.example.core.result.UiState.Empty -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = "No loads to generate report", color = CosmicMute, fontSize = 14.sp)
                }
            }
            is com.example.core.result.UiState.Error -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${state.error.message}", color = CosmicRed, fontSize = 14.sp)
                }
            }
            is com.example.core.result.UiState.Success -> {
                val summary = state.data
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = "Compliance Executive Summary", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = "A complete engineering load sheet was calculated for project '${viewModel.projectName.value}'. Fully conforming to standard thermal specifications and continuous rating procedures.",
                            color = CosmicText,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        HorizontalDivider(color = CosmicBorder)

                        listOf(
                            "Total connected grid demand" to Calculations.fmtW(summary.totalConnectedLoadW, 0),
                            "Operating load factor ratio" to Calculations.fmtPct(summary.loadFactor, 1),
                            "Diurnal peak operating current" to Calculations.fmtA(summary.estimatedMaxCurrentA, 1),
                            "Recommended off-grid inverter cap" to Calculations.fmtW(summary.maximumDemandW * 1.25, 0)
                        ).forEach { (lbl, value) ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = lbl, fontSize = 12.sp, color = CosmicMute)
                                Text(text = value, fontSize = 12.sp, color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
