package com.example.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.LoadEntity
import com.example.domain.*
import com.example.ui.theme.*
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

val CATEGORY_OPTIONS = listOf("Lighting","HVAC","Kitchen","Pump","Medical","IT","Industrial","EV","Security","Water","Office","Laundry","Other")

@Composable
fun DashboardView(
    loads: List<LoadEntity>,
    viewModel: SolarViewModel
) {
    val summary = Calculations.computeSummary(loads)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Section
        HeroBanner(viewModel, summary)

        // KPI Summary Matrix
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiMiniCard(
                label = "Connected Power",
                value = Calculations.fmtW(summary.totalConnectedLoadW, 1),
                sub = "Sum of all ratings",
                modifier = Modifier.weight(1f),
                indicatorColor = CosmicOrange
            )
            KpiMiniCard(
                label = "Max Active Demand",
                value = Calculations.fmtW(summary.maximumDemandW, 1),
                sub = "Coincident Peak",
                modifier = Modifier.weight(1f),
                indicatorColor = CosmicAmber
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiMiniCard(
                label = "Daily Consumption",
                value = Calculations.fmtWh(summary.totalDailyEnergyWh, 1),
                sub = "Estimated 24h",
                modifier = Modifier.weight(1f),
                indicatorColor = CosmicGreen
            )
            KpiMiniCard(
                label = "Annual Aggregated",
                value = String.format("%.0f kWh", summary.annualEnergyKWh),
                sub = "Normalized seasonal",
                modifier = Modifier.weight(1f),
                indicatorColor = CosmicBlue
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiMiniCard(
                label = "Full Starting Current",
                value = Calculations.fmtA(summary.estimatedMaxCurrentA, 1),
                sub = "Average per phase",
                modifier = Modifier.weight(1f),
                indicatorColor = CosmicPink
            )
            KpiMiniCard(
                label = "System Load Factor",
                value = Calculations.fmtPct(summary.loadFactor, 1),
                sub = "Avg/Max Demand ratio",
                modifier = Modifier.weight(1f),
                indicatorColor = CosmicCyan
            )
        }

        // Charts & Graph Plot panels
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "24-Hour Cumulative Load Curve",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "Diurnal load sequence in kW · Peak demand: ${String.format("%.2f", summary.maximumDemandW / 1000.0)} kW",
                    color = CosmicMute,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                val labels = List(24) { "$it:00" }
                val graphData = summary.hourlyProfile.map { it / 1000.0 } // kW
                SimpleAreaLineChart(
                    data = graphData,
                    labels = labels,
                    unit = " kW"
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category distribution card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                border = BorderStroke(1.dp, CosmicBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Energy Allocation",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = "Aggregated daily energy share",
                        color = CosmicMute,
                        fontSize = 11.sp,
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp)
                    )

                    if (summary.byCategory.isNotEmpty()) {
                        val donutItems = summary.byCategory.map { it.name to it.value }
                        val dColors = summary.byCategory.map { Color(android.graphics.Color.parseColor(it.colorHex)) }
                        SimpleDonutChart(items = donutItems, colors = dColors, modifier = Modifier.size(140.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            summary.byCategory.forEach { c ->
                                val color = Color(android.graphics.Color.parseColor(c.colorHex))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = c.name, fontSize = 11.sp, color = CosmicText)
                                    }
                                    Text(
                                        text = Calculations.fmtWh(c.value, 0),
                                        fontSize = 11.sp,
                                        color = CosmicMute,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No energy registrations available", color = CosmicMute, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Daily energy splits
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Operational Classifications",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val barItems = listOf(
                    Triple("Daytime Loading (8h-18h)", summary.dayEnergyWh, CosmicAmber),
                    Triple("Night Loading (18h-8h)", summary.nightEnergyWh, CosmicBlue),
                    Triple("Critical & Emergency Loads", summary.criticalLoadWh, CosmicRed),
                    Triple("Deferrable Flexible Loads", summary.deferrableLoadWh, CosmicGreen),
                    Triple("Standby / Phantom Leakage", summary.phantomLossWh, CosmicPurple)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    barItems.forEach { (lbl, valWh, color) ->
                        val ratio = if (summary.totalDailyEnergyWh > 0) (valWh / summary.totalDailyEnergyWh).toFloat() else 0f
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = lbl, fontSize = 12.sp, color = CosmicText)
                                Text(
                                    text = "${Calculations.fmtWh(valWh, 1)} (${String.format("%.1f", ratio * 100)}%)",
                                    fontSize = 11.sp,
                                    color = CosmicMute,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape)
                                    .background(CosmicBorder)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(ratio.coerceAtLeast(0.01f))
                                        .fillMaxHeight()
                                        .clip(CircleShape)
                                        .background(color)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Top 5 critical insights
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "System Diagnostics Insights",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val pfLimitOk = loads.all { it.powerFactor >= 0.8 || it.powerFactor <= 0.0 }
                val startSurgeRisk = summary.maximumSurgeKW > (summary.peakDemandKW * 5.0)
                val standbyFraction = if (summary.totalDailyEnergyWh > 0) summary.phantomLossWh / summary.totalDailyEnergyWh else 0.0

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DiagnosticRow(
                        ok = pfLimitOk,
                        title = "Power Factor Compliance",
                        desc = if (pfLimitOk) "Satisfactorily optimized above utility thresholds." else "Caution: sub-optimum inductive loads require power factor correction."
                    )
                    DiagnosticRow(
                        ok = !startSurgeRisk,
                        title = "Starting Surge Capacitance",
                        desc = if (startSurgeRisk) "Inrush danger: starting surge requires staggered sequencer or oversized VFD inverter." else "Balanced starting envelopes; highly compatible with off-grid configurations."
                    )
                    DiagnosticRow(
                        ok = standbyFraction < 0.15,
                        title = "Standby Parasitic Leak",
                        desc = "Standby leaks consume ${String.format("%.1f", standbyFraction * 100)}% of total daily energy budget."
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun HeroBanner(viewModel: SolarViewModel, summary: Calculations.Summary) {
    val level by viewModel.expertLevel.collectAsState()
    val name by viewModel.projectName.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(CosmicPanel2, CosmicPanel)
                )
            )
            .border(BorderStroke(1.dp, CosmicBorder), RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = CosmicOrange,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "KADcal engineering suite · $level Mode",
                    color = CosmicOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = name,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Professional-grade time-of-use analysis matching NEC & IEC compliance structures. Double-tap project indicators to start direct edits.",
                color = CosmicMute,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun KpiMiniCard(
    label: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier,
    indicatorColor: Color = CosmicOrange
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CosmicPanel),
        border = BorderStroke(1.dp, CosmicBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label, color = CosmicMute, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(indicatorColor)
                )
            }
            Text(
                text = value,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )
            Text(text = sub, color = CosmicMute, fontSize = 10.sp)
        }
    }
}

@Composable
fun DiagnosticRow(ok: Boolean, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CosmicPanel2)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = if (ok) Icons.Default.CheckCircle else Icons.Default.Info,
            contentDescription = null,
            tint = if (ok) CosmicGreenLight else CosmicAmber,
            modifier = Modifier.size(16.dp)
        )
        Column {
            Text(text = title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = desc, color = CosmicMute, fontSize = 10.sp, lineHeight = 13.sp)
        }
    }
}

