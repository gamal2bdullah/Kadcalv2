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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LoadEntity
import com.example.domain.ApplianceLibrary
import com.example.domain.Calculations
import com.example.ui.viewmodel.InventoryViewModel
import com.example.ui.viewmodel.InventoryEvent
import com.example.ui.viewmodel.SharedViewModel
import com.example.ui.theme.*

val CATEGORY_OPTIONS = listOf("Lighting","HVAC","Kitchen","Pump","Medical","IT","Industrial","EV","Security","Water","Office","Laundry","Other")

@Composable
fun InventoryScreen(
    loads: List<LoadEntity>,
    inventoryViewModel: InventoryViewModel,
    sharedViewModel: SharedViewModel
) {
    val uiState by inventoryViewModel.uiState.collectAsState()
    val search = uiState.searchQuery
    val categoryFilter = uiState.categoryFilter
    
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
                Text(text = "Electrical Load Inventory", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = "Total ${loads.size} load segments registered", color = CosmicMute, fontSize = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { sharedViewModel.isLibraryModalOpen.value = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicPanel2),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Library", fontSize = 12.sp)
                }
                Button(
                    onClick = { sharedViewModel.activeEditingLoad.value = ApplianceLibrary.createLoadFromTemplate(ApplianceLibrary.APPLIANCE_LIBRARY[0], loads.size + 1) },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicOrange)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add Load", fontSize = 12.sp)
                }
            }
        }

        // Filters UI
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = { inventoryViewModel.onEvent(InventoryEvent.SetSearchQuery(it)) },
                placeholder = { Text("Search by name, tag, or ID…", color = CosmicMute, fontSize = 12.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CosmicMute) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CosmicPanel,
                    unfocusedContainerColor = CosmicPanel,
                    focusedBorderColor = CosmicOrange,
                    unfocusedBorderColor = CosmicBorder
                ),
                shape = RoundedCornerShape(8.dp)
            )

            // Category filter dropdown box
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CosmicPanel)
                    .border(BorderStroke(1.dp, CosmicBorder))
                    .height(52.dp)
                    .clickable { 
                        val nextIdx = (CATEGORY_OPTIONS.indexOf(categoryFilter) + 2) % (CATEGORY_OPTIONS.size + 1)
                        val nextCategory = if (nextIdx == 0) "All" else CATEGORY_OPTIONS[nextIdx - 1]
                        inventoryViewModel.onEvent(InventoryEvent.SetCategoryFilter(nextCategory))
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = null, tint = CosmicOrange, modifier = Modifier.size(16.dp))
                    Text(text = categoryFilter, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // List container
        val filteredLoads = loads.filter { l ->
            val matchS = search.isEmpty() || l.loadName.contains(search, ignoreCase = true) || l.loadTag.contains(search, ignoreCase = true) || l.loadId.contains(search, ignoreCase = true)
            val matchC = categoryFilter == "All" || l.categoryMain == categoryFilter
            matchS && matchC
        }

        if (filteredLoads.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(BorderStroke(1.dp, CosmicBorder))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CosmicBorder, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "No matching loads found", color = CosmicMute, fontSize = 14.sp)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredLoads.forEach { l ->
                    LoadListItemCard(l = l, sharedViewModel = sharedViewModel)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun LoadListItemCard(l: LoadEntity, sharedViewModel: SharedViewModel) {
    val connLoad = Calculations.calcConnectedLoad(l)
    val dailyWh = Calculations.calcDailyEnergy(l)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { sharedViewModel.activeEditingLoad.value = l },
        colors = CardDefaults.cardColors(containerColor = CosmicPanel),
        border = BorderStroke(1.dp, CosmicBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = l.loadId, color = CosmicOrange, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text(text = l.loadTag, color = CosmicMute, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CosmicPanel2)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(text = l.categoryMain, color = CosmicAmber, fontSize = 9.sp)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = l.loadName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (l.arabicName.isNotEmpty()) {
                    Text(text = l.arabicName, color = CosmicMute, fontSize = 11.sp, textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth())
                }
            }

            Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = Calculations.fmtW(connLoad, 0),
                    color = CosmicText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = Calculations.fmtWh(dailyWh, 0) + "/d",
                    color = CosmicAmber,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { sharedViewModel.duplicateLoad(l) },
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(CosmicPanel2)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Duplicate", tint = CosmicText, modifier = Modifier.size(12.dp))
                    }
                    IconButton(
                        onClick = { sharedViewModel.deleteLoad(l) },
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(CosmicBorder)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
    }
}
