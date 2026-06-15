package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LoadEntity
import com.example.domain.Calculations
import com.example.domain.PhaseBalancer
import com.example.ui.viewmodel.PhaseViewModel
import com.example.ui.theme.*

@Composable
fun PhaseScreen(
    loads: List<LoadEntity>,
    phaseViewModel: PhaseViewModel
) {
    val result = PhaseBalancer.balancePhases(loads)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Three-Phase Balance Engine", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = "Heuristics greedy vector optimizer", color = CosmicMute, fontSize = 12.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (result.balancingScore >= 80) CosmicGreen.copy(alpha = 0.2f) else CosmicAmber.copy(alpha = 0.2f))
                    .padding(8.dp)
            ) {
                Text(text = "Score: ${result.balancingScore}/100", color = if (result.balancingScore >= 80) CosmicGreenLight else CosmicAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        // Horizontal Phase Loadings visual list
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Phase Conductor Allocation Loads", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))

                val phaseBars = listOf(
                    Triple("L1 Conductor Load", result.phases.L1, CosmicOrange),
                    Triple("L2 Conductor Load", result.phases.L2, CosmicAmber),
                    Triple("L3 Conductor Load", result.phases.L3, CosmicPink)
                )

                val maxL = maxOf(0.1, result.phases.L1, result.phases.L2, result.phases.L3)
                phaseBars.forEach { (lbl, valW, color) ->
                    val ratio = (valW / maxL).toFloat()
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = lbl, fontSize = 11.sp, color = CosmicText)
                            Text(text = Calculations.fmtW(valW, 1), fontSize = 11.sp, color = CosmicMute, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(CosmicBorder)) {
                            Box(modifier = Modifier.fillMaxWidth(ratio).fillMaxHeight().clip(CircleShape).background(color))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Calculated Imbalance Ratio", fontSize = 12.sp, color = CosmicText)
                    Text(
                        text = String.format("%.2f%%", result.imbalancePercent),
                        fontSize = 12.sp,
                        color = if (result.imbalancePercent <= 10.0) CosmicGreenLight else Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Recommendations
        if (result.recommendations.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                border = BorderStroke(1.dp, CosmicBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Vector Recommendations", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    result.recommendations.forEach { r ->
                        Text(text = "▸ $r", color = CosmicMute, fontSize = 11.sp, lineHeight = 15.sp)
                    }
                }
            }
        }
    }
}
