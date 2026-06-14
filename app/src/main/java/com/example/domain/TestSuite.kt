package com.example.domain

import com.example.data.LoadEntity
import kotlin.math.abs
import kotlin.math.sqrt

object TestSuite {

    data class TestResult(
        val category: String, // Unit, Property, Golden, Formula, Regression, Integration
        val name: String,
        val passed: Boolean,
        val message: String = "",
        val expected: String = "",
        val actual: String = ""
    )

    data class SuiteResult(
        val results: List<TestResult>,
        val totalCount: Int,
        val passedCount: Int,
        val failedCount: Int,
        val passPercentage: Double,
        val timestamp: String
    )

    fun runAllTests(): SuiteResult {
        val results = mutableListOf<TestResult>()
        
        fun t(category: String, name: String, block: () -> Pair<Boolean, String>) {
            try {
                val (ok, extra) = block()
                results.add(TestResult(category, name, ok, extra))
            } catch (e: Exception) {
                results.add(TestResult(category, name, false, "Threw exception: ${e.message}"))
            }
        }

        fun tWithExpected(category: String, name: String, expected: String, block: () -> Pair<Boolean, Pair<String, String>>) {
            try {
                val (ok, vals) = block()
                results.add(TestResult(category, name, ok, expected = vals.first, actual = vals.second))
            } catch (e: Exception) {
                results.add(TestResult(category, name, false, "Threw exception: ${e.message}", expected = expected, actual = "Exception"))
            }
        }

        val emptyLoad = LoadEntity(
            id = "test-empty", loadId = "LD-0000", loadTag = "", loadName = "", arabicName = "",
            categoryMain = "Lighting", categorySub = "", spaceArea = "", buildingLevel = "",
            distributionBoard = "", circuitReference = "", description = "", electricalType = "AC",
            voltageNominal = 220, frequency = "50Hz", phaseType = "1Ø", ratedPowerW = 0.0,
            runningPowerW = 0.0, measuredPowerW = 0.0, powerFactor = 0.9, efficiency = 85.0,
            thdPercent = 10.0, harmonicClass = "Nonlinear", lockedRotorCurrentA = 0.0,
            surgeMultiplier = 1.0, surgePowerW = 0.0, quantity = 1, dutyCyclePercent = 60.0,
            utilizationFactorKu = 0.8, demandFactor = 0.9, coincidenceFactor = 0.7, diversityFactor = 1.2,
            continuousLoad = false, continuousHours = 0.0, criticality = "Normal",
            deferrableLoad = false, shiftableToDaytime = false, smartControlled = false, autoStart = false,
            cyclingLoad = false, standbyLoad = false, phantomLoadW = 0.0, dayHoursSummer = 4.0,
            nightHoursSummer = 4.0, dayHoursWinter = 3.0, nightHoursWinter = 5.0, weekdayHours = 8.0,
            weekendHours = 10.0, operatingDaysPerWeek = 7, operatingDaysPerYear = 365,
            operatingMode = "Scheduled", timeProfileType = "Evening Peak", peakStartTime = "18:00",
            peakEndTime = "22:00", hourlyProfile = List(24) { 0 }, simultaneousGroup = "",
            maxSimultaneousUnits = 1, dataSource = "Estimated", measurementMethod = "Estimate",
            measurementDate = "", confidenceLevel = "Medium", notes = ""
        )

        // 1. UNIT TESTS
        t("Unit", "calcConnectedLoad: 100W x 2 = 200W") {
            val l = emptyLoad.copy(ratedPowerW = 100.0, quantity = 2)
            val v = Calculations.calcConnectedLoad(l)
            (v == 200.0) to "Calculated: $v W"
        }

        t("Unit", "calcConnectedLoad: 0W load handles cleanly") {
            val l = emptyLoad.copy(ratedPowerW = 0.0, quantity = 5)
            val v = Calculations.calcConnectedLoad(l)
            (v == 0.0) to "Calculated: $v W"
        }

        t("Unit", "calcSurgePower: 100W * 3x * 2qty = 600W") {
            val l = emptyLoad.copy(ratedPowerW = 100.0, quantity = 2, surgeMultiplier = 3.0)
            val v = Calculations.calcSurgePower(l)
            (v == 600.0) to "Calculated: $v W"
        }

        tWithExpected("Unit", "calcApparentPower: 1000W @ PF 0.8 = 1.25 kVA", "1.250") {
            val l = emptyLoad.copy(ratedPowerW = 1000.0, quantity = 1, powerFactor = 0.8)
            val v = Calculations.calcApparentPower(l)
            (abs(v - 1.25) < 1e-9) to (Pair("1.250 kVA", String.format("%.3f kVA", v)))
        }

        t("Unit", "calcFullLoadCurrent (1Ø): 1000W at 220V, PF 0.9 = 5.05A") {
            val l = emptyLoad.copy(ratedPowerW = 1000.0, voltageNominal = 220, powerFactor = 0.9, phaseType = "1Ø")
            val v = Calculations.calcFullLoadCurrent(l)
            (abs(v - 5.0505) < 0.01) to "Calculated: ${String.format("%.3f A", v)}"
        }

        t("Unit", "calcFullLoadCurrent (3Ø): handles root-3 formulas") {
            val l = emptyLoad.copy(ratedPowerW = 10000.0, voltageNominal = 380, powerFactor = 0.9, phaseType = "3Ø")
            val v = Calculations.calcFullLoadCurrent(l)
            val expected = 10000.0 / (sqrt(3.0) * 380.0 * 0.9)
            (abs(v - expected) < 0.000001) to "Calculated: ${String.format("%.3f A", v)}"
        }

        t("Unit", "calcDailyEnergy: verifies summer logic") {
            val l = emptyLoad.copy(
                ratedPowerW = 100.0, runningPowerW = 100.0, quantity = 1,
                utilizationFactorKu = 1.0, dutyCyclePercent = 100.0, demandFactor = 1.0,
                dayHoursSummer = 4.0, nightHoursSummer = 4.0, operatingDaysPerWeek = 7
            )
            val v = Calculations.calcDailyEnergy(l, "summer")
            (v == 800.0) to "Calculated energy: $v Wh"
        }

        // 2. PROPERTY TESTS
        t("Property", "calcConnectedLoad ≥ 0 for any positive power and qty") {
            val l = emptyLoad.copy(ratedPowerW = 50.0, quantity = 2)
            (Calculations.calcConnectedLoad(l) >= 0.0) to "Passes"
        }

        t("Property", "calcSurgePower is always ≥ connected load when surge multiplier ≥ 1") {
            val l = emptyLoad.copy(ratedPowerW = 100.0, quantity = 2, surgeMultiplier = 5.0)
            val surf = Calculations.calcSurgePower(l)
            val conn = Calculations.calcConnectedLoad(l)
            (surf >= conn) to "Surge: $surf W, Connected: $conn W"
        }

        t("Property", "Day Load profile sums correctly to runtime energy") {
            val l = emptyLoad.copy(
                ratedPowerW = 100.0, runningPowerW = 100.0, quantity = 1,
                utilizationFactorKu = 1.0, dutyCyclePercent = 100.0, demandFactor = 1.0,
                dayHoursSummer = 6.0, nightHoursSummer = 0.0, operatingDaysPerWeek = 7,
                timeProfileType = "Day Load"
            )
            val hp = Calculations.calcHourlyOperatingLoad(l)
            val sum = hp.sum()
            (abs(sum - 600.0) < 1e-6) to "Total aggregated hours: $sum Wh"
        }

        // 3. GOLDEN REFS
        t("Golden", "Basic preset contains exactly 10 loads") {
            val loads = ApplianceLibrary.makePresetLoads("basic")
            (loads.size == 10) to "Contains ${loads.size} loads"
        }

        t("Golden", "Professional preset contains exactly 14 loads") {
            val loads = ApplianceLibrary.makePresetLoads("professional")
            (loads.size == 14) to "Contains ${loads.size} loads"
        }

        t("Golden", "Commercial preset contains exactly 7 loads") {
            val loads = ApplianceLibrary.makePresetLoads("commercial")
            (loads.size == 7) to "Contains ${loads.size} loads"
        }

        t("Golden", "APPLIANCE_LIBRARY houses at least 30 reference templates") {
            val sz = ApplianceLibrary.APPLIANCE_LIBRARY.size
            (sz >= 30) to "Count: $sz"
        }

        t("Golden", "POLICY_PACK holds at least 25 strict policy registries") {
            val sz = PolicyRegistry.POLICY_PACK.size
            (sz >= 25) to "Count: $sz"
        }

        // 4. REGRESSION
        t("Regression", "Empty loads summary executes gracefully") {
            val sum = Calculations.computeSummary(emptyList())
            (sum.totalConnectedLoadW == 0.0) to "Executed safely: connected is ${sum.totalConnectedLoadW}"
        }

        t("Regression", "validateLoad returns array-like rules results safely") {
            val rulesVal = ValidationRules.validateLoad(emptyLoad)
            (rulesVal.isNotEmpty()) to "Count of issues: ${rulesVal.size}"
        }

        t("Regression", "Phase balance executes gracefully on empty list") {
            val bal = PhaseBalancer.balancePhases(emptyList())
            (bal.imbalancePercent == 0.0 && bal.balancingScore == 100) to "Score: ${bal.balancingScore}, imbalance: ${bal.imbalancePercent}"
        }

        // 5. INT FORMULAS
        t("Formula", "kVA formula complies with kVA = kW / PF") {
            val l = emptyLoad.copy(ratedPowerW = 1000.0, powerFactor = 0.5)
            val kva = Calculations.calcApparentPower(l)
            (abs(kva - 2.0) < 1e-9) to "Apparent power: $kva kVA"
        }

        t("Formula", "Annual energy aggregation averages seasonally") {
            val l = emptyLoad.copy(
                ratedPowerW = 100.0, runningPowerW = 100.0, quantity = 1,
                utilizationFactorKu = 1.0, dutyCyclePercent = 100.0, demandFactor = 1.0,
                dayHoursSummer = 4.0, nightHoursSummer = 4.0,
                dayHoursWinter = 3.0, nightHoursWinter = 5.0,
                operatingDaysPerWeek = 7, operatingDaysPerYear = 365
            )
            val annual = Calculations.calcAnnualEnergy(l)
            val summerDaily = Calculations.calcDailyEnergy(l, "summer")
            val winterDaily = Calculations.calcDailyEnergy(l, "winter")
            val expected = summerDaily * 183.0 + winterDaily * 182.0
            (abs(annual - expected) < 1e-3) to "Annual: $annual, Expected: $expected"
        }

        // 6. INTEGRATION / PIPELINE
        t("Integration", "E2E: preset loading -> aggregate summary -> phase balancer pipeline") {
            val p = ApplianceLibrary.makePresetLoads("commercial")
            val s = Calculations.computeSummary(p)
            val b = PhaseBalancer.balancePhases(p)
            (s.totalConnectedLoadW > 0.0 && b.balancingScore in 0..100) to "Connected load: ${s.totalConnectedLoadW} W, balancing score: ${b.balancingScore}"
        }

        val total = results.size
        val passed = results.count { it.passed }
        val failed = total - passed
        val percentage = if (total > 0) (passed.toDouble() / total) * 100.0 else 0.0

        return SuiteResult(
            results = results,
            totalCount = total,
            passedCount = passed,
            failedCount = failed,
            passPercentage = percentage,
            timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date())
        )
    }
}
