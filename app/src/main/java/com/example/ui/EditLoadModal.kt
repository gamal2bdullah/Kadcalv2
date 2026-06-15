package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.LoadEntity
import com.example.domain.Calculations
import com.example.domain.ValidationRules
import com.example.ui.theme.*

@Composable
fun EditLoadModal(
    initialLoad: LoadEntity,
    onDismiss: () -> Unit,
    onSave: (LoadEntity) -> Unit
) {
    var loadState by remember { mutableStateOf(initialLoad) }
    val scrollState = rememberScrollState()

    val validatorIssues = ValidationRules.validateLoad(loadState)
    val criticalIssues = validatorIssues.filter { it.severity == "error" }
    val warningIssues = validatorIssues.filter { it.severity == "warning" }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(16.dp))
                .border(BorderStroke(1.dp, CosmicBorder), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CosmicBg)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CosmicPanel)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (initialLoad.loadName.isEmpty()) "Add Sizing Load" else "Modify Load: ${initialLoad.loadId}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(text = "Engineering Specifications Sizing", color = CosmicMute, fontSize = 11.sp)
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = CosmicText)
                    }
                }

                // Scrolling specification forms
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // SECTION A: Identity
                    FormSection(title = "Section A · Identity Specifications") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                FormInput(
                                    label = "Load ID", value = loadState.loadId,
                                    onValueChange = { loadState = loadState.copy(loadId = it) }, modifier = Modifier.weight(1f)
                                )
                                FormInput(
                                    label = "Sizing Tag Ref", value = loadState.loadTag,
                                    onValueChange = { loadState = loadState.copy(loadTag = it) }, modifier = Modifier.weight(1f)
                                )
                            }
                            FormInput(
                                label = "Appliance Name (English)", value = loadState.loadName,
                                onValueChange = { loadState = loadState.copy(loadName = it) }
                            )
                            FormInput(
                                label = "Appliance Name (Arabic)", value = loadState.arabicName,
                                onValueChange = { loadState = loadState.copy(arabicName = it) }, isArabic = true
                            )
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                FormDropdown(
                                    label = "Main Category", value = loadState.categoryMain,
                                    options = listOf("Lighting","HVAC","Kitchen","Pump","Medical","IT","Industrial","EV","Security","Water","Office","Laundry","Other"),
                                    onSelectionChange = { loadState = loadState.copy(categoryMain = it) }, modifier = Modifier.weight(1f)
                                )
                                FormDropdown(
                                    label = "Operational Area", value = loadState.spaceArea,
                                    options = listOf("Living Room","Bedroom","Master Bedroom","Kitchen","Bathroom","Office","Garage","Hallway","Outdoor","Roof","Garden","Lobby","Other"),
                                    onSelectionChange = { loadState = loadState.copy(spaceArea = it) }, modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // SECTION B: Electrical specs
                    FormSection(title = "Section B · Electrical Specifications") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                FormDropdown(
                                    label = "Load Phase", value = loadState.phaseType,
                                    options = listOf("1Ø", "3Ø"),
                                    onSelectionChange = { loadState = loadState.copy(phaseType = it) }, modifier = Modifier.weight(1f)
                                )
                                FormDropdown(
                                    label = "System Current Type", value = loadState.electricalType,
                                    options = listOf("AC", "DC"),
                                    onSelectionChange = { loadState = loadState.copy(electricalType = it) }, modifier = Modifier.weight(1f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                FormInputNum(
                                    label = "Rated Power (W)", value = loadState.ratedPowerW,
                                    onValueChange = { loadState = loadState.copy(ratedPowerW = it) }, modifier = Modifier.weight(1.5f)
                                )
                                FormInputNum(
                                    label = "Power Factor (0-1)", value = loadState.powerFactor,
                                    onValueChange = { loadState = loadState.copy(powerFactor = it) }, modifier = Modifier.weight(1f)
                                )
                                FormInputNum(
                                    label = "Quantity", value = loadState.quantity.toDouble(),
                                    onValueChange = { loadState = loadState.copy(quantity = it.toInt()) }, modifier = Modifier.weight(1f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                FormInputNum(
                                    label = "Nominal Voltage", value = loadState.voltageNominal.toDouble(),
                                    onValueChange = { loadState = loadState.copy(voltageNominal = it.toInt()) }, modifier = Modifier.weight(1f)
                                )
                                FormInputNum(
                                    label = "Surge Multiplier", value = loadState.surgeMultiplier,
                                    onValueChange = { loadState = loadState.copy(surgeMultiplier = it) }, modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // SECTION C: Work cycle
                    FormSection(title = "Section C · Operational Cycle (24h)") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                FormInputNum(
                                    label = "Summer Day Hours (h)", value = loadState.dayHoursSummer,
                                    onValueChange = { loadState = loadState.copy(dayHoursSummer = it) }, modifier = Modifier.weight(1f)
                                )
                                FormInputNum(
                                    label = "Summer Night Hours (h)", value = loadState.nightHoursSummer,
                                    onValueChange = { loadState = loadState.copy(nightHoursSummer = it) }, modifier = Modifier.weight(1f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                FormInputNum(
                                    label = "Utilization Ku (0-1)", value = loadState.utilizationFactorKu,
                                    onValueChange = { loadState = loadState.copy(utilizationFactorKu = it) }, modifier = Modifier.weight(1f)
                                )
                                FormInputNum(
                                    label = "Duty Cycle (%)", value = loadState.dutyCyclePercent,
                                    onValueChange = { loadState = loadState.copy(dutyCyclePercent = it) }, modifier = Modifier.weight(1f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                FormDropdown(
                                    label = "Criticality Group", value = loadState.criticality,
                                    options = listOf("Critical", "Essential", "Normal", "Optional"),
                                    onSelectionChange = { loadState = loadState.copy(criticality = it) }, modifier = Modifier.weight(1f)
                                )
                                FormDropdown(
                                    label = "Operational Profiles", value = loadState.timeProfileType,
                                    options = listOf("Base Load", "Morning Peak", "Noon Peak", "Evening Peak", "Night Load", "Day Load", "24/7"),
                                    onSelectionChange = { loadState = loadState.copy(timeProfileType = it) }, modifier = Modifier.weight(1f)
                                )
                            }

                            // Horizontal switch checkboxes
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                FormCheckRow(
                                    label = "Continuous load", checked = loadState.continuousLoad,
                                    onCheckedChange = { loadState = loadState.copy(continuousLoad = it) }, modifier = Modifier.weight(1f)
                                )
                                FormCheckRow(
                                    label = "Shiftable daytime", checked = loadState.shiftableToDaytime,
                                    onCheckedChange = { loadState = loadState.copy(shiftableToDaytime = it) }, modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Precalculating real-time preview panel
                    FormSection(title = "Real-time Calculated Sizing") {
                        val cLoad = Calculations.calcConnectedLoad(loadState)
                        val energy = Calculations.calcDailyEnergy(loadState)
                        val flc = Calculations.calcFullLoadCurrent(loadState)
                        val surge = Calculations.calcSurgePower(loadState)

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            CalculatedSizingRow("Connected Sizing Total", Calculations.fmtW(cLoad, 0))
                            CalculatedSizingRow("Transients Starts Surge", Calculations.fmtW(surge, 0))
                            CalculatedSizingRow("Full Load Active Current", Calculations.fmtA(flc, 2))
                            CalculatedSizingRow("Aggregated daily Energy", Calculations.fmtWh(energy, 0))
                        }
                    }

                    // Error log block
                    if (validatorIssues.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CosmicPanel)
                                .border(BorderStroke(1.dp, CosmicBorder), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Compliance Audit checks:", color = CosmicOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            validatorIssues.take(3).forEach { issue ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(if (issue.severity == "error") CosmicRed else CosmicAmber)
                                    )
                                    Text(text = issue.message, color = CosmicText, fontSize = 11.sp, lineHeight = 14.sp)
                                }
                            }
                        }
                    }
                }

                // Footer Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CosmicPanel)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CosmicText),
                        border = BorderStroke(1.dp, CosmicBorder)
                    ) {
                        Text(text = "Cancel", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            if (criticalIssues.isNotEmpty()) {
                                _globalToastChannel.tryEmit(Pair("Please fix fatal errors first!", "err"))
                            } else {
                                onSave(loadState)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicOrange)
                    ) {
                        Text(text = "Save Design Specs", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FormSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(text = title, color = CosmicOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Box(modifier = Modifier.padding(12.dp)) {
                content()
            }
        }
    }
}

@Composable
fun FormInput(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, isArabic: Boolean = false) {
    Column(modifier = modifier) {
        Text(text = label, color = CosmicMute, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 12.sp,
                textAlign = if (isArabic) TextAlign.End else TextAlign.Start,
                color = Color.White
            ),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CosmicPanel2,
                unfocusedContainerColor = CosmicPanel2,
                focusedBorderColor = CosmicOrange,
                unfocusedBorderColor = CosmicBorder
            )
        )
    }
}

@Composable
fun FormInputNum(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    onNext: (() -> Unit)? = null,
    onPrevious: (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    var textValue by remember { mutableStateOf(if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()) }
    var hasError by remember { mutableStateOf(false) }

    LaunchedEffect(value) {
        if (textValue.toDoubleOrNull() != value) {
            textValue = if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
        }
    }

    Column(modifier = modifier) {
        Text(text = label, color = CosmicMute, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = textValue,
            onValueChange = { newText ->
                textValue = newText
                newText.toDoubleOrNull()?.let { d ->
                    hasError = false
                    onValueChange(d)
                } ?: run { hasError = true }
            },
            isError = hasError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    if (onNext != null) onNext() else focusManager.moveFocus(FocusDirection.Down)
                },
                onPrevious = {
                    if (onPrevious != null) onPrevious() else focusManager.moveFocus(FocusDirection.Up)
                },
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) {
                        when (event.key) {
                            Key.Tab -> {
                                if (event.isShiftPressed) {
                                    if (onPrevious != null) onPrevious() else focusManager.moveFocus(FocusDirection.Up)
                                } else {
                                    if (onNext != null) onNext() else focusManager.moveFocus(FocusDirection.Down)
                                }
                                true
                            }
                            else -> false
                        }
                    } else {
                        false
                    }
                },
            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = Color.White, fontFamily = FontFamily.Monospace),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CosmicPanel2,
                unfocusedContainerColor = CosmicPanel2,
                focusedBorderColor = CosmicOrange,
                unfocusedBorderColor = CosmicBorder
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDropdown(label: String, value: String, options: List<String>, onSelectionChange: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(text = label, color = CosmicMute, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(CosmicPanel2)
                .border(BorderStroke(1.dp, CosmicBorder), RoundedCornerShape(6.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = value, color = Color.White, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = CosmicMute,
                    modifier = Modifier.size(18.dp)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(CosmicPanel)
            ) {
                options.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(text = opt, color = Color.White, fontSize = 11.sp) },
                        onClick = {
                            onSelectionChange(opt)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FormCheckRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(CosmicPanel2)
            .border(BorderStroke(1.dp, CosmicBorder), RoundedCornerShape(6.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = CosmicOrange, uncheckedColor = CosmicBorder)
        )
        Text(text = label, color = Color.White, fontSize = 11.sp)
    }
}

@Composable
fun CalculatedSizingRow(label: String, valStr: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = CosmicMute, fontSize = 11.sp)
        Text(text = valStr, color = CosmicAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}
