package com.example.domain

import com.example.data.LoadEntity
import kotlin.math.max
import kotlin.math.sqrt

object Calculations {

    fun calcConnectedLoad(l: LoadEntity): Double {
        return l.ratedPowerW * l.quantity
    }

    fun calcRunningLoad(l: LoadEntity): Double {
        val runningP = if (l.runningPowerW > 0) l.runningPowerW else l.ratedPowerW
        return runningP * l.quantity
    }

    fun calcDemandLoad(l: LoadEntity): Double {
        return calcConnectedLoad(l) * l.utilizationFactorKu
    }

    fun calcSurgePower(l: LoadEntity): Double {
        return l.ratedPowerW * l.surgeMultiplier * l.quantity
    }

    fun calcApparentPower(l: LoadEntity): Double {
        val c = calcConnectedLoad(l)
        val pf = if (l.powerFactor > 0) l.powerFactor else 0.9
        return c / (pf * 1000.0)
    }

    fun calcReactivePower(l: LoadEntity): Double {
        val kva = calcApparentPower(l)
        val kw = (l.ratedPowerW * l.quantity) / 1000.0
        return sqrt(max(0.0, kva * kva - kw * kw))
    }

    fun calcFullLoadCurrent(l: LoadEntity): Double {
        val c = calcConnectedLoad(l)
        val pf = if (l.powerFactor > 0) l.powerFactor else 0.9
        val v = if (l.voltageNominal > 0) l.voltageNominal else 220
        return if (l.phaseType == "3Ø") {
            c / (sqrt(3.0) * v * pf)
        } else {
            c / (v * pf)
        }
    }

    fun calcLockedRotorCurrent(l: LoadEntity): Double {
        return if (l.lockedRotorCurrentA > 0) {
            l.lockedRotorCurrentA * l.quantity
        } else {
            calcFullLoadCurrent(l) * l.surgeMultiplier
        }
    }

    fun calcDailyEnergyActiveDay(l: LoadEntity, season: String): Double {
        val running = calcRunningLoad(l)
        val ku = l.utilizationFactorKu
        val duty = l.dutyCyclePercent / 100.0
        val demand = l.demandFactor
        val dayH = if (season == "summer") l.dayHoursSummer else l.dayHoursWinter
        val nightH = if (season == "summer") l.nightHoursSummer else l.nightHoursWinter
        return running * ku * duty * demand * (dayH + nightH)
    }

    fun calcDailyEnergy(l: LoadEntity, season: String = "summer"): Double {
        val days = l.operatingDaysPerWeek / 7.0
        return calcDailyEnergyActiveDay(l, season) * days
    }

    fun calcDayEnergy(l: LoadEntity, season: String = "summer"): Double {
        val running = calcRunningLoad(l)
        val ku = l.utilizationFactorKu
        val duty = l.dutyCyclePercent / 100.0
        val demand = l.demandFactor
        val dayH = if (season == "summer") l.dayHoursSummer else l.dayHoursWinter
        val days = l.operatingDaysPerWeek / 7.0
        return running * ku * duty * demand * dayH * days
    }

    fun calcNightEnergy(l: LoadEntity, season: String = "summer"): Double {
        val running = calcRunningLoad(l)
        val ku = l.utilizationFactorKu
        val duty = l.dutyCyclePercent / 100.0
        val demand = l.demandFactor
        val nightH = if (season == "summer") l.nightHoursSummer else l.nightHoursWinter
        val days = l.operatingDaysPerWeek / 7.0
        return running * ku * duty * demand * nightH * days
    }

    fun calcAnnualEnergy(l: LoadEntity): Double {
        val summerActive = calcDailyEnergyActiveDay(l, "summer")
        val winterActive = calcDailyEnergyActiveDay(l, "winter")
        val avgActiveWh = (summerActive * 183.0 + winterActive * 182.0) / 365.0
        return avgActiveWh * l.operatingDaysPerYear
    }

    fun calcCoincidentLoad(l: LoadEntity): Double {
        return calcDemandLoad(l) * l.coincidenceFactor
    }

    fun calcDiversifiedLoad(l: LoadEntity): Double {
        return calcCoincidentLoad(l) / l.diversityFactor
    }

