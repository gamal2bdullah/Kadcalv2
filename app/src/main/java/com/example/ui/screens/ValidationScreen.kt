package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.LoadEntity
import com.example.domain.ValidationRules
import com.example.ui.SolarViewModel
import com.example.ui.theme.*

@Composable
fun ValidationScreen(
    loads: List<LoadEntity>,
    viewModel: SolarViewModel
) {
    val matrixState by viewModel.validationMatrixState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Compliance Audit Matrix", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)

        // Error list
        if (matrixState.errors.isNotEmpty()) {
            ValidationCategoryCard(title = "Fatal System Errors", icon = Icons.Default.Close, color = CosmicRed, list = matrixState.errors)
        }

        // Warnings list
        if (matrixState.warnings.isNotEmpty()) {
            ValidationCategoryCard(title = "Engineering Warnings", icon = Icons.Default.Warning, color = CosmicAmber, list = matrixState.warnings)
        }

        // Advisories list
        if (matrixState.advisories.isNotEmpty()) {
            ValidationCategoryCard(title = "Optimization Advisories", icon = Icons.Default.Info, color = CosmicCyan, list = matrixState.advisories)
        }

        if (matrixState.totalCount == 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = CosmicGreenLight, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Verification: Zero compliance violations reported!", color = CosmicGreenLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ValidationCategoryCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    list: List<ValidationRules.RuleIssue>
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CosmicPanel),
        border = BorderStroke(1.dp, CosmicBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                Text(text = "$title (${list.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                list.forEach { issue ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(CosmicPanel2)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = issue.ruleId, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Column {
                            Text(text = "${issue.loadName}: ${issue.message}", color = CosmicText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            if (issue.fixSuggestion.isNotEmpty()) {
                                Text(text = "➜ Fix suggestion: ${issue.fixSuggestion}", color = CosmicGreenLight, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
