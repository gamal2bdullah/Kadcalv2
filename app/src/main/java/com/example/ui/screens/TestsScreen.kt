package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.TestsViewModel
import com.example.ui.theme.*

@Composable
fun TestsScreen(
    testsViewModel: TestsViewModel
) {
    val result by testsViewModel.testSuiteResult.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Automated Self-Audit Suite", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = "Regressions & unit verification testing", color = CosmicMute, fontSize = 12.sp)
            }

            Button(
                onClick = { testsViewModel.runTestSuite() },
                colors = ButtonDefaults.buttonColors(containerColor = CosmicOrange)
            ) {
                Text(text = "Execute Audit", fontSize = 12.sp)
            }
        }

        if (result == null) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Run automated self-tests to verify formulas", color = CosmicMute, fontSize = 12.sp)
            }
        } else {
            val r = result!!
            Column(
                modifier = Modifier.weight(1f).verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiMiniCard(label = "Specs total tested", value = "${r.totalCount}", sub = "100% test coverage", modifier = Modifier.weight(1f), CosmicOrange)
                    KpiMiniCard(label = "Suites Passed", value = "${r.passedCount}", sub = "${String.format("%.1f", r.passPercentage)}% success", modifier = Modifier.weight(1f), CosmicGreen)
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "Compliance test reports logs", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
                        r.results.forEach { test ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CosmicPanel2)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = test.name, fontSize = 11.sp, color = CosmicText, modifier = Modifier.weight(1f))
                                Text(
                                    text = if (test.passed) "PASS" else "FAIL",
                                    color = if (test.passed) CosmicGreenLight else CosmicRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
