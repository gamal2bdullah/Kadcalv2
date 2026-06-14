package com.example.domain

import com.example.data.LoadEntity

object ValidationRules {

    data class RuleIssue(
        val ruleId: String,
        val name: String,
        val field: String,
        val message: String,
        val severity: String, // error, warning, advisory, info, assumption
        val source: String,
        val fixSuggestion: String,
        val relatedFormula: String = "",
        val loadId: String,
        val loadName: String
    )

    data class Rule(
        val id: String,
        val name: String,
        val severity: String,
        val source: String,
        val fixSuggestion: String,
        val formula: String = "",
        val check: (LoadEntity) -> Boolean,
        val field: String,
        val makeMessage: (LoadEntity) -> String
    )

    val RULES = listOf(
        // ERRORS
        Rule(
            id = "E001", name = "Required: Load Name", severity = "error", source = "Validation Engine v1.0",
            fixSuggestion = "Provide a descriptive name (e.g., 'AC Living Room 1')", field = "loadName",
            check = { it.loadName.trim().isEmpty() }, makeMessage = { "Load name is required" }
        ),
        Rule(
            id = "E002", name = "Required: Rated Power", severity = "error", source = "Calculation Engine",
            fixSuggestion = "Enter the nameplate rated power from the appliance spec", formula = "connected = ratedPowerW × quantity",
            field = "ratedPowerW", check = { it.ratedPowerW <= 0.0 }, makeMessage = { "Rated power must be > 0 (got ${it.ratedPowerW} W)" }
        ),
        Rule(
            id = "E003", name = "Power Factor Range", severity = "error", source = "IEEE 141",
            fixSuggestion = "Use 0.85 for motors, 0.95 for LED, 0.65 for SMPS", formula = "kVA = P/(PF×1000)",
            field = "powerFactor", check = { it.powerFactor <= 0.0 || it.powerFactor > 1.0 }, makeMessage = { "PF ${it.powerFactor} outside (0,1]" }
        ),
        Rule(
            id = "E004", name = "Efficiency Range", severity = "error", source = "Physics",
            fixSuggestion = "Use 85-95% for modern equipment", field = "efficiency",
            check = { it.efficiency < 0.0 || it.efficiency > 100.0 }, makeMessage = { "Efficiency ${it.efficiency}% outside 0-100" }
        ),
        Rule(
            id = "E005", name = "Quantity Positive", severity = "error", source = "Validation Engine",
            fixSuggestion = "Use 1 or greater", field = "quantity",
            check = { it.quantity < 1 }, makeMessage = { "Quantity ${it.quantity} must be ≥ 1" }
        ),
        Rule(
            id = "E006", name = "Voltage Required", severity = "error", source = "Validation Engine",
            fixSuggestion = "Use 220V (1Ø) or 380V (3Ø)", field = "voltageNominal",
            check = { it.voltageNominal <= 0 }, makeMessage = { "Voltage must be > 0" }
        ),
        Rule(
            id = "E007", name = "THD Out of Range", severity = "error", source = "Validation Engine",
            fixSuggestion = "5-15% typical for electronic loads", field = "thdPercent",
            check = { it.thdPercent < 0.0 || it.thdPercent > 100.0 }, makeMessage = { "THD ${it.thdPercent}% outside 0-100" }
        ),
        // WARNINGS
        Rule(
            id = "W001", name = "Running Exceeds Rated", severity = "warning", source = "Physics / Nameplate",
            fixSuggestion = "Running load should be ≤ rated load", formula = "running ≤ rated",
            field = "runningPowerW", check = { it.runningPowerW > it.ratedPowerW * 1.05 }, makeMessage = { "Running ${it.runningPowerW} W exceeds rated ${it.ratedPowerW} W" }
        ),
        Rule(
            id = "W002", name = "High THD — Filter Recommended", severity = "warning", source = "IEEE 519",
            fixSuggestion = "Install passive/active filter", formula = "THD_derate=0.9 if THD>15%",
            field = "thdPercent", check = { it.thdPercent > 15.0 }, makeMessage = { "THD ${it.thdPercent}% exceeds 15% policy threshold" }
        ),
        Rule(
            id = "W003", name = "Criticality Mismatch", severity = "warning", source = "Validation Engine",
            fixSuggestion = "Mark Critical/Essential if 24/7", field = "criticality",
            check = { it.continuousLoad && it.criticality == "Optional" }, makeMessage = { "Continuous load marked as Optional — inconsistent" }
        ),
        Rule(
            id = "W004", name = "Hours Exceed 24", severity = "warning", source = "Validation Engine",
            fixSuggestion = "Day + night must total ≤ 24 hours", field = "hours",
            check = { (it.dayHoursSummer + it.nightHoursSummer) > 24.0 || (it.dayHoursWinter + it.nightHoursWinter) > 24.0 },
            makeMessage = { "Operating hours > 24: summer=${(it.dayHoursSummer + it.nightHoursSummer)}h, winter=${(it.dayHoursWinter + it.nightHoursWinter)}h" }
        ),
        Rule(
            id = "W005", name = "Simultaneous Units > Qty", severity = "warning", source = "Validation Engine",
            fixSuggestion = "maxSimultaneous must be ≤ quantity", field = "maxSimultaneousUnits",
            check = { it.maxSimultaneousUnits > it.quantity }, makeMessage = { "maxSimultaneous=${it.maxSimultaneousUnits} > quantity=${it.quantity}" }
        ),
        Rule(
            id = "W006", name = "Low Confidence Source", severity = "warning", source = "Data Quality Policy",
            fixSuggestion = "Replace with measured or manufacturer data", field = "dataSource",
            check = { it.dataSource == "Estimated" }, makeMessage = { "Data source is Estimated — confidence is low" }
        ),
        Rule(
            id = "W007", name = "Surge Exceeds Tolerance", severity = "warning", source = "NEC 690.8",
            fixSuggestion = "Use soft-starter or VFD", field = "surgeMultiplier",
            check = { it.surgeMultiplier > 5.0 && it.ratedPowerW > 1000.0 }, makeMessage = { "Surge ${it.surgeMultiplier}× for ${it.ratedPowerW} W load — verify inverter" }
        ),
        Rule(
            id = "W008", name = "Duty Cycle Range", severity = "warning", source = "Validation Engine",
            fixSuggestion = "Use 0-100%", field = "dutyCyclePercent",
            check = { it.dutyCyclePercent < 0.0 || it.dutyCyclePercent > 100.0 }, makeMessage = { "Duty cycle ${it.dutyCyclePercent}% outside 0-100" }
        ),
        Rule(
            id = "W009", name = "Ku Out of Range", severity = "warning", source = "Validation Engine",
            fixSuggestion = "Use values between 0 and 1", field = "utilizationFactorKu",
            check = { it.utilizationFactorKu < 0.0 || it.utilizationFactorKu > 1.0 }, makeMessage = { "Ku=${it.utilizationFactorKu} outside [0,1]" }
        ),
        Rule(
            id = "W010", name = "Demand Factor Range", severity = "warning", source = "NEC 220",
            fixSuggestion = "Use values between 0 and 1", field = "demandFactor",
            check = { it.demandFactor < 0.0 || it.demandFactor > 1.0 }, makeMessage = { "Demand factor=${it.demandFactor} outside [0,1]" }
        ),
        // ADVISORIES
        Rule(
            id = "A001", name = "Phantom w/o Standby Flag", severity = "advisory", source = "Data Quality",
            fixSuggestion = "Mark standbyLoad = true", field = "standbyLoad",
            check = { it.phantomLoadW > 0.0 && !it.standbyLoad }, makeMessage = { "${it.phantomLoadW} W phantom detected but not flagged as standby" }
        ),
        Rule(
            id = "A002", name = "Deferrable Not Shiftable", severity = "advisory", source = "Solar Optimization",
            fixSuggestion = "Enable shiftableToDaytime", field = "shiftableToDaytime",
            check = { it.deferrableLoad && !it.shiftableToDaytime }, makeMessage = { "Deferrable load not flagged shiftable-to-daytime" }
        ),
        Rule(
            id = "A003", name = "Critical Load with Surge", severity = "advisory", source = "NEC 690.8",
            fixSuggestion = "Plan UPS or oversized inverter", field = "surgeMultiplier",
            check = { it.criticality == "Critical" && it.surgeMultiplier >= 3.0 }, makeMessage = { "Critical load with ${it.surgeMultiplier}× surge needs UPS/backup planning" }
        ),
        Rule(
            id = "A004", name = "High Diversity Factor", severity = "advisory", source = "IEC Conv.",
            fixSuggestion = "Re-verify diversity factor (1.0-2.0 typical)", field = "diversityFactor",
            check = { it.diversityFactor > 2.0 }, makeMessage = { "Diversity ${it.diversityFactor} unusually high" }
        ),
        Rule(
            id = "A005", name = "Missing Tag", severity = "advisory", source = "Best Practice",
            fixSuggestion = "Add a unique tag (e.g., AC-LR-01)", field = "loadTag",
            check = { it.loadTag.trim().isEmpty() }, makeMessage = { "Missing tag identifier" }
        ),
        Rule(
            id = "A006", name = "24/7 Without Continuous", severity = "advisory", source = "Validation Engine",
            fixSuggestion = "Enable continuousLoad", field = "continuousLoad",
            check = { it.timeProfileType == "24/7" && !it.continuousLoad }, makeMessage = { "24/7 profile but continuousLoad=false" }
        ),
        Rule(
            id = "A007", name = "EV w/o Shift", severity = "advisory", source = "Solar Optimization",
            fixSuggestion = "Enable shiftableToDaytime", field = "shiftableToDaytime",
            check = { it.categoryMain == "EV" && !it.shiftableToDaytime }, makeMessage = { "EV charging should shift to solar window" }
        ),
        // INFO
        Rule(
            id = "I001", name = "PF Below 0.7", severity = "info", source = "IEEE 519",
            fixSuggestion = "Consider power factor correction", field = "powerFactor",
            check = { it.powerFactor < 0.7 && it.powerFactor > 0 }, makeMessage = { "Low PF ${it.powerFactor} — capacitor bank recommended" }
        ),
        Rule(
            id = "I002", name = "Imported Estimated", severity = "info", source = "Audit",
            fixSuggestion = "Verify and upgrade source", field = "confidenceLevel",
            check = { it.dataSource == "Estimated" && it.confidenceLevel == "Low" }, makeMessage = { "Imported with Low confidence — verify before audit" }
        ),
        Rule(
            id = "I003", name = "High kVA Load", severity = "info", source = "NEC 220",
            fixSuggestion = "Consider 3Ø connection if not already", field = "phaseType",
            check = { it.ratedPowerW > 5000.0 && it.phaseType == "1Ø" }, makeMessage = { "Large single-phase load — consider 3-phase" }
        ),
        Rule(
            id = "I004", name = "Phantom Cumulative Risk", severity = "info", source = "Energy Audit",
            fixSuggestion = "Use switched outlets or smart plugs", field = "phantomLoadW",
            check = { it.phantomLoadW > 5.0 && it.quantity >= 3 }, makeMessage = { "${it.phantomLoadW} W × ${it.quantity} = ${it.phantomLoadW * it.quantity} W phantom drain" }
        ),
        // ASSUMPTIONS
        Rule(
            id = "AS001", name = "Default PF in use", severity = "assumption", source = "Policy PF-RES-MOTOR/ELECTRONIC",
            fixSuggestion = "Override with measured PF if available", field = "powerFactor",
            check = { it.powerFactor == 0.9 && it.dataSource != "Measured" }, makeMessage = { "Using default PF 0.9 — actual may differ" }
        ),
        Rule(
            id = "AS002", name = "Default Efficiency", severity = "assumption", source = "Policy EFF-MOTOR-STANDARD",
            fixSuggestion = "Verify nameplate efficiency", field = "efficiency",
            check = { it.efficiency == 85.0 && it.dataSource != "Measured" }, makeMessage = { "Using default efficiency 85%" }
        ),
        Rule(
            id = "AS003", name = "Default Ku", severity = "assumption", source = "Policy KU-RES-CONTINUOUS",
            fixSuggestion = "Override based on actual usage", field = "utilizationFactorKu",
            check = { it.utilizationFactorKu == 0.8 && it.dataSource != "Measured" }, makeMessage = { "Using default utilization Ku=0.8" }
        ),
        Rule(
            id = "AS004", name = "Default Hours", severity = "assumption", source = "Policy PROFILE-*",
            fixSuggestion = "Match to actual user schedule", field = "dayHoursSummer",
            check = { it.dayHoursSummer == 4.0 && it.nightHoursSummer == 4.0 }, makeMessage = { "Using default season hours (4 day / 4 night)" }
        )
    )

