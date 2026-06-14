package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LoadEntity
import com.example.domain.ApplianceLibrary
import com.example.ui.SolarViewModel
import com.example.ui._globalToastChannel
import com.example.ui.theme.*

@Composable
fun LibraryScreen(
    loads: List<LoadEntity>,
    viewModel: SolarViewModel
) {
    val search by viewModel.searchLibrary.collectAsState()
    val scrollState = rememberScrollState()
    
    val filtered = ApplianceLibrary.APPLIANCE_LIBRARY.filter { t ->
        search.isEmpty() || t.name.contains(search, ignoreCase = true) || t.categorySub.contains(search, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Appliance Template Database", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)

        OutlinedTextField(
            value = search,
            onValueChange = { viewModel.searchLibrary.value = it },
            placeholder = { Text("Search from 50+ typical appliances templates…", color = CosmicMute, fontSize = 12.sp) },
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
            filtered.forEach { t ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = t.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "${t.categoryMain} · Nominal standard power: ${t.ratedPowerW.toInt()} W", color = CosmicMute, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                val index = loads.size + 1
                                val entity = ApplianceLibrary.createLoadFromTemplate(t, index)
                                viewModel.addLoad(entity)
                                _globalToastChannel.tryEmit(Pair("Imported standard '${t.name}' successfully!", "ok"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicOrange),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(text = "Import", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
