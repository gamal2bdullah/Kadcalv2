package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SolarViewModel
import com.example.ui._globalToastChannel
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: SolarViewModel
) {
    val name by viewModel.projectName.collectAsState()
    val currentLevel by viewModel.expertLevel.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Project Specifications Options", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Rename Project Sizing", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { viewModel.updateProjectName(it) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CosmicPanel2,
                        unfocusedContainerColor = CosmicPanel2,
                        focusedBorderColor = CosmicOrange,
                        unfocusedBorderColor = CosmicBorder
                    )
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Set Sizing Calculation Difficulty Mode", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "Higher options enable detailed engineering parameters tracking", color = CosmicMute, fontSize = 11.sp, modifier = Modifier.padding(bottom = 6.dp))

                listOf("Basic", "Professional", "Commercial", "Expert").forEach { lvl ->
                    val active = currentLevel == lvl
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (active) CosmicOrange.copy(alpha = 0.15f) else CosmicPanel2)
                            .border(BorderStroke(1.dp, if (active) CosmicOrange else CosmicBorder), RoundedCornerShape(8.dp))
                            .clickable { viewModel.updateExpertLevel(lvl) }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = lvl, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        if (active) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CosmicOrange)
                        }
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Load Sizing Mock Templates", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "Prepopulate standard loads database index quickly", color = CosmicMute, fontSize = 11.sp, modifier = Modifier.padding(bottom = 6.dp))

                listOf("basic" to "Basic residential sizing", "professional" to "Professional smart automation", "commercial" to "Industrial three-phase factories").forEach { (key, label) ->
                    Button(
                        onClick = {
                            viewModel.loadPreset(key)
                            _globalToastChannel.tryEmit(Pair("Loaded standard template preset successfully!", "ok"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicPanel2),
                        border = BorderStroke(1.dp, CosmicBorder),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text(text = label, color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "About KADcal / حول التطبيق", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "Learn more about the solar PV load design suite and operating company profile", color = CosmicMute, fontSize = 11.sp, modifier = Modifier.padding(bottom = 6.dp))

                Button(
                    onClick = {
                        viewModel.navigateTo("about")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicOrange),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color.White)
                        Text(text = "عرض نبذة ومعلومات التطبيق", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