    fun validateLoad(l: LoadEntity): List<RuleIssue> {
        val issues = mutableListOf<RuleIssue>()
        RULES.forEach { r ->
            if (r.check(l)) {
                issues.add(
                    RuleIssue(
                        ruleId = r.id,
                        name = r.name,
                        field = r.field,
                        message = r.makeMessage(l),
                        severity = r.severity,
                        source = r.source,
                        fixSuggestion = r.fixSuggestion,
                        relatedFormula = r.formula,
                        loadId = l.id,
                        loadName = if (l.loadName.isEmpty()) "(unnamed)" else l.loadName
                    )
                )
            }
        }
        return issues
    }

    fun getSeverityPriority(severity: String): Int {
        return when (severity.lowercase()) {
            "error" -> 1
            "warning" -> 2
            "advisory" -> 3
            "info" -> 4
            "assumption" -> 5
            else -> 6
        }
    }

    fun validateAllLoads(loads: List<LoadEntity>): List<RuleIssue> {
        val list = loads.flatMap { validateLoad(it) }
        return list.sortedWith(
            compareBy(
                { getSeverityPriority(it.severity) },
                { it.ruleId },
                { it.loadName },
                { it.loadId }
            )
        )
    }

    data class ValidationMatrix(
        val totalCount: Int,
        val errors: List<RuleIssue>,
        val warnings: List<RuleIssue>,
        val advisories: List<RuleIssue>,
        val infos: List<RuleIssue>,
        val assumptions: List<RuleIssue>
    )

    fun getValidationMatrix(loads: List<LoadEntity>): ValidationMatrix {
        val all = validateAllLoads(loads)
        return ValidationMatrix(
            totalCount = all.size,
            errors = all.filter { it.severity == "error" },
            warnings = all.filter { it.severity == "warning" },
            advisories = all.filter { it.severity == "advisory" },
            infos = all.filter { it.severity == "info" },
            assumptions = all.filter { it.severity == "assumption" }
        )
    }
}