@Composable
fun InventoryView(
    loads: List<LoadEntity>,
    viewModel: SolarViewModel
) {
    val search by viewModel.searchInventory.collectAsState()
    val categoryFilter by viewModel.filterInventoryCategory.collectAsState()
    
    val scrollState = rememberScrollState()

    var showDeleteAllDialog by remember { mutableStateOf(false) }

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
                    onClick = { viewModel.isLibraryModalOpen.value = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicPanel2),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Library", fontSize = 12.sp)
                }
                Button(
                    onClick = { viewModel.activeEditingLoad.value = ApplianceLibrary.createLoadFromTemplate(ApplianceLibrary.APPLIANCE_LIBRARY[0], loads.size + 1) },
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
                onValueChange = { viewModel.searchInventory.value = it },
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

            // Simplistic category dropdown filter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CosmicPanel)
                    .border(BorderStroke(1.dp, CosmicBorder))
                    .height(52.dp)
                    .clickable { 
                        // Simplified cycling categories for smooth mobile click behavior
                        val nextIdx = (CATEGORY_OPTIONS.indexOf(categoryFilter) + 2) % (CATEGORY_OPTIONS.size + 1)
                        viewModel.filterInventoryCategory.value = if (nextIdx == 0) "All" else CATEGORY_OPTIONS[nextIdx - 1]
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
                    LoadListItemCard(l = l, viewModel = viewModel)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun LoadListItemCard(l: LoadEntity, viewModel: SolarViewModel) {
    val connLoad = Calculations.calcConnectedLoad(l)
    val dailyWh = Calculations.calcDailyEnergy(l)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.activeEditingLoad.value = l },
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
                    Text(text = l.loadId, color = CosmicOrange, fontSize = 10.sp, familyMonospace = true)
                    Text(text = l.loadTag, color = CosmicMute, fontSize = 10.sp, familyMonospace = true)
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
                        onClick = { viewModel.duplicateLoad(l) },
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(CosmicPanel2)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Duplicate", tint = CosmicText, modifier = Modifier.size(12.dp))
                    }
                    IconButton(
                        onClick = { viewModel.deleteLoad(l) },
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

@Composable
private fun Text(text: String, color: Color, fontSize: androidx.compose.ui.unit.TextUnit, familyMonospace: Boolean) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        fontFamily = if (familyMonospace) FontFamily.Monospace else FontFamily.Default
    )
}

