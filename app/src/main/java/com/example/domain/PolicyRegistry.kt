package com.example.domain

object PolicyRegistry {

    data class Policy(
        val policyId: String,
        val name: String,
        val scope: String,
        val defaultValue: Double,
        val unit: String,
        val minRange: Double,
        val maxRange: Double,
        val sourceType: String,
        val sourceReference: String,
        val confidenceLevel: String, // High, Medium, Low
        val engineeringRationale: String,
        val overrideAllowed: String, // Yes, No
        val lastReviewed: String,
        val reviewNotes: String
    )

    data class PolicyStats(
        val total: Int,
        val highConfidence: Int,
        val mediumConfidence: Int,
        val lowConfidence: Int,
        val sourceCounts: Map<String, Int>,
        val lastReviewed: String
    )

    val POLICY_PACK = listOf(
        Policy(
            policyId = "PF-RES-RESISTIVE",
            name = "Power Factor — Residential Resistive Load",
            scope = "category:Lighting + sub:LED/Incandescent + electricalType:AC",
            defaultValue = 0.95, unit = "pf", minRange = 0.85, maxRange = 1.0,
            sourceType = "IEEE", sourceReference = "IEEE 141-1993, §3.5 — Resistive lighting loads",
            confidenceLevel = "High",
            engineeringRationale = "Modern LED drivers with active PFC achieve >0.9 PF. Resistive elements like water heaters enjoy unity (1.0).",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "Validated against measured data from 240+ sites."
        ),
        Policy(
            policyId = "PF-RES-MOTOR",
            name = "Power Factor — Residential Motor Load",
            scope = "category:Kitchen,Pump,HVAC + isMotor:true",
            defaultValue = 0.82, unit = "pf", minRange = 0.60, maxRange = 0.95,
            sourceType = "NEC", sourceReference = "NEC 430.22 typical motor design PF",
            confidenceLevel = "High",
            engineeringRationale = "Single-phase motors without capacitor correction typ. 0.75-0.85. PSC with run capacitors are 0.85-0.95.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "PSC motors with run capacitors exceed 0.9."
        ),
        Policy(
            policyId = "PF-RES-ELECTRONIC",
            name = "Power Factor — Residential Electronic Load",
            scope = "category:IT,Kitchen,Office + hasStandby:true",
            defaultValue = 0.65, unit = "pf", minRange = 0.50, maxRange = 0.95,
            sourceType = "Empirical", sourceReference = "ENERGY STAR electronics database",
            confidenceLevel = "Medium",
            engineeringRationale = "Uncorrected SMPS draw highly distorted current spikes at voltage peaks. Active-PFC reach 0.95+.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "Premium brands feature active-PFC power supplies."
        ),
        Policy(
            policyId = "PF-COM-OFFICE",
            name = "Power Factor — Commercial Office",
            scope = "building:Office + electricalType:AC",
            defaultValue = 0.85, unit = "pf", minRange = 0.75, maxRange = 0.95,
            sourceType = "IEEE", sourceReference = "IEEE 1100-2005",
            confidenceLevel = "High",
            engineeringRationale = "Cumulative composite PF of fluorescent/LED ballasts, copy machines, and IT setups yields 0.85 average.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "PF-IND-MOTOR",
            name = "Power Factor — Industrial Motor",
            scope = "category:Industrial + isMotor:true + phaseType:3Ø",
            defaultValue = 0.86, unit = "pf", minRange = 0.75, maxRange = 0.95,
            sourceType = "NEC", sourceReference = "NEC 430.22 / NEMA MG-1",
            confidenceLevel = "High",
            engineeringRationale = "Three-phase induction motors operated at full load typically achieve 0.85-0.88 PF.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "PF-HEATER",
            name = "Power Factor — Resistive Heater",
            scope = "category:HVAC,Water + isResistive:true",
            defaultValue = 1.0, unit = "pf", minRange = 0.98, maxRange = 1.0,
            sourceType = "IEEE", sourceReference = "Pure circuit resistance physics",
            confidenceLevel = "High",
            engineeringRationale = "Pure resistance element has no reactive components, rendering current exactly in-phase with voltage (PF=1.0).",
            overrideAllowed = "No", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "EFF-LED-MODERN",
            name = "Efficiency — LED Lighting",
            scope = "category:Lighting + sub:LED*",
            defaultValue = 90.0, unit = "%", minRange = 80.0, maxRange = 98.0,
            sourceType = "Manufacturer", sourceReference = "ENERGY STAR LED efficacy guide",
            confidenceLevel = "High",
            engineeringRationale = "Modern LED drivers supply extremely high wall-plug efficiency (85% to 95%).",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "EFF-MOTOR-STANDARD",
            name = "Efficiency — Standard Motor",
            scope = "isMotor:true + size:<10HP",
            defaultValue = 85.0, unit = "%", minRange = 75.0, maxRange = 95.0,
            sourceType = "NEC", sourceReference = "NEMA Premium Efficiency table (IE3)",
            confidenceLevel = "High",
            engineeringRationale = "Standard NEMA IE2/IE3 motors have nominal efficiencies between 84% and 91%.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "SURGE-RESISTIVE",
            name = "Surge Multiplier — Resistive",
            scope = "isMotor:false + isResistive:true",
            defaultValue = 1.0, unit = "×", minRange = 1.0, maxRange = 1.5,
            sourceType = "Engineering", sourceReference = "Physics — no capacitive/inductive reactance",
            confidenceLevel = "High",
            engineeringRationale = "Resistive coils experience zero reactances, hence nameplate current is drawn instantly without starting surge.",
            overrideAllowed = "No", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "SURGE-PSC-SMALL",
            name = "Surge Multiplier — Small PSC Motor",
            scope = "isMotor:true + size:<1HP",
            defaultValue = 3.0, unit = "×", minRange = 2.0, maxRange = 5.0,
            sourceType = "NEC", sourceReference = "NEC 430.32 LRA parameters",
            confidenceLevel = "High",
            engineeringRationale = "Small single-phase pumps/fans Split-phase motors draw 3-5 times standard running FLC.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "SURGE-CAPACITOR-MED",
            name = "Surge Multiplier — Capacitor-Start Motor",
            scope = "isMotor:true + size:1-5HP",
            defaultValue = 5.0, unit = "×", minRange = 4.0, maxRange = 6.0,
            sourceType = "NEC", sourceReference = "NEC 430.32",
            confidenceLevel = "High",
            engineeringRationale = "Robust single-phase compressors or pumps draw 400%-600% locked-rotor currents at start.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "SURGE-DOL-LARGE",
            name = "Surge Multiplier — DOL 3Ø Motor",
            scope = "isMotor:true + size:>5HP + phaseType:3Ø",
            defaultValue = 7.0, unit = "×", minRange = 5.0, maxRange = 8.0,
            sourceType = "NEC", sourceReference = "NEC 430.32 / NEMA MG-1",
            confidenceLevel = "High",
            engineeringRationale = "Industrial DOL Induction motors require high starting torque, necessitating up to 7-8× starting surge.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "SURGE-COMPRESSOR",
            name = "Surge Multiplier — Hermetic Compressor",
            scope = "category:Kitchen,HVAC + sub:Compressor*",
            defaultValue = 5.0, unit = "×", minRange = 4.0, maxRange = 7.0,
            sourceType = "Manufacturer", sourceReference = "Sealed compressor FLC data tables",
            confidenceLevel = "High",
            engineeringRationale = "Hermetic refrigeration pumps starts under heavy backpressures, drawing significant LRA (30-60A).",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "SURGE-INVERTER-DRIVE",
            name = "Surge Multiplier — VFD / Inverter Drive",
            scope = "isMotor:true + hasVFD:true",
            defaultValue = 1.2, unit = "×", minRange = 1.0, maxRange = 1.5,
            sourceType = "Manufacturer", sourceReference = "Commercial compressor soft-start specs",
            confidenceLevel = "High",
            engineeringRationale = "Variable Frequency Drives ramp frequencies, maintaining low currents and restricting surge currents (1.0-1.2×).",
            overrideAllowed = "No", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "DF-RES-LIGHTING",
            name = "Demand Factor — Residential Lighting",
            scope = "category:Lighting + building:Residential",
            defaultValue = 1.0, unit = "×", minRange = 0.8, maxRange = 1.0,
            sourceType = "NEC", sourceReference = "NEC 220.42",
            confidenceLevel = "High",
            engineeringRationale = "First 3 kVA computed at 100%, then remaining at 35%. Conservative baseline is 1.0 maximum.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "DF-RES-RECEP",
            name = "Demand Factor — General Receptacle",
            scope = "category:Office,Other + building:Residential",
            defaultValue = 0.5, unit = "×", minRange = 0.3, maxRange = 0.7,
            sourceType = "NEC", sourceReference = "NEC 220.44",
            confidenceLevel = "High",
            engineeringRationale = "Convenience receptacle sockets are highly unlikely to be utilized simultaneously.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "CF-MOTOR-INDUSTRIAL",
            name = "Coincidence Factor — Industrial Motors",
            scope = "category:Industrial + count:>3",
            defaultValue = 0.7, unit = "×", minRange = 0.5, maxRange = 0.9,
            sourceType = "IEC", sourceReference = "IEC 60364-1 index guidelines",
            confidenceLevel = "Medium",
            engineeringRationale = "Machine processes operate disjoint patterns; simultaneous starts are prevented via interlock configurations.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "CF-RES-MIXED",
            name = "Coincidence Factor — Residential Mixed",
            scope = "building:Residential + loadCount:>10",
            defaultValue = 0.7, unit = "×", minRange = 0.5, maxRange = 0.9,
            sourceType = "IEC", sourceReference = "IEC residential loading index standard",
            confidenceLevel = "Medium",
            engineeringRationale = "Household loads operate randomly; cumulative actual coincident demand loads average 50% to 90% of aggregate.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "DIV-MAXIMUM",
            name = "Diversity Factor — Aggregate Maximum",
            scope = "global:peak-aggregation",
            defaultValue = 1.2, unit = "×", minRange = 1.0, maxRange = 2.5,
            sourceType = "IEEE", sourceReference = "IEEE 141 index",
            confidenceLevel = "Medium",
            engineeringRationale = "Ratio of individual peak averages divided by aggregate actual coincident capacity peak.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "KU-RES-CONTINUOUS",
            name = "Utilization Factor — Continuous Residential",
            scope = "continuousLoad:true + criticality:!=Optional",
            defaultValue = 0.8, unit = "×", minRange = 0.5, maxRange = 1.0,
            sourceType = "Empirical", sourceReference = "Consumer electrical load index data",
            confidenceLevel = "Medium",
            engineeringRationale = "Loads kept continuously active operate near 80% nameplate capacity on average.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "THD-LIMIT-IEEE519",
            name = "THD Limit — IEEE 519",
            scope = "global:harmonic-analysis",
            defaultValue = 8.0, unit = "%", minRange = 0.0, maxRange = 20.0,
            sourceType = "IEEE", sourceReference = "IEEE 519-2014, Table 1",
            confidenceLevel = "High",
            engineeringRationale = "IEEE 519 restricts point-of-common coupling harmonic voltage distortions below 8%.",
            overrideAllowed = "No", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "THD-DERATE-FACTOR",
            name = "THD Derate Factor — High-Harmonic Loads",
            scope = "thdPercent:>15",
            defaultValue = 0.9, unit = "×", minRange = 0.8, maxRange = 1.0,
            sourceType = "IEEE", sourceReference = "Transformer derating standard indexes",
            confidenceLevel = "Medium",
            engineeringRationale = "Harmonic distortions trigger excessive eddy core damages, requiring a 10% safety derating offset.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "PHASE-IMB-LIMIT",
            name = "Phase Imbalance Limit",
            scope = "global:phase-balancing",
            defaultValue = 10.0, unit = "%", minRange = 0.0, maxRange = 20.0,
            sourceType = "IEC 60364", sourceReference = "IEC 60364-5-52 imbalance standards",
            confidenceLevel = "High",
            engineeringRationale = "Distributions showing imbalance >10% trigger severe neutrals heating and stator losses.",
            overrideAllowed = "No", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "INV-OVERSIZE-MIN",
            name = "Inverter Oversize — Minimum",
            scope = "solar:inverter-sizing",
            defaultValue = 25.0, unit = "%", minRange = 10.0, maxRange = 50.0,
            sourceType = "Solar-Specific", sourceReference = "NEC 690.8 / NABCEP",
            confidenceLevel = "High",
            engineeringRationale = "Maintains a 1.25 oversize parameter on solar system output ratings to ensure safety under surges.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "Standard off-grid system safety guidelines."
        ),
        Policy(
            policyId = "INV-OVERSIZE-MOTOR",
            name = "Inverter Oversize — Motor-Heavy",
            scope = "motor-loads:>30%",
            defaultValue = 50.0, unit = "%", minRange = 30.0, maxRange = 100.0,
            sourceType = "Solar-Specific", sourceReference = "Off-grid solar design guidelines",
            confidenceLevel = "High",
            engineeringRationale = "High reactive motor loads demand significant oversized inverter headroom to permit inrush capacities.",
            overrideAllowed = "Yes", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "CONFIDENCE-MEASURED",
            name = "Confidence — Measured Data",
            scope = "dataSource:Measured",
            defaultValue = 1.0, unit = "pf", minRange = 0.0, maxRange = 0.0,
            sourceType = "Engineering", sourceReference = "Direct validation",
            confidenceLevel = "High",
            engineeringRationale = "Physical measurements supply absolute confidence metrics.",
            overrideAllowed = "No", lastReviewed = "2025-01-15", reviewNotes = "-"
        ),
        Policy(
            policyId = "CONFIDENCE-ESTIMATED",
            name = "Confidence — Estimated",
            scope = "dataSource:Estimated",
            defaultValue = 0.0, unit = "pf", minRange = 0.0, maxRange = 0.0,
            sourceType = "Engineering", sourceReference = "Default assumptions",
            confidenceLevel = "Low",
            engineeringRationale = "Arbitrary baselines require validation during final construction stages.",
            overrideAllowed = "No", lastReviewed = "2025-01-15", reviewNotes = "-"
        )
    )

    fun getPolicyStats(): PolicyStats {
        val total = POLICY_PACK.size
        val high = POLICY_PACK.count { it.confidenceLevel == "High" }
        val med = POLICY_PACK.count { it.confidenceLevel == "Medium" }
        val low = POLICY_PACK.count { it.confidenceLevel == "Low" }
        
        val sourceCounts = POLICY_PACK.groupBy { it.sourceType }.mapValues { it.value.size }
        val lastReviewed = POLICY_PACK.maxOfOrNull { it.lastReviewed } ?: "2025-01-15"

        return PolicyStats(
            total = total,
            highConfidence = high,
            mediumConfidence = med,
            lowConfidence = low,
            sourceCounts = sourceCounts,
            lastReviewed = lastReviewed
        )
    }
}
