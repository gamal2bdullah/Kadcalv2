package com.example.domain

import com.example.data.LoadEntity
import kotlin.math.abs
import kotlin.math.max

object PhaseBalancer {

    data class Allocation(
        val loadId: String,
        val loadName: String,
        val phase: String, // L1, L2, L3, 3Ø
        val reasoning: String
    )

    data class PhaseValues(
        val L1: Double,
        val L2: Double,
        val L3: Double
    )

    data class SurgeStacking(
        val phase: String,
        val peakSurge: Double,
        val stackingRisk: String // low, medium, high
    )

    data class Violation(
        val loadName: String,
        val reason: String,
        val severity: String
    )

    data class BalancingResult(
        val allocations: List<Allocation>,
        val phases: PhaseValues,
        val imbalancePercent: Double,
        val imbalanceStatus: String,
        val recommendations: List<String>,
        val violations: List<Violation>,
        val surgeStacking: List<SurgeStacking>,
        val totalConnected: Double,
        val balancingScore: Int
    )

    fun balancePhases(loads: List<LoadEntity>): BalancingResult {
        if (loads.isEmpty()) {
            return BalancingResult(
                allocations = emptyList(),
                phases = PhaseValues(0.0, 0.0, 0.0),
                imbalancePercent = 0.0,
                imbalanceStatus = "excellent",
                recommendations = listOf("No loads available to balance."),
                violations = emptyList(),
                surgeStacking = listOf(
                    SurgeStacking("L1", 0.0, "low"),
                    SurgeStacking("L2", 0.0, "low"),
                    SurgeStacking("L3", 0.0, "low")
                ),
                totalConnected = 0.0,
                balancingScore = 100
            )
        }

        val threePhase = loads.filter { it.phaseType == "3Ø" }
        val onePhase = loads.filter { it.phaseType == "1Ø" }

        var l1Sum = 0.0
        var l2Sum = 0.0
        var l3Sum = 0.0

        // 3Ø loads are distributed equally on L1, L2, L3
        threePhase.forEach { l ->
            val share = Calculations.calcConnectedLoad(l) / 3.0
            l1Sum += share
            l2Sum += share
            l3Sum += share
        }

        // Greedy bin-packing of 1Ø loads based on connected load desc
        val sortedOnePhase = onePhase.map { l ->
            object {
                val load = l
                val w = Calculations.calcConnectedLoad(l)
                val surge = Calculations.calcSurgePower(l)
                val isCrit = l.criticality == "Critical"
                val isHighSurge = l.surgeMultiplier >= 3.0
            }
        }.sortedByDescending { it.w }

        val allocations = mutableListOf<Allocation>()
        
        threePhase.forEach { l ->
            allocations.add(
                Allocation(
                    loadId = l.id,
                    loadName = l.loadName,
                    phase = "3Ø",
                    reasoning = "Distributed equally (Three-phase inherently balanced)"
                )
            )
        }

        val groupAssignments = mutableMapOf<String, String>()

        sortedOnePhase.forEach { it ->
            val g = it.load.simultaneousGroup.trim()
            val targetPhase: String
            if (g.isNotEmpty() && groupAssignments.containsKey(g)) {
                targetPhase = groupAssignments[g]!!
            } else {
                // Select phase with minimum current load
                val minPhase = listOf("L1" to l1Sum, "L2" to l2Sum, "L3" to l3Sum).minByOrNull { it.second }!!.first
                targetPhase = minPhase
                if (g.isNotEmpty()) {
                    groupAssignments[g] = targetPhase
                }
            }

            val curW = it.w
            when (targetPhase) {
                "L1" -> l1Sum += curW
                "L2" -> l2Sum += curW
                "L3" -> l3Sum += curW
            }

            val reasoning = when {
                it.isCrit -> "Critical load assigned to least-loaded phase"
                it.isHighSurge -> "High starting surge (${it.load.surgeMultiplier}×) placed on least-loaded phase"
                g.isNotEmpty() -> "Grouped into phase $targetPhase with group '$g'"
                else -> "Greedy bin-packing allocated to least-loaded phase"
            }

            allocations.add(
                Allocation(
                    loadId = it.load.id,
                    loadName = it.load.loadName,
                    phase = targetPhase,
                    reasoning = reasoning
                )
            )
        }

        val total = l1Sum + l2Sum + l3Sum
        val avg = total / 3.0
        val maxDev = if (avg > 0) {
            maxOf(abs(l1Sum - avg), abs(l2Sum - avg), abs(l3Sum - avg))
        } else {
            0.0
        }
        val imbalancePercent = if (avg > 0) (maxDev / avg) * 100.0 else 0.0

        val imbalanceStatus = when {
            imbalancePercent < 1.0 -> "excellent"
            imbalancePercent < 2.0 -> "good"
            imbalancePercent < 5.0 -> "acceptable"
            imbalancePercent < 10.0 -> "warning"
            else -> "critical"
        }

        val balancingScore = max(0, (100 - imbalancePercent * 5.0).toInt().coerceAtMost(100))

        val recommendations = mutableListOf<String>()
        val violations = mutableListOf<Violation>()

        if (imbalancePercent > 10.0) {
            recommendations.add("Phase imbalance > 10% — IEC 60364 warning. Re-distribute large 1Ø loads across phases.")
            violations.add(
                Violation(
                    loadName = "System",
                    reason = "Phase imbalance exceeds standard limits (10%)",
                    severity = "critical"
                )
            )
        }
        if (imbalancePercent > 5.0) {
            recommendations.add("Consider converting the largest single-phase loads (e.g. water heaters, large EV chargers) to three-phase if feasible.")
        }
        val motorsCount = onePhase.count { it.surgeMultiplier >= 3.0 }
        if (motorsCount > 3) {
            recommendations.add("Detected $motorsCount high-surge motors on single phase lines — configure a sequencer to stagger start times.")
        }
        if (onePhase.isEmpty() && threePhase.isNotEmpty()) {
            recommendations.add("All connected loads are 3-phase. System is inherently perfectly balanced.")
        }
        if (balancingScore >= 90) {
            recommendations.add("Phase balance is highly optimized. Maintain this balance as new loads are introduced.")
        }

        // Surge stacking per phase
        val surgeStacking = listOf("L1", "L2", "L3").map { ph ->
            var peakSurge = threePhase.sumOf { Calculations.calcSurgePower(it) / 3.0 }
            allocations.filter { it.phase == ph }.forEach { a ->
                val l = loads.find { it.id == a.loadId }
                if (l != null) {
                    peakSurge += Calculations.calcSurgePower(l)
                }
            }
            val risk = when {
                peakSurge > 10000.0 -> "high"
                peakSurge > 5000.0 -> "medium"
                else -> "low"
            }
            SurgeStacking(ph, peakSurge, risk)
        }

        return BalancingResult(
            allocations = allocations,
            phases = PhaseValues(l1Sum, l2Sum, l3Sum),
            imbalancePercent = imbalancePercent,
            imbalanceStatus = imbalanceStatus,
            recommendations = recommendations,
            violations = violations,
            surgeStacking = surgeStacking,
            totalConnected = total,
            balancingScore = balancingScore
        )
    }
}
