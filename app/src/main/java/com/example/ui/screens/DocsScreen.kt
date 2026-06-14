package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun DocsScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Engineering Specifications Doc", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Mathematical Formulas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = "Diurnal operational sizing compliant with strict thermal equations:\n\n" +
                            "1. Connected power (W) = W_rated × quantity\n" +
                            "2. Operational Demand (W) = Connected Power × utilization constant Ku\n" +
                            "3. Transients Starting Surge (W) = W_rated × multiplier × quantity\n" +
                            "4. Full Active Current I (A) = Power / (Conductor Voltage × powerFactor) \n" +
                            "5. Three-Phase Vector current calculations integrate radical root multipliers (√3 × V_line × PF)\n" +
                            "6. Normalized Annual Consumption = (SummerEnergy × 183.0 + WinterEnergy × 182.0) × (AggDays / 365.0)",
                    color = CosmicText,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }
}
