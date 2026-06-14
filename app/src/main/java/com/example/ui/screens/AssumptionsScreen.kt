package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.example.domain.PolicyRegistry
import com.example.ui.SolarViewModel
import com.example.ui.theme.*

@Composable
fun AssumptionsScreen(
    viewModel: SolarViewModel
) {
    val search by viewModel.searchPolicies.collectAsState()
    val scrollState = rememberScrollState()

    val filtered = PolicyRegistry.POLICY_PACK.filter { p ->
        search.isEmpty() || p.name.contains(search, ignoreCase = true) || p.policyId.contains(search, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Engineering Standards Registry", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)

        OutlinedTextField(
            value = search,
            onValueChange = { viewModel.searchPolicies.value = it },
            placeholder = { Text("Search standards registry (IEEE, NEMA, NEC)…", color = CosmicMute, fontSize = 12.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CosmicMute) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = CosmicPanel,
                unfocusedContainerColor = CosmicPanel,
                focusedBorderColor = CosmicOrange,
                unfocusedBorderColor = CosmicBorder
            )
        )

        Column(
            modifier = Modifier.weight(1f).verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filtered.forEach { policy ->
                PolicyCollapseItem(policy = policy, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PolicyCollapseItem(policy: PolicyRegistry.Policy, viewModel: SolarViewModel) {
    val expandedId by viewModel.expandedPolicyId.collectAsState()
    val isExpanded = expandedId == policy.policyId

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.expandedPolicyId.value = if (isExpanded) null else policy.policyId },
        colors = CardDefaults.cardColors(containerColor = CosmicPanel),
        border = BorderStroke(1.dp, if (isExpanded) CosmicOrange else CosmicBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = policy.policyId, color = CosmicOrange, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text(text = policy.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (policy.confidenceLevel == "High") CosmicGreen.copy(alpha = 0.2f) else CosmicAmber.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = policy.sourceType, color = if (policy.confidenceLevel == "High") CosmicGreenLight else CosmicAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    HorizontalDivider(color = CosmicBorder)
                    Text(text = "Rational standard background:", fontSize = 11.sp, color = CosmicOrange, fontWeight = FontWeight.SemiBold)
                    Text(text = policy.engineeringRationale, fontSize = 11.sp, color = CosmicText, lineHeight = 14.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Traceability Source Reference:", fontSize = 11.sp, color = CosmicOrange, fontWeight = FontWeight.SemiBold)
                    Text(text = policy.sourceReference, fontSize = 11.sp, color = CosmicMute, fontFamily = FontFamily.Monospace)
                    Text(text = "Range guidelines Min: ${policy.minRange} | Max: ${policy.maxRange} ${policy.unit}", fontSize = 10.sp, color = CosmicMute)
                }
            }
        }
    }
}
