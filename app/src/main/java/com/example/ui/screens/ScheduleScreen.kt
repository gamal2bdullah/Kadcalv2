package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LoadEntity
import com.example.domain.Calculations
import com.example.ui.SolarViewModel
import com.example.ui._globalToastChannel
import com.example.ui.theme.*
import java.io.File
import java.io.FileOutputStream

@Composable
fun ScheduleScreen(
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