    fun defaultProfileForType(t: String): List<Int> {
        return when (t) {
            "Morning Peak" -> listOf(0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0)
            "Noon Peak"    -> listOf(0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0)
            "Evening Peak" -> listOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0)
            "Night Load"   -> listOf(1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1)
            "24/7", "Base Load" -> List(24) { 1 }
            "Day Load"     -> listOf(0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0)
            else           -> listOf(0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0)
        }
    }

    fun calcHourlyOperatingLoad(l: LoadEntity): List<Double> {
        val running = calcRunningLoad(l)
        val ku = l.utilizationFactorKu
        val duty = l.dutyCyclePercent / 100.0
        val totH = l.dayHoursSummer + l.nightHoursSummer
        if (totH == 0.0) return List(24) { 0.0 }
        
        val profile = if (l.hourlyProfile.size == 24 && l.hourlyProfile.any { it > 0 }) {
            l.hourlyProfile
        } else {
            defaultProfileForType(l.timeProfileType)
        }
        val tot = profile.sum().toDouble()
        val finalTot = if (tot == 0.0) 1.0 else tot
        
        return profile.map { v -> (running * ku * duty * v) / finalTot * totH }
    }

    // Standard styling helper formatters
    fun fmtW(w: Double, d: Int = 0): String {
        val absW = kotlin.math.abs(w)
        return when {
            absW >= 1e6 -> String.format("%.2f MW", w / 1e6)
            absW >= 1e3 -> String.format("%.${d}f kW", w / 1e3)
            else -> String.format("%.${d}f W", w)
        }
    }

    fun fmtWh(wh: Double, d: Int = 0): String {
        val absWh = kotlin.math.abs(wh)
        return when {
            absWh >= 1e6 -> String.format("%.2f MWh", wh / 1e6)
            absWh >= 1e3 -> String.format("%.${d}f kWh", wh / 1e3)
            else -> String.format("%.${d}f Wh", wh)
        }
    }

    fun fmtA(a: Double, d: Int = 1): String {
        return String.format("%.${d}f A", a)
    }

    fun fmtKVA(k: Double, d: Int = 2): String {
        return String.format("%.${d}f kVA", k)
    }

    fun fmtPct(p: Double, d: Int = 1): String {
        return String.format("%.${d}f%%", p)
    }

    data class Summary(
        val totalConnectedLoadW: Double,
        val totalRunningLoadW: Double,
        val maximumDemandW: Double,
        val diversifiedLoadW: Double,
        val coincidentPeakLoadW: Double,
        val totalDailyEnergyWh: Double,
        val dayEnergyWh: Double,
        val nightEnergyWh: Double,
        val monthlyEnergyKWh: Double,
        val annualEnergyKWh: Double,
        val peakDemandKW: Double,
        val peakDemandKVA: Double,
        val estimatedMaxCurrentA: Double,
        val maximumSurgeKW: Double,
        val loadFactor: Double,
        val phantomLossWh: Double,
        val criticalLoadWh: Double,
        val deferrableLoadWh: Double,
        val byCategory: List<CategoryEnergy>,
        val byCriticality: List<CategoryEnergy>,
        val hourlyProfile: List<Double>
    )

    data class CategoryEnergy(
        val name: String,
        val value: Double,
        val colorHex: String
    )

    val CHART_COLORS = listOf(
        "#f59e0b", "#ff6a00", "#10b981", "#3b82f6", "#8b5cf6", "#ec4899", 
        "#06b6d4", "#eab308", "#ef4444", "#22c55e", "#a855f7", "#14b8a6", 
        "#f97316", "#84cc16"
    )

    fun computeSummary(loads: List<LoadEntity>): Summary {
        if (loads.isEmpty()) {
            return Summary(
                totalConnectedLoadW = 0.0, totalRunningLoadW = 0.0, maximumDemandW = 0.0,
                diversifiedLoadW = 0.0, coincidentPeakLoadW = 0.0, totalDailyEnergyWh = 0.0,
                dayEnergyWh = 0.0, nightEnergyWh = 0.0, monthlyEnergyKWh = 0.0, annualEnergyKWh = 0.0,
                peakDemandKW = 0.0, peakDemandKVA = 0.0, estimatedMaxCurrentA = 0.0,
                maximumSurgeKW = 0.0, loadFactor = 0.0, phantomLossWh = 0.0,
                criticalLoadWh = 0.0, deferrableLoadWh = 0.0,
                byCategory = emptyList(), byCriticality = emptyList(),
                hourlyProfile = List(24) { 0.0 }
            )
        }

        val totalConnected = loads.sumOf { calcConnectedLoad(it) }
        val totalRunning = loads.sumOf { calcRunningLoad(it) }

        val hourlyP = MutableList(24) { 0.0 }
        val hourlyQ = MutableList(24) { 0.0 }
        loads.forEach { l ->
            val profile = calcHourlyOperatingLoad(l)
            val pf = if (l.powerFactor > 0.0 && l.powerFactor <= 1.0) l.powerFactor else 0.85
            val tanTheta = if (pf < 1.0) sqrt(1.0 - pf * pf) / pf else 0.0
            for (i in 0 until 24) {
                val p = profile[i]
                hourlyP[i] += p
                hourlyQ[i] += p * tanTheta
            }
        }
        val hourlyS = List(24) { i ->
            sqrt(hourlyP[i] * hourlyP[i] + hourlyQ[i] * hourlyQ[i])
        }
        val maxHourly = hourlyP.maxOrNull() ?: 0.0
        val maxHourlyS = hourlyS.maxOrNull() ?: 0.0

        val coincidentPeak = loads.sumOf { calcCoincidentLoad(it) }
        val diversified = loads.sumOf { calcDiversifiedLoad(it) }

        val totalDailyWh = hourlyP.sum()
        // Day hours slice (8-18 index is index 8 to 17) -> index 8 to 17 in Kotlin is (8 until 18)
        val dayE = hourlyP.slice(8 until 18).sum()
        val nightE = totalDailyWh - dayE

        val maxSurge = loads.sumOf { calcSurgePower(it) }
        val peakKW = maxHourly / 1000.0
        val peakKVA = maxHourlyS / 1000.0
        val has3Phase = loads.any { it.phaseType == "3Ø" }
        val avg3PhaseV = loads.filter { it.phaseType == "3Ø" }.map { it.voltageNominal.toDouble() }.average().let { if (it.isNaN() || it <= 0.0) 380.0 else it }
        val avg1PhaseV = loads.filter { it.phaseType != "3Ø" }.map { it.voltageNominal.toDouble() }.average().let { if (it.isNaN() || it <= 0.0) 220.0 else it }
        val estMaxCurrent = if (has3Phase) {
            (peakKVA * 1000.0) / (sqrt(3.0) * avg3PhaseV)
        } else {
            (peakKVA * 1000.0) / avg1PhaseV
        }

        val avg = totalDailyWh / 24.0
        val lf = if (maxHourly > 0) (avg / maxHourly) * 100.0 else 0.0

        val phantomLoss = loads.sumOf { (it.phantomLoadW * it.quantity * 24.0) }
        val criticalLoadWh = loads.filter { it.criticality == "Critical" || it.criticality == "Essential" }
            .sumOf { calcDailyEnergy(it) }
        val deferrableLoadWh = loads.filter { it.deferrableLoad }.sumOf { calcDailyEnergy(it) }

        // Category breakdown
        val catGroup = loads.groupBy { it.categoryMain }
        val byCategory = catGroup.entries.mapIndexed { idx, entry ->
            CategoryEnergy(
                name = entry.key,
                value = entry.value.sumOf { calcDailyEnergy(it) },
                colorHex = CHART_COLORS[idx % CHART_COLORS.size]
            )
        }.sortedByDescending { it.value }

        // Criticality breakdown
        val critGroup = loads.groupBy { it.criticality }
        val critColors = listOf("#ef4444", "#f59e0b", "#3b82f6", "#6b7a9c")
        val byCriticality = critGroup.entries.mapIndexed { idx, entry ->
            CategoryEnergy(
                name = entry.key,
                value = entry.value.sumOf { calcDailyEnergy(it) },
                colorHex = critColors[idx % critColors.size]
            )
        }

        return Summary(
            totalConnectedLoadW = totalConnected,
            totalRunningLoadW = totalRunning,
            maximumDemandW = maxHourly,
            diversifiedLoadW = diversified,
            coincidentPeakLoadW = coincidentPeak,
            totalDailyEnergyWh = totalDailyWh,
            dayEnergyWh = dayE,
            nightEnergyWh = nightE,
            monthlyEnergyKWh = (totalDailyWh * 30.0) / 1000.0,
            annualEnergyKWh = (totalDailyWh * 365.0) / 1000.0,
            peakDemandKW = peakKW,
            peakDemandKVA = peakKVA,
            estimatedMaxCurrentA = estMaxCurrent,
            maximumSurgeKW = maxSurge / 1000.0,
            loadFactor = lf,
            phantomLossWh = phantomLoss,
            criticalLoadWh = criticalLoadWh,
            deferrableLoadWh = deferrableLoadWh,
            byCategory = byCategory,
            byCriticality = byCriticality,
            hourlyProfile = hourlyP
        )
    }
}
