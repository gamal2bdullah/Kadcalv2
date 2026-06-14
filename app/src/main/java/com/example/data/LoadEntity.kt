package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "loads")
@TypeConverters(HourlyProfileConverter::class)
data class LoadEntity(
    @PrimaryKey val id: String,
    val loadId: String,
    val loadTag: String,
    val loadName: String,
    val arabicName: String,
    val categoryMain: String,
    val categorySub: String,
    val spaceArea: String,
    val buildingLevel: String,
    val distributionBoard: String,
    val circuitReference: String,
    val description: String,
    val electricalType: String,
    val voltageNominal: Int,
    val frequency: String,
    val phaseType: String,
    val ratedPowerW: Double,
    val runningPowerW: Double,
    val measuredPowerW: Double,
    val powerFactor: Double,
    val efficiency: Double,
    val thdPercent: Double,
    val harmonicClass: String,
    val lockedRotorCurrentA: Double,
    val surgeMultiplier: Double,
    val surgePowerW: Double,
    val quantity: Int,
    val dutyCyclePercent: Double,
    val utilizationFactorKu: Double,
    val demandFactor: Double,
    val coincidenceFactor: Double,
    val diversityFactor: Double,
    val continuousLoad: Boolean,
    val continuousHours: Double,
    val criticality: String,
    val deferrableLoad: Boolean,
    val shiftableToDaytime: Boolean,
    val smartControlled: Boolean,
    val autoStart: Boolean,
    val cyclingLoad: Boolean,
    val standbyLoad: Boolean,
    val phantomLoadW: Double,
    val dayHoursSummer: Double,
    val nightHoursSummer: Double,
    val dayHoursWinter: Double,
    val nightHoursWinter: Double,
    val weekdayHours: Double,
    val weekendHours: Double,
    val operatingDaysPerWeek: Int,
    val operatingDaysPerYear: Int,
    val operatingMode: String,
    val timeProfileType: String,
    val peakStartTime: String,
    val peakEndTime: String,
    val hourlyProfile: List<Int>,
    val simultaneousGroup: String,
    val maxSimultaneousUnits: Int,
    val dataSource: String,
    val measurementMethod: String,
    val measurementDate: String,
    val confidenceLevel: String,
    val notes: String
)

class HourlyProfileConverter {
    @TypeConverter
    fun fromString(value: String): List<Int> {
        if (value.isEmpty()) return emptyList()
        return value.split(",").map { it.toIntOrNull() ?: 0 }
    }

    @TypeConverter
    fun toString(list: List<Int>): String {
        return list.joinToString(",")
    }
}
