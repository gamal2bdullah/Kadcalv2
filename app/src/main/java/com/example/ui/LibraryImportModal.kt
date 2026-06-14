package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.ApplianceLibrary
import com.example.ui.theme.*

@Composable
fun LibraryImportModal(
    onDismiss: () -> Unit,
    onSelect: (ApplianceLibrary.Template) -> Unit
) {
    var search by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val filtered = ApplianceLibrary.APPLIANCE_LIBRARY.filter { t ->
        search.isEmpty() || t.name.contains(search, ignoreCase = true) || t.categorySub.contains(search, ignoreCase = true)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .border(BorderStroke(1.dp, CosmicBorder), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CosmicBg)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CosmicPanel)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Appliance Library Browse", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Select template for instant project populate", color = CosmicMute, fontSize = 11.sp)
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = CosmicText)
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Search appliances templates database...", color = CosmicMute, fontSize = 12.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CosmicMute) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CosmicPanel,
                        unfocusedContainerColor = CosmicPanel,
                        focusedBorderColor = CosmicOrange,
                        unfocusedBorderColor = CosmicBorder
                    )
                )

                // Scroll Catalog list
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filtered.forEach { t ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(t) },
                            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                            border = BorderStroke(1.dp, CosmicBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = t.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = "${t.categoryMain} · ${t.categorySub}", color = CosmicMute, fontSize = 11.sp)
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CosmicPanel2)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "${t.ratedPowerW.toInt()} W", color = CosmicAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