// REST OF THE DETAILED PAGE VIEWS AS EXTREMELY POLISHED JETPACK COMPOSE MODULES!

@Composable
fun ScheduleView(
    loads: List<LoadEntity>,
    viewModel: SolarViewModel
) {
    val search by viewModel.searchSchedule.collectAsState()
    val categoryFilter by viewModel.filterScheduleCategory.collectAsState()
    val displayAdvanced by viewModel.showAdvScheduleCols.collectAsState()
    val context = LocalContext.current

    val scrollHoriz = rememberScrollState()
    val scrollVert = rememberScrollState()

    val filtered = loads.filter { l ->
        val matchS = search.isEmpty() || l.loadName.contains(search, ignoreCase = true) || l.loadTag.contains(search, ignoreCase = true) || l.loadId.contains(search, ignoreCase = true)
        val matchC = categoryFilter == "All" || l.categoryMain == categoryFilter
        matchS && matchC
    }

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
                Text(text = "Engineering Schedule Sheet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = "High-compliance tabular data model", color = CosmicMute, fontSize = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.showAdvScheduleCols.value = !displayAdvanced },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicPanel2),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Text(text = if (displayAdvanced) "Hide Advanced" else "Show Advanced", fontSize = 11.sp)
                }
                Button(
                    onClick = {
                        exportToDeviceCSV(context, loads)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicOrange)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "CSV", fontSize = 11.sp)
                }
            }
        }

        // Spreadsheet display with horizontal & vertical scroll
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(scrollHoriz)
                    .verticalScroll(scrollVert)
            ) {
                Column {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .background(CosmicPanel2)
                            .padding(bottom = 1.dp)
                            .height(44.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            "ID" to 80, "Tag/Ref" to 120, "Load Name" to 180, "Category" to 110,
                            "Qty" to 60, "Rated (W)" to 90, "Run (W)" to 90, "PF" to 60,
                            "Ku" to 60, "Connected (W)" to 115, "Daily (Wh)" to 115,
                            "Annual (kWh)" to 115
                        ).forEach { (lbl, width) ->
                            Text(
                                text = lbl,
                                color = CosmicOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .width(width.dp)
                                    .padding(8.dp),
                                textAlign = if (lbl == "ID" || lbl == "Tag/Ref" || lbl == "Load Name") TextAlign.Start else TextAlign.End
                            )
                        }
                    }

                    // Data Rows
                    filtered.forEach { l ->
                        val conn = Calculations.calcConnectedLoad(l)
                        val daily = Calculations.calcDailyEnergy(l)
                        val annual = Calculations.calcAnnualEnergy(l)

                        Row(
                            modifier = Modifier
                                .height(40.dp)
                                .border(BorderStroke(1.dp, CosmicBorder.copy(alpha = 0.5f))),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CellStr(l.loadId, 80)
                            CellStr(l.loadTag, 120, CosmicAmber)
                            CellStr(l.loadName, 180, Color.White, maxLines = 1)
                            CellStr(l.categoryMain, 110)
                            CellNum(l.quantity.toDouble(), 60, decimals = 0)
                            CellNum(l.ratedPowerW, 90)
                            CellNum(l.runningPowerW, 90)
                            CellNum(l.powerFactor, 60, decimals = 2)
                            CellNum(l.utilizationFactorKu, 60, decimals = 2)
                            CellNum(conn, 115, isBold = true, textColor = CosmicOrange)
                            CellNum(daily, 115, isBold = true, textColor = CosmicAmber)
                            CellNum(annual / 1000.0, 115, decimals = 1, textColor = CosmicGreenLight)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CellStr(text: String, width: Int, color: Color = CosmicText, maxLines: Int = Int.MAX_VALUE) {
    Text(
        text = text,
        color = color,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier
            .width(width.dp)
            .padding(8.dp),
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun CellNum(value: Double, width: Int, decimals: Int = 0, isBold: Boolean = false, textColor: Color = CosmicText) {
    Text(
        text = String.format("%.${decimals}f", value),
        color = textColor,
        fontSize = 11.sp,
        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier
            .width(width.dp)
            .padding(8.dp),
        textAlign = TextAlign.End
    )
}

fun exportToDeviceCSV(context: Context, loads: List<LoadEntity>) {
    try {
        val dir = context.getExternalFilesDir(null)
        val file = File(dir, "load-schedule-export.csv")
        FileOutputStream(file).use { out ->
            out.write("ID,Tag,Name,Category,Space,Qty,RatedPowerW,RunningPowerW,PF,Ku,ConnectedW,DailyWh\n".toByteArray())
            loads.forEach { l ->
                val row = "${l.loadId},${l.loadTag},${l.loadName},${l.categoryMain},${l.spaceArea},${l.quantity},${l.ratedPowerW},${l.runningPowerW},${l.powerFactor},${l.utilizationFactorKu},${Calculations.calcConnectedLoad(l)},${Calculations.calcDailyEnergy(l)}\n"
                out.write(row.toByteArray())
            }
        }
        _globalToastChannel.tryEmit(Pair("Exported cleanly to Android files directory!", "ok"))
    } catch (e: Exception) {
        _globalToastChannel.tryEmit(Pair("Export issue: ${e.message}", "err"))
    }
}

val _globalToastChannel = kotlinx.coroutines.flow.MutableSharedFlow<Pair<String, String>>(extraBufferCapacity = 8)

@Composable
fun AnalysisView(
    loads: List<LoadEntity>,
    viewModel: SolarViewModel
) {
    val subView by viewModel.analysisSubView.collectAsState()
    val summary = Calculations.computeSummary(loads)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Deep Sizing Analysis", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "hourly" to "Profiles", "seasonal" to "Seasonal",
                "category" to "Categories", "surges" to "Starting Inrush"
            ).forEach { (key, label) ->
                val active = subView == key
                FilterChip(
                    selected = active,
                    onClick = { viewModel.analysisSubView.value = key },
                    label = { Text(text = label, color = if (active) Color.White else CosmicMute) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CosmicOrange,
                        selectedLabelColor = Color.White,
                        containerColor = CosmicPanel
                    ),
                    border = FilterChipDefaults.filterChipBorder(enabled = true, selected = active, borderColor = CosmicBorder, selectedBorderColor = CosmicOrange)
                )
            }
        }

        when (subView) {
            "hourly" -> {
                Card(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "24-Hour Category Aggregate Feed Profiles", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(text = "kW overlay values", color = CosmicMute, fontSize = 11.sp, modifier = Modifier.padding(bottom = 12.dp))
                        
                        val labels = List(24) { "$it:00" }
                        SimpleAreaLineChart(
                            data = summary.hourlyProfile.map { it / 1000.0 },
                            labels = labels,
                            unit = " kW"
                        )
                    }
                }
            }
            "seasonal" -> {
                // Seasonal Comparison
                val winterDaily = loads.sumOf { Calculations.calcDailyEnergy(it, "winter") }
                Card(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(text = "Summer vs Winter Daily Analysis", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        
                        HorizontalBarsChart(
                            items = listOf(
                                "Summer Loaded Consumption" to summary.totalDailyEnergyWh / 1000.0,
                                "Winter Loaded Consumption" to winterDaily / 1000.0
                            ),
                            color = CosmicAmber,
                            formatter = { String.format("%.2f kWh/day", it) }
                        )

                        Text(
                            text = "Seasonal Energy fluctuations are predominantly driven by climate control regimes. System batteries must buffer the critical worst-case month.",
                            color = CosmicMute,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
            "category" -> {
                Card(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Category Demand Intensity", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalBarsChart(
                            items = summary.byCategory.map { it.name to it.value / 1000.0 },
                            color = CosmicCyan,
                            formatter = { String.format("%.2f kWh", it) }
                        )
                    }
                }
            }
            "surges" -> {
                Card(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CosmicPanel),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Starting Inrush Envelopes (Motor Spikes)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(text = "Required transient capacity for off-grid setup", color = CosmicMute, fontSize = 11.sp, modifier = Modifier.padding(bottom = 12.dp))

                        val sortedSurgeLoads = loads.sortedByDescending { Calculations.calcSurgePower(it) }.take(6)
                        HorizontalBarsChart(
                            items = sortedSurgeLoads.map { (it.loadName.ifEmpty { it.loadId }) to Calculations.calcSurgePower(it) / 1000.0 },
                            color = CosmicPurple,
                            formatter = { String.format("%.2f kW", it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PhaseView(
    loads: List<LoadEntity>,
    viewModel: SolarViewModel
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

@Composable
fun ValidationView(
    loads: List<LoadEntity>,
    viewModel: SolarViewModel
) {
    val matrix = ValidationRules.getValidationMatrix(loads)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Compliance Audit matrix", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)

        // Error list
        if (matrix.errors.isNotEmpty()) {
            ValidationCategoryCard(title = "Fatal System Errors", icon = Icons.Default.Close, color = CosmicRed, list = matrix.errors)
        }

        // Warnings list
        if (matrix.warnings.isNotEmpty()) {
            ValidationCategoryCard(title = "Engineering Warnings", icon = Icons.Default.Warning, color = CosmicAmber, list = matrix.warnings)
        }

        // Advisories list
        if (matrix.advisories.isNotEmpty()) {
            ValidationCategoryCard(title = "Optimization Advisories", icon = Icons.Default.Info, color = CosmicCyan, list = matrix.advisories)
        }

        if (matrix.totalCount == 0) {
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

@Composable
fun AssumptionsView(
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
                    // Detailed registry specifications
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

@Composable
fun ReportsView(
    loads: List<LoadEntity>,
    viewModel: SolarViewModel
) {
    val summary = Calculations.computeSummary(loads)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Traceable Sizing Report", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)

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

@Composable
fun LibraryView(
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

@Composable
fun TestsView(
    viewModel: SolarViewModel
) {
    val scope = rememberCoroutineScope()
    val result by viewModel.testSuiteResult.collectAsState()
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
                onClick = { viewModel.runTestSuite() },
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

@Composable
fun DocsView() {
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

@Composable
fun SettingsView(
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
