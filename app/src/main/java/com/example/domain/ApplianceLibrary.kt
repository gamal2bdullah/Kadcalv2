package com.example.domain

import com.example.data.LoadEntity
import java.util.UUID

object ApplianceLibrary {

    data class Template(
        val name: String,
        val arabicName: String,
        val categoryMain: String,
        val categorySub: String,
        val ratedPowerW: Double,
        val runningPowerW: Double,
        val powerFactor: Double,
        val efficiency: Double,
        val electricalType: String = "AC",
        val voltageNominal: Int = 220,
        val phaseType: String = "1Ø",
        val frequency: String = "50Hz",
        val thdPercent: Double = 10.0,
        val harmonicClass: String = "Nonlinear",
        val surgeMultiplier: Double = 1.0,
        val dutyCyclePercent: Double = 60.0,
        val utilizationFactorKu: Double = 0.8,
        val demandFactor: Double = 0.9,
        val isMotor: Boolean = false,
        val hasStandby: Boolean = false,
        val phantomLoadW: Double = 0.0,
        val typicalDayHours: Double = 4.0,
        val typicalNightHours: Double = 4.0,
        val operatingMode: String = "Scheduled",
        val timeProfileType: String = "Evening Peak",
        val continuousLoad: Boolean = false,
        val criticality: String = "Normal",
        val isCyclic: Boolean = false,
        val hourlyProfile: List<Int> = List(24) { 0 }
    )

    private fun hp(hours: List<Int>): List<Int> {
        val result = MutableList(24) { 0 }
        hours.forEach { h -> if (h in 0..23) result[h] = 1 }
        return result
    }

    private val h247 = List(24) { 1 }

    val APPLIANCE_LIBRARY = listOf(
        // LIGHTING
        Template(
            name = "LED Bulb 9W", arabicName = "لمبة LED 9 واط",
            categoryMain = "Lighting", categorySub = "LED Bulb",
            ratedPowerW = 9.0, runningPowerW = 9.0, powerFactor = 0.9, efficiency = 90.0,
            thdPercent = 15.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 60.0, utilizationFactorKu = 0.8, demandFactor = 0.9,
            typicalDayHours = 4.0, typicalNightHours = 4.0,
            timeProfileType = "Evening Peak", criticality = "Normal",
            hourlyProfile = hp(listOf(18, 19, 20, 21, 22, 23))
        ),
        Template(
            name = "LED Bulb 12W", arabicName = "لمبة LED 12 واط",
            categoryMain = "Lighting", categorySub = "LED Bulb",
            ratedPowerW = 12.0, runningPowerW = 12.0, powerFactor = 0.9, efficiency = 90.0,
            thdPercent = 15.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 60.0, utilizationFactorKu = 0.8, demandFactor = 0.9,
            typicalDayHours = 4.0, typicalNightHours = 4.0,
            timeProfileType = "Evening Peak", criticality = "Normal",
            hourlyProfile = hp(listOf(18, 19, 20, 21, 22, 23))
        ),
        Template(
            name = "LED Tube Light 18W", arabicName = "أنبوب LED 18 واط",
            categoryMain = "Lighting", categorySub = "LED Tube",
            ratedPowerW = 18.0, runningPowerW = 18.0, powerFactor = 0.92, efficiency = 92.0,
            thdPercent = 12.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 70.0, utilizationFactorKu = 0.85, demandFactor = 0.9,
            typicalDayHours = 6.0, typicalNightHours = 4.0,
            timeProfileType = "Evening Peak", criticality = "Essential",
            hourlyProfile = hp(listOf(6, 7, 8, 18, 19, 20, 21, 22, 23))
        ),
        Template(
            name = "LED Panel 40W", arabicName = "لوحة LED 40 واط",
            categoryMain = "Lighting", categorySub = "LED Panel",
            ratedPowerW = 40.0, runningPowerW = 40.0, powerFactor = 0.95, efficiency = 95.0,
            thdPercent = 10.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 60.0, utilizationFactorKu = 0.85, demandFactor = 0.9,
            typicalDayHours = 6.0, typicalNightHours = 4.0,
            timeProfileType = "Evening Peak", criticality = "Normal",
            hourlyProfile = hp(listOf(7, 8, 9, 10, 18, 19, 20, 21, 22, 23))
        ),
        Template(
            name = "LED Floodlight 100W", arabicName = "كشاف LED 100 واط",
            categoryMain = "Lighting", categorySub = "Floodlight",
            ratedPowerW = 100.0, runningPowerW = 100.0, powerFactor = 0.95, efficiency = 92.0,
            thdPercent = 12.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 40.0, utilizationFactorKu = 0.7, demandFactor = 0.9,
            typicalDayHours = 0.0, typicalNightHours = 10.0, operatingMode = "Sensor-Based",
            timeProfileType = "Night Load", criticality = "Normal",
            hourlyProfile = hp(listOf(18, 19, 20, 21, 22, 23, 0, 1, 2, 3, 4, 5))
        ),
        Template(
            name = "LED Streetlight 150W", arabicName = "إنارة شوارع LED 150 واط",
            categoryMain = "Lighting", categorySub = "Streetlight",
            ratedPowerW = 150.0, runningPowerW = 150.0, powerFactor = 0.95, efficiency = 93.0,
            thdPercent = 12.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 50.0, utilizationFactorKu = 0.8, demandFactor = 1.0,
            typicalDayHours = 0.0, typicalNightHours = 12.0,
            timeProfileType = "Night Load", criticality = "Essential",
            hourlyProfile = hp(listOf(18, 19, 20, 21, 22, 23, 0, 1, 2, 3, 4, 5))
        ),
        // HVAC
        Template(
            name = "Split AC 1 Ton (Inverter)", arabicName = "مكيف سبليت 1 طن انفرتر",
            categoryMain = "HVAC", categorySub = "Split Inverter",
            ratedPowerW = 1100.0, runningPowerW = 800.0, powerFactor = 0.92, efficiency = 88.0,
            thdPercent = 8.0, harmonicClass = "Nonlinear", surgeMultiplier = 3.0,
            dutyCyclePercent = 60.0, utilizationFactorKu = 0.7, demandFactor = 0.8,
            isMotor = true, hasStandby = true, phantomLoadW = 5.0,
            typicalDayHours = 8.0, typicalNightHours = 4.0, operatingMode = "Continuous",
            timeProfileType = "Noon Peak", continuousLoad = true, criticality = "Essential", isCyclic = true,
            hourlyProfile = hp(listOf(9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 0, 1, 2, 3, 4))
        ),
        Template(
            name = "Split AC 1.5 Ton (Inverter)", arabicName = "مكيف سبليت 1.5 طن انفرتر",
            categoryMain = "HVAC", categorySub = "Split Inverter",
            ratedPowerW = 1500.0, runningPowerW = 1100.0, powerFactor = 0.92, efficiency = 88.0,
            thdPercent = 8.0, harmonicClass = "Nonlinear", surgeMultiplier = 3.0,
            dutyCyclePercent = 60.0, utilizationFactorKu = 0.7, demandFactor = 0.8,
            isMotor = true, hasStandby = true, phantomLoadW = 5.0,
            typicalDayHours = 8.0, typicalNightHours = 4.0, operatingMode = "Continuous",
            timeProfileType = "Noon Peak", continuousLoad = true, criticality = "Essential", isCyclic = true,
            hourlyProfile = hp(listOf(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 0, 1, 2, 3, 4))
        ),
        Template(
            name = "Split AC 2 Ton (Inverter)", arabicName = "مكيف سبليت 2 طن انفرتر",
            categoryMain = "HVAC", categorySub = "Split Inverter",
            ratedPowerW = 2000.0, runningPowerW = 1500.0, powerFactor = 0.92, efficiency = 87.0,
            thdPercent = 8.0, harmonicClass = "Nonlinear", surgeMultiplier = 3.0,
            dutyCyclePercent = 65.0, utilizationFactorKu = 0.75, demandFactor = 0.8,
            isMotor = true, hasStandby = true, phantomLoadW = 6.0,
            typicalDayHours = 9.0, typicalNightHours = 4.0, operatingMode = "Continuous",
            timeProfileType = "Noon Peak", continuousLoad = true, criticality = "Essential", isCyclic = true,
            hourlyProfile = hp(listOf(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 0, 1, 2, 3, 4))
        ),
        Template(
            name = "Window AC 1.5 Ton", arabicName = "مكيف شباك 1.5 طن",
            categoryMain = "HVAC", categorySub = "Window AC",
            ratedPowerW = 1800.0, runningPowerW = 1500.0, powerFactor = 0.85, efficiency = 80.0,
            thdPercent = 10.0, harmonicClass = "Nonlinear", surgeMultiplier = 5.0,
            dutyCyclePercent = 70.0, utilizationFactorKu = 0.8, demandFactor = 0.9,
            isMotor = true, typicalDayHours = 8.0, typicalNightHours = 4.0, operatingMode = "Continuous",
            timeProfileType = "Noon Peak", continuousLoad = true, criticality = "Essential", isCyclic = true,
            hourlyProfile = hp(listOf(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 0, 1, 2, 3, 4))
        ),
        Template(
            name = "Central AC 3 Ton", arabicName = "تكييف مركزي 3 طن",
            categoryMain = "HVAC", categorySub = "Central AC",
            ratedPowerW = 4500.0, runningPowerW = 3500.0, powerFactor = 0.88, efficiency = 85.0,
            phaseType = "3Ø", voltageNominal = 380, thdPercent = 9.0, harmonicClass = "Nonlinear",
            surgeMultiplier = 5.0, dutyCyclePercent = 60.0, utilizationFactorKu = 0.7, demandFactor = 0.8,
            isMotor = true, typicalDayHours = 9.0, typicalNightHours = 3.0, operatingMode = "Continuous",
            timeProfileType = "Noon Peak", continuousLoad = true, criticality = "Essential", isCyclic = true,
            hourlyProfile = hp(listOf(9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 0, 1, 2, 3, 4))
        ),
        Template(
            name = "Ceiling Fan", arabicName = "مروحة سقف",
            categoryMain = "HVAC", categorySub = "Ceiling Fan",
            ratedPowerW = 75.0, runningPowerW = 60.0, powerFactor = 0.85, efficiency = 80.0,
            thdPercent = 5.0, harmonicClass = "Linear", surgeMultiplier = 1.5,
            dutyCyclePercent = 50.0, utilizationFactorKu = 0.7, demandFactor = 0.85,
            isMotor = true, typicalDayHours = 10.0, typicalNightHours = 6.0, operatingMode = "Continuous",
            timeProfileType = "Evening Peak", continuousLoad = true, criticality = "Normal",
            hourlyProfile = hp(listOf(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 0, 1, 2, 3))
        ),
        Template(
            name = "Exhaust Fan", arabicName = "مروحة شفط",
            categoryMain = "HVAC", categorySub = "Exhaust Fan",
            ratedPowerW = 50.0, runningPowerW = 45.0, powerFactor = 0.8, efficiency = 78.0,
            thdPercent = 5.0, harmonicClass = "Linear", surgeMultiplier = 2.0,
            dutyCyclePercent = 30.0, utilizationFactorKu = 0.6, demandFactor = 0.7,
            isMotor = true, typicalDayHours = 4.0, typicalNightHours = 1.0, operatingMode = "Intermittent",
            timeProfileType = "Day Load", criticality = "Normal",
            hourlyProfile = hp(listOf(8, 9, 10, 11, 12, 18, 19, 20, 21))
        ),
        Template(
            name = "Electric Heater 2000W", arabicName = "مدفأة كهربائية 2000 واط",
            categoryMain = "HVAC", categorySub = "Heater",
            ratedPowerW = 2000.0, runningPowerW = 2000.0, powerFactor = 1.0, efficiency = 100.0,
            thdPercent = 2.0, harmonicClass = "Linear", surgeMultiplier = 1.0,
            dutyCyclePercent = 40.0, utilizationFactorKu = 0.7, demandFactor = 0.9,
            typicalDayHours = 4.0, typicalNightHours = 6.0, operatingMode = "Seasonal",
            timeProfileType = "Evening Peak", continuousLoad = true, criticality = "Normal", isCyclic = true,
            hourlyProfile = hp(listOf(18, 19, 20, 21, 22, 23, 0, 1, 2, 3, 4, 5))
        ),
        // KITCHEN
        Template(
            name = "Refrigerator (Standard)", arabicName = "ثلاجة قياسية",
            categoryMain = "Kitchen", categorySub = "Refrigerator",
            ratedPowerW = 350.0, runningPowerW = 150.0, powerFactor = 0.75, efficiency = 85.0,
            thdPercent = 8.0, harmonicClass = "Nonlinear", surgeMultiplier = 5.0,
            dutyCyclePercent = 50.0, utilizationFactorKu = 0.8, demandFactor = 1.0,
            isMotor = true, hasStandby = true, phantomLoadW = 8.0,
            typicalDayHours = 12.0, typicalNightHours = 12.0, operatingMode = "Continuous",
            timeProfileType = "24/7", continuousLoad = true, criticality = "Critical", isCyclic = true,
            hourlyProfile = h247
        ),
        Template(
            name = "Refrigerator (Inverter)", arabicName = "ثلاجة انفرتر",
            categoryMain = "Kitchen", categorySub = "Refrigerator Inverter",
            ratedPowerW = 200.0, runningPowerW = 90.0, powerFactor = 0.85, efficiency = 92.0,
            thdPercent = 6.0, harmonicClass = "Nonlinear", surgeMultiplier = 3.0,
            dutyCyclePercent = 40.0, utilizationFactorKu = 0.75, demandFactor = 1.0,
            isMotor = true, hasStandby = true, phantomLoadW = 4.0,
            typicalDayHours = 12.0, typicalNightHours = 12.0, operatingMode = "Continuous",
            timeProfileType = "24/7", continuousLoad = true, criticality = "Critical", isCyclic = true,
            hourlyProfile = h247
        ),
        Template(
            name = "Freezer (Deep)", arabicName = "فريزر عميق",
            categoryMain = "Kitchen", categorySub = "Freezer",
            ratedPowerW = 400.0, runningPowerW = 200.0, powerFactor = 0.78, efficiency = 82.0,
            thdPercent = 8.0, harmonicClass = "Nonlinear", surgeMultiplier = 5.0,
            dutyCyclePercent = 55.0, utilizationFactorKu = 0.85, demandFactor = 1.0,
            isMotor = true, hasStandby = true, phantomLoadW = 6.0,
            typicalDayHours = 12.0, typicalNightHours = 12.0, operatingMode = "Continuous",
            timeProfileType = "24/7", continuousLoad = true, criticality = "Critical", isCyclic = true,
            hourlyProfile = h247
        ),
        Template(
            name = "Microwave Oven", arabicName = "فرن ميكروويف",
            categoryMain = "Kitchen", categorySub = "Microwave",
            ratedPowerW = 1200.0, runningPowerW = 1200.0, powerFactor = 0.95, efficiency = 70.0,
            thdPercent = 10.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 15.0, utilizationFactorKu = 0.3, demandFactor = 0.5,
            hasStandby = true, phantomLoadW = 3.0,
            typicalDayHours = 1.0, typicalNightHours = 0.0, operatingMode = "Intermittent",
            timeProfileType = "Morning Peak", criticality = "Normal",
            hourlyProfile = hp(listOf(7, 8, 12, 13, 19, 20))
        ),
        Template(
            name = "Electric Oven", arabicName = "فرن كهربائي",
            categoryMain = "Kitchen", categorySub = "Oven",
            ratedPowerW = 2500.0, runningPowerW = 2500.0, powerFactor = 1.0, efficiency = 85.0,
            thdPercent = 3.0, harmonicClass = "Linear", surgeMultiplier = 1.0,
            dutyCyclePercent = 10.0, utilizationFactorKu = 0.4, demandFactor = 0.6,
            typicalDayHours = 1.0, typicalNightHours = 0.0, operatingMode = "Intermittent",
            timeProfileType = "Evening Peak", criticality = "Normal",
            hourlyProfile = hp(listOf(7, 8, 12, 13, 18, 19))
        ),
        Template(
            name = "Electric Kettle", arabicName = "غلاية كهربائية",
            categoryMain = "Kitchen", categorySub = "Kettle",
            ratedPowerW = 2200.0, runningPowerW = 2200.0, powerFactor = 1.0, efficiency = 95.0,
            thdPercent = 3.0, harmonicClass = "Linear", surgeMultiplier = 1.0,
            dutyCyclePercent = 8.0, utilizationFactorKu = 0.3, demandFactor = 0.5,
            typicalDayHours = 1.0, typicalNightHours = 0.0, operatingMode = "Intermittent",
            timeProfileType = "Morning Peak", criticality = "Normal",
            hourlyProfile = hp(listOf(6, 7, 8, 12, 13, 19, 20))
        ),
        Template(
            name = "Toaster", arabicName = "محمصة خبز",
            categoryMain = "Kitchen", categorySub = "Toaster",
            ratedPowerW = 1000.0, runningPowerW = 1000.0, powerFactor = 1.0, efficiency = 90.0,
            thdPercent = 3.0, harmonicClass = "Linear", surgeMultiplier = 1.0,
            dutyCyclePercent = 5.0, utilizationFactorKu = 0.2, demandFactor = 0.4,
            typicalDayHours = 0.5, typicalNightHours = 0.0, operatingMode = "Intermittent",
            timeProfileType = "Morning Peak", criticality = "Optional",
            hourlyProfile = hp(listOf(7, 8))
        ),
        Template(
            name = "Coffee Maker", arabicName = "صانعة القهوة",
            categoryMain = "Kitchen", categorySub = "Coffee Maker",
            ratedPowerW = 900.0, runningPowerW = 900.0, powerFactor = 0.95, efficiency = 85.0,
            thdPercent = 8.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 8.0, utilizationFactorKu = 0.3, demandFactor = 0.5,
            hasStandby = true, phantomLoadW = 2.0,
            typicalDayHours = 1.0, typicalNightHours = 0.0, operatingMode = "Intermittent",
            timeProfileType = "Morning Peak", criticality = "Optional",
            hourlyProfile = hp(listOf(6, 7, 8))
        ),
        Template(
            name = "Dishwasher", arabicName = "غسالة صحون",
            categoryMain = "Kitchen", categorySub = "Dishwasher",
            ratedPowerW = 1800.0, runningPowerW = 1300.0, powerFactor = 0.9, efficiency = 80.0,
            thdPercent = 8.0, harmonicClass = "Nonlinear", surgeMultiplier = 2.0,
            dutyCyclePercent = 10.0, utilizationFactorKu = 0.4, demandFactor = 0.6,
            isMotor = true, hasStandby = true, phantomLoadW = 2.0,
            typicalDayHours = 1.0, typicalNightHours = 0.0, operatingMode = "Scheduled",
            timeProfileType = "Evening Peak", criticality = "Normal",
            hourlyProfile = hp(listOf(21, 22))
        ),
        Template(
            name = "Blender / Mixer", arabicName = "خلاط",
            categoryMain = "Kitchen", categorySub = "Blender",
            ratedPowerW = 500.0, runningPowerW = 500.0, powerFactor = 0.9, efficiency = 80.0,
            thdPercent = 10.0, harmonicClass = "Nonlinear", surgeMultiplier = 2.0,
            dutyCyclePercent = 3.0, utilizationFactorKu = 0.1, demandFactor = 0.3,
            isMotor = true, typicalDayHours = 0.3, typicalNightHours = 0.0, operatingMode = "Intermittent",
            timeProfileType = "Day Load", criticality = "Optional",
            hourlyProfile = hp(listOf(7, 8, 12, 13))
        ),
        Template(
            name = "Rice Cooker", arabicName = "قدر كهربائي للأرز",
            categoryMain = "Kitchen", categorySub = "Rice Cooker",
            ratedPowerW = 800.0, runningPowerW = 700.0, powerFactor = 0.95, efficiency = 85.0,
            thdPercent = 6.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 10.0, utilizationFactorKu = 0.4, demandFactor = 0.6,
            hasStandby = true, phantomLoadW = 2.0,
            typicalDayHours = 1.0, typicalNightHours = 0.0, operatingMode = "Intermittent",
            timeProfileType = "Evening Peak", criticality = "Normal",
            hourlyProfile = hp(listOf(7, 8, 12, 13, 18, 19))
        ),
        // PUMP
        Template(
            name = "Water Pump 0.5 HP", arabicName = "مضخة مياه 0.5 حصان",
            categoryMain = "Pump", categorySub = "Surface Pump",
            ratedPowerW = 370.0, runningPowerW = 350.0, powerFactor = 0.8, efficiency = 75.0,
            thdPercent = 6.0, harmonicClass = "Linear", surgeMultiplier = 5.0,
            dutyCyclePercent = 20.0, utilizationFactorKu = 0.5, demandFactor = 0.7,
            isMotor = true, typicalDayHours = 1.5, typicalNightHours = 0.5, operatingMode = "Intermittent",
            timeProfileType = "Morning Peak", criticality = "Essential",
            hourlyProfile = hp(listOf(6, 7, 8, 19, 20))
        ),
        Template(
            name = "Water Pump 1 HP", arabicName = "مضخة مياه 1 حصان",
            categoryMain = "Pump", categorySub = "Surface Pump",
            ratedPowerW = 750.0, runningPowerW = 700.0, powerFactor = 0.82, efficiency = 78.0,
            thdPercent = 6.0, harmonicClass = "Linear", surgeMultiplier = 5.0,
            dutyCyclePercent = 20.0, utilizationFactorKu = 0.5, demandFactor = 0.7,
            isMotor = true, typicalDayHours = 1.5, typicalNightHours = 0.5, operatingMode = "Intermittent",
            timeProfileType = "Morning Peak", criticality = "Essential",
            hourlyProfile = hp(listOf(6, 7, 8, 19, 20))
        ),
        Template(
            name = "Submersible Pump 1.5 HP", arabicName = "مضخة غاطسة 1.5 حصان",
            categoryMain = "Pump", categorySub = "Submersible",
            ratedPowerW = 1100.0, runningPowerW = 1000.0, powerFactor = 0.82, efficiency = 78.0,
            thdPercent = 6.0, harmonicClass = "Linear", surgeMultiplier = 7.0,
            dutyCyclePercent = 25.0, utilizationFactorKu = 0.5, demandFactor = 0.7,
            isMotor = true, typicalDayHours = 2.0, typicalNightHours = 0.5, operatingMode = "Intermittent",
            timeProfileType = "Morning Peak", criticality = "Essential",
            hourlyProfile = hp(listOf(6, 7, 8, 19, 20))
        ),
        Template(
            name = "Submersible Pump 3 HP (3Ø)", arabicName = "مضخة غاطسة 3 حصان 3 فاز",
            categoryMain = "Pump", categorySub = "Submersible 3Ø",
            ratedPowerW = 2200.0, runningPowerW = 2000.0, powerFactor = 0.85, efficiency = 82.0,
            phaseType = "3Ø", voltageNominal = 380, thdPercent = 6.0, harmonicClass = "Linear",
            surgeMultiplier = 7.0, dutyCyclePercent = 25.0, utilizationFactorKu = 0.5, demandFactor = 0.7,
            isMotor = true, typicalDayHours = 2.0, typicalNightHours = 0.5, operatingMode = "Intermittent",
            timeProfileType = "Morning Peak", criticality = "Essential",
            hourlyProfile = hp(listOf(6, 7, 8, 19, 20))
        ),
        Template(
            name = "Booster Pump", arabicName = "مضخة معززة",
            categoryMain = "Pump", categorySub = "Booster",
            ratedPowerW = 400.0, runningPowerW = 380.0, powerFactor = 0.82, efficiency = 78.0,
            thdPercent = 6.0, harmonicClass = "Linear", surgeMultiplier = 5.0,
            dutyCyclePercent = 15.0, utilizationFactorKu = 0.4, demandFactor = 0.6,
            isMotor = true, typicalDayHours = 1.0, typicalNightHours = 0.5, operatingMode = "Sensor-Based",
            timeProfileType = "Day Load", criticality = "Essential",
            hourlyProfile = hp(listOf(6, 7, 8, 12, 13, 18, 19, 20))
        ),
        // MEDICAL
        Template(
            name = "CPAP Machine", arabicName = "جهاز CPAP",
            categoryMain = "Medical", categorySub = "CPAP",
            ratedPowerW = 80.0, runningPowerW = 70.0, powerFactor = 0.9, efficiency = 88.0,
            thdPercent = 6.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 33.0, utilizationFactorKu = 0.8, demandFactor = 1.0,
            isMotor = true, hasStandby = true, phantomLoadW = 2.0,
            typicalDayHours = 0.0, typicalNightHours = 8.0, operatingMode = "Continuous",
            timeProfileType = "Night Load", continuousLoad = true, criticality = "Critical", isCyclic = true,
            hourlyProfile = hp(listOf(22, 23, 0, 1, 2, 3, 4, 5))
        ),
        Template(
            name = "Oxygen Concentrator", arabicName = "مكثف أكسجين",
            categoryMain = "Medical", categorySub = "Oxygen",
            ratedPowerW = 350.0, runningPowerW = 320.0, powerFactor = 0.9, efficiency = 85.0,
            thdPercent = 8.0, harmonicClass = "Nonlinear", surgeMultiplier = 2.0,
            dutyCyclePercent = 80.0, utilizationFactorKu = 0.9, demandFactor = 1.0,
            isMotor = true, typicalDayHours = 12.0, typicalNightHours = 12.0, operatingMode = "Continuous",
            timeProfileType = "24/7", continuousLoad = true, criticality = "Critical",
            hourlyProfile = h247
        ),
        Template(
            name = "Nebulizer", arabicName = "جهاز رذاذ",
            categoryMain = "Medical", categorySub = "Nebulizer",
            ratedPowerW = 80.0, runningPowerW = 75.0, powerFactor = 0.9, efficiency = 85.0,
            thdPercent = 5.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 10.0, utilizationFactorKu = 0.3, demandFactor = 0.5,
            isMotor = true, typicalDayHours = 0.5, typicalNightHours = 0.5, operatingMode = "Intermittent",
            timeProfileType = "Day Load", criticality = "Critical",
            hourlyProfile = hp(listOf(7, 8, 12, 13, 19, 20))
        ),
        Template(
            name = "Dialysis Machine (Home)", arabicName = "جهاز غسيل كلى منزلي",
            categoryMain = "Medical", categorySub = "Dialysis",
            ratedPowerW = 1500.0, runningPowerW = 1300.0, powerFactor = 0.9, efficiency = 85.0,
            thdPercent = 8.0, harmonicClass = "Nonlinear", surgeMultiplier = 2.0,
            dutyCyclePercent = 17.0, utilizationFactorKu = 0.5, demandFactor = 0.8,
            isMotor = true, typicalDayHours = 4.0, typicalNightHours = 0.0, operatingMode = "Scheduled",
            timeProfileType = "Day Load", continuousLoad = true, criticality = "Critical",
            hourlyProfile = hp(listOf(8, 9, 10, 11, 12, 13))
        ),
        // IT
        Template(
            name = "Desktop Computer", arabicName = "كمبيوتر مكتبي",
            categoryMain = "IT", categorySub = "Desktop",
            ratedPowerW = 300.0, runningPowerW = 180.0, powerFactor = 0.95, efficiency = 85.0,
            thdPercent = 12.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.2,
            dutyCyclePercent = 50.0, utilizationFactorKu = 0.7, demandFactor = 0.85,
            hasStandby = true, phantomLoadW = 3.0,
            typicalDayHours = 8.0, typicalNightHours = 0.0, operatingMode = "Scheduled",
            timeProfileType = "Day Load", continuousLoad = true, criticality = "Essential",
            hourlyProfile = hp(listOf(8, 9, 10, 11, 12, 13, 14, 15, 16, 17))
        ),
        Template(
            name = "Laptop", arabicName = "لابتوب",
            categoryMain = "IT", categorySub = "Laptop",
            ratedPowerW = 65.0, runningPowerW = 45.0, powerFactor = 0.95, efficiency = 90.0,
            thdPercent = 8.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 50.0, utilizationFactorKu = 0.7, demandFactor = 0.85,
            hasStandby = true, phantomLoadW = 1.0,
            typicalDayHours = 8.0, typicalNightHours = 1.0, operatingMode = "Scheduled",
            timeProfileType = "Day Load", continuousLoad = true, criticality = "Normal",
            hourlyProfile = hp(listOf(8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 22, 23))
        ),
        Template(
            name = "Monitor 24\"", arabicName = "شاشة 24 بوصة",
            categoryMain = "IT", categorySub = "Monitor",
            ratedPowerW = 40.0, runningPowerW = 30.0, powerFactor = 0.95, efficiency = 88.0,
            thdPercent = 10.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 50.0, utilizationFactorKu = 0.7, demandFactor = 0.85,
            hasStandby = true, phantomLoadW = 1.0,
            typicalDayHours = 8.0, typicalNightHours = 0.0, operatingMode = "Scheduled",
            timeProfileType = "Day Load", continuousLoad = true, criticality = "Normal",
            hourlyProfile = hp(listOf(8, 9, 10, 11, 12, 13, 14, 15, 16, 17))
        ),
        Template(
            name = "TV LED 55\"", arabicName = "تلفاز LED 55 بوصة",
            categoryMain = "IT", categorySub = "TV",
            ratedPowerW = 120.0, runningPowerW = 95.0, powerFactor = 0.95, efficiency = 85.0,
            thdPercent = 10.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 30.0, utilizationFactorKu = 0.6, demandFactor = 0.7,
            hasStandby = true, phantomLoadW = 3.0,
            typicalDayHours = 2.0, typicalNightHours = 4.0, operatingMode = "Scheduled",
            timeProfileType = "Evening Peak", criticality = "Optional",
            hourlyProfile = hp(listOf(19, 20, 21, 22, 23))
        ),
        Template(
            name = "Router / Modem", arabicName = "راوتر",
            categoryMain = "IT", categorySub = "Router",
            ratedPowerW = 15.0, runningPowerW = 10.0, powerFactor = 0.9, efficiency = 80.0,
            thdPercent = 8.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 100.0, utilizationFactorKu = 1.0, demandFactor = 1.0,
            typicalDayHours = 12.0, typicalNightHours = 12.0, operatingMode = "Continuous",
            timeProfileType = "24/7", continuousLoad = true, criticality = "Essential",
            hourlyProfile = h247
        ),
        Template(
            name = "Server (Small)", arabicName = "سيرفر صغير",
            categoryMain = "IT", categorySub = "Server",
            ratedPowerW = 600.0, runningPowerW = 450.0, powerFactor = 0.95, efficiency = 85.0,
            thdPercent = 15.0, harmonicClass = "High Harmonics", surgeMultiplier = 1.5,
            dutyCyclePercent = 80.0, utilizationFactorKu = 0.85, demandFactor = 1.0,
            typicalDayHours = 12.0, typicalNightHours = 12.0, operatingMode = "Continuous",
            timeProfileType = "24/7", continuousLoad = true, criticality = "Critical",
            hourlyProfile = h247
        ),
        // SECURITY
        Template(
            name = "CCTV System (4 cam)", arabicName = "نظام كاميرات (4 كاميرا)",
            categoryMain = "Security", categorySub = "CCTV",
            ratedPowerW = 60.0, runningPowerW = 50.0, powerFactor = 0.9, efficiency = 85.0,
            thdPercent = 8.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 100.0, utilizationFactorKu = 1.0, demandFactor = 1.0,
            typicalDayHours = 12.0, typicalNightHours = 12.0, operatingMode = "Continuous",
            timeProfileType = "24/7", continuousLoad = true, criticality = "Critical",
            hourlyProfile = h247
        ),
        Template(
            name = "Alarm System", arabicName = "نظام إنذار",
            categoryMain = "Security", categorySub = "Alarm",
            ratedPowerW = 20.0, runningPowerW = 15.0, powerFactor = 0.85, efficiency = 80.0,
            thdPercent = 6.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 100.0, utilizationFactorKu = 1.0, demandFactor = 1.0,
            typicalDayHours = 12.0, typicalNightHours = 12.0, operatingMode = "Continuous",
            timeProfileType = "24/7", continuousLoad = true, criticality = "Critical",
            hourlyProfile = h247
        ),
        Template(
            name = "Access Control", arabicName = "باب إلكتروني",
            categoryMain = "Security", categorySub = "Access Control",
            ratedPowerW = 25.0, runningPowerW = 20.0, powerFactor = 0.85, efficiency = 80.0,
            thdPercent = 6.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 100.0, utilizationFactorKu = 1.0, demandFactor = 1.0,
            typicalDayHours = 12.0, typicalNightHours = 12.0, operatingMode = "Continuous",
            timeProfileType = "24/7", continuousLoad = true, criticality = "Essential",
            hourlyProfile = h247
        ),
        // WATER
        Template(
            name = "Water Heater 30L", arabicName = "سخان مياه 30 لتر",
            categoryMain = "Water", categorySub = "Water Heater",
            ratedPowerW = 1500.0, runningPowerW = 1500.0, powerFactor = 1.0, efficiency = 90.0,
            thdPercent = 2.0, harmonicClass = "Linear", surgeMultiplier = 1.0,
            dutyCyclePercent = 25.0, utilizationFactorKu = 0.5, demandFactor = 0.7,
            typicalDayHours = 2.0, typicalNightHours = 0.0, operatingMode = "Scheduled",
            timeProfileType = "Morning Peak", criticality = "Essential", isCyclic = true,
            hourlyProfile = hp(listOf(6, 7, 8, 19, 20))
        ),
        Template(
            name = "Water Heater 50L", arabicName = "سخان مياه 50 لتر",
            categoryMain = "Water", categorySub = "Water Heater",
            ratedPowerW = 2000.0, runningPowerW = 2000.0, powerFactor = 1.0, efficiency = 90.0,
            thdPercent = 2.0, harmonicClass = "Linear", surgeMultiplier = 1.0,
            dutyCyclePercent = 25.0, utilizationFactorKu = 0.5, demandFactor = 0.7,
            typicalDayHours = 2.0, typicalNightHours = 0.0, operatingMode = "Scheduled",
            timeProfileType = "Morning Peak", criticality = "Essential", isCyclic = true,
            hourlyProfile = hp(listOf(6, 7, 8, 19, 20))
        ),
        Template(
            name = "Solar Water Heater Booster", arabicName = "سخان شمسي مساند",
            categoryMain = "Water", categorySub = "Booster Heater",
            ratedPowerW = 1000.0, runningPowerW = 1000.0, powerFactor = 1.0, efficiency = 90.0,
            thdPercent = 2.0, harmonicClass = "Linear", surgeMultiplier = 1.0,
            dutyCyclePercent = 15.0, utilizationFactorKu = 0.3, demandFactor = 0.5,
            typicalDayHours = 1.0, typicalNightHours = 0.0, operatingMode = "Sensor-Based",
            timeProfileType = "Morning Peak", criticality = "Normal", isCyclic = true,
            hourlyProfile = hp(listOf(6, 7, 18, 19))
        ),
        // OFFICE
        Template(
            name = "Photocopier", arabicName = "آلة تصوير",
            categoryMain = "Office", categorySub = "Copier",
            ratedPowerW = 1500.0, runningPowerW = 800.0, powerFactor = 0.95, efficiency = 80.0,
            thdPercent = 12.0, harmonicClass = "Nonlinear", surgeMultiplier = 2.0,
            dutyCyclePercent = 20.0, utilizationFactorKu = 0.5, demandFactor = 0.6,
            hasStandby = true, phantomLoadW = 8.0,
            typicalDayHours = 4.0, typicalNightHours = 0.0, operatingMode = "Scheduled",
            timeProfileType = "Day Load", criticality = "Normal",
            hourlyProfile = hp(listOf(9, 10, 11, 13, 14, 15, 16))
        ),
        Template(
            name = "Laser Printer", arabicName = "طابعة ليزر",
            categoryMain = "Office", categorySub = "Printer",
            ratedPowerW = 500.0, runningPowerW = 350.0, powerFactor = 0.9, efficiency = 80.0,
            thdPercent = 10.0, harmonicClass = "Nonlinear", surgeMultiplier = 2.0,
            dutyCyclePercent = 15.0, utilizationFactorKu = 0.5, demandFactor = 0.6,
            hasStandby = true, phantomLoadW = 3.0,
            typicalDayHours = 3.0, typicalNightHours = 0.0, operatingMode = "Intermittent",
            timeProfileType = "Day Load", criticality = "Normal",
            hourlyProfile = hp(listOf(9, 10, 11, 14, 15, 16))
        ),
        // LAUNDRY
        Template(
            name = "Washing Machine", arabicName = "غسالة ملابس",
            categoryMain = "Laundry", categorySub = "Washing Machine",
            ratedPowerW = 500.0, runningPowerW = 350.0, powerFactor = 0.85, efficiency = 80.0,
            thdPercent = 10.0, harmonicClass = "Nonlinear", surgeMultiplier = 3.0,
            dutyCyclePercent = 10.0, utilizationFactorKu = 0.4, demandFactor = 0.6,
            isMotor = true, hasStandby = true, phantomLoadW = 2.0,
            typicalDayHours = 1.0, typicalNightHours = 0.0, operatingMode = "Scheduled",
            timeProfileType = "Day Load", criticality = "Normal",
            hourlyProfile = hp(listOf(10, 11, 19, 20))
        ),
        Template(
            name = "Dryer", arabicName = "مجفف ملابس",
            categoryMain = "Laundry", categorySub = "Dryer",
            ratedPowerW = 3000.0, runningPowerW = 2800.0, powerFactor = 1.0, efficiency = 85.0,
            thdPercent = 5.0, harmonicClass = "Linear", surgeMultiplier = 2.0,
            dutyCyclePercent = 10.0, utilizationFactorKu = 0.4, demandFactor = 0.6,
            isMotor = true, typicalDayHours = 1.0, typicalNightHours = 0.0, operatingMode = "Scheduled",
            timeProfileType = "Day Load", criticality = "Normal",
            hourlyProfile = hp(listOf(11, 12, 20, 21))
        ),
        Template(
            name = "Iron", arabicName = "مكواة",
            categoryMain = "Laundry", categorySub = "Iron",
            ratedPowerW = 1500.0, runningPowerW = 1200.0, powerFactor = 1.0, efficiency = 90.0,
            thdPercent = 3.0, harmonicClass = "Linear", surgeMultiplier = 1.0,
            dutyCyclePercent = 30.0, utilizationFactorKu = 0.5, demandFactor = 0.6,
            typicalDayHours = 0.5, typicalNightHours = 0.0, operatingMode = "Intermittent",
            timeProfileType = "Evening Peak", criticality = "Optional",
            hourlyProfile = hp(listOf(19, 20))
        ),
        // EV
        Template(
            name = "EV Charger Level 1 (1.4kW)", arabicName = "شاحن EV بطيئ 1.4 كواط",
            categoryMain = "EV", categorySub = "EV Charger L1",
            ratedPowerW = 1400.0, runningPowerW = 1400.0, powerFactor = 0.98, efficiency = 92.0,
            thdPercent = 5.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 50.0, utilizationFactorKu = 0.6, demandFactor = 0.8,
            typicalDayHours = 0.0, typicalNightHours = 8.0,
            timeProfileType = "Night Load", continuousLoad = true, criticality = "Optional",
            hourlyProfile = hp(listOf(22, 23, 0, 1, 2, 3, 4, 5))
        ),
        Template(
            name = "EV Charger Level 2 (7kW)", arabicName = "شاحن EV متوسط 7 كواط",
            categoryMain = "EV", categorySub = "EV Charger L2",
            ratedPowerW = 7000.0, runningPowerW = 6800.0, powerFactor = 0.98, efficiency = 95.0,
            thdPercent = 5.0, harmonicClass = "Nonlinear", surgeMultiplier = 1.0,
            dutyCyclePercent = 60.0, utilizationFactorKu = 0.7, demandFactor = 0.85,
            typicalDayHours = 0.0, typicalNightHours = 6.0,
            timeProfileType = "Night Load", continuousLoad = true, criticality = "Optional",
            hourlyProfile = hp(listOf(23, 0, 1, 2, 3, 4))
        ),
        // INDUSTRIAL
        Template(
            name = "3Ø Motor 5 HP", arabicName = "موتور 3 فاز 5 حصان",
            categoryMain = "Industrial", categorySub = "Motor 3Ø",
            ratedPowerW = 3700.0, runningPowerW = 3500.0, powerFactor = 0.85, efficiency = 85.0,
            phaseType = "3Ø", voltageNominal = 380, thdPercent = 5.0, harmonicClass = "Linear",
            surgeMultiplier = 7.0, dutyCyclePercent = 50.0, utilizationFactorKu = 0.7, demandFactor = 0.8,
            isMotor = true, typicalDayHours = 6.0, typicalNightHours = 0.0, operatingMode = "Scheduled",
            timeProfileType = "Day Load", continuousLoad = true, criticality = "Critical", isCyclic = true,
            hourlyProfile = hp(listOf(8, 9, 10, 11, 12, 13, 14, 15, 16, 17))
        ),
        Template(
            name = "3Ø Motor 10 HP", arabicName = "موتور 3 فاز 10 حصان",
            categoryMain = "Industrial", categorySub = "Motor 3Ø",
            ratedPowerW = 7500.0, runningPowerW = 7000.0, powerFactor = 0.86, efficiency = 88.0,
            phaseType = "3Ø", voltageNominal = 380, thdPercent = 5.0, harmonicClass = "Linear",
            surgeMultiplier = 7.0, dutyCyclePercent = 50.0, utilizationFactorKu = 0.7, demandFactor = 0.8,
            isMotor = true, typicalDayHours = 6.0, typicalNightHours = 0.0, operatingMode = "Scheduled",
            timeProfileType = "Day Load", continuousLoad = true, criticality = "Critical", isCyclic = true,
            hourlyProfile = hp(listOf(8, 9, 10, 11, 12, 13, 14, 15, 16, 17))
        ),
        Template(
            name = "Welding Machine", arabicName = "ماكينة لحام",
            categoryMain = "Industrial", categorySub = "Welder",
            ratedPowerW = 4500.0, runningPowerW = 3500.0, powerFactor = 0.75, efficiency = 75.0,
            phaseType = "3Ø", voltageNominal = 380, thdPercent = 25.0, harmonicClass = "High Harmonics",
            surgeMultiplier = 3.0, dutyCyclePercent = 20.0, utilizationFactorKu = 0.5, demandFactor = 0.7,
            typicalDayHours = 3.0, typicalNightHours = 0.0, operatingMode = "Intermittent",
            timeProfileType = "Day Load", criticality = "Normal",
            hourlyProfile = hp(listOf(9, 10, 11, 14, 15, 16))
        ),
        Template(
            name = "Compressor 3Ø 7.5kW", arabicName = "ضاغط هواء 7.5kW",
            categoryMain = "Industrial", categorySub = "Compressor 3Ø",
            ratedPowerW = 7500.0, runningPowerW = 6000.0, powerFactor = 0.85, efficiency = 85.0,
            phaseType = "3Ø", voltageNominal = 380, thdPercent = 6.0, harmonicClass = "Linear",
            surgeMultiplier = 7.0, dutyCyclePercent = 40.0, utilizationFactorKu = 0.6, demandFactor = 0.8,
            isMotor = true, typicalDayHours = 6.0, typicalNightHours = 0.0, operatingMode = "Scheduled",
            timeProfileType = "Day Load", continuousLoad = true, criticality = "Critical", isCyclic = true,
            hourlyProfile = hp(listOf(8, 9, 10, 11, 12, 13, 14, 15, 16, 17))
        )
    )

    fun createLoadFromTemplate(t: Template, index: Int): LoadEntity {
        return LoadEntity(
            id = "id-" + UUID.randomUUID().toString().take(12),
            loadId = "LD-${String.format("%04d", index)}",
            loadTag = "${t.categorySub.uppercase().replace(" ", "-").take(15)}-01",
            loadName = t.name,
            arabicName = t.arabicName,
            categoryMain = t.categoryMain,
            categorySub = t.categorySub,
            spaceArea = "Living Room",
            buildingLevel = "Ground",
            distributionBoard = "DB-1",
            circuitReference = "C1",
            description = "Imported from library: ${t.name}",
            electricalType = t.electricalType,
            voltageNominal = t.voltageNominal,
            frequency = t.frequency,
            phaseType = t.phaseType,
            ratedPowerW = t.ratedPowerW,
            runningPowerW = t.runningPowerW,
            measuredPowerW = 0.0,
            powerFactor = t.powerFactor,
            efficiency = t.efficiency,
            thdPercent = t.thdPercent,
            harmonicClass = t.harmonicClass,
            lockedRotorCurrentA = 0.0,
            surgeMultiplier = t.surgeMultiplier,
            surgePowerW = t.ratedPowerW * t.surgeMultiplier,
            quantity = 1,
            dutyCyclePercent = t.dutyCyclePercent,
            utilizationFactorKu = t.utilizationFactorKu,
            demandFactor = t.demandFactor,
            coincidenceFactor = 0.7,
            diversityFactor = 1.2,
            continuousLoad = t.continuousLoad,
            continuousHours = 0.0,
            criticality = t.criticality,
            deferrableLoad = false,
            shiftableToDaytime = false,
            smartControlled = false,
            autoStart = false,
            cyclingLoad = t.isCyclic,
            standbyLoad = t.hasStandby,
            phantomLoadW = t.phantomLoadW,
            dayHoursSummer = t.typicalDayHours,
            nightHoursSummer = t.typicalNightHours,
            dayHoursWinter = t.typicalDayHours,
            nightHoursWinter = t.typicalNightHours,
            weekdayHours = 8.0,
            weekendHours = 10.0,
            operatingDaysPerWeek = 7,
            operatingDaysPerYear = 365,
            operatingMode = t.operatingMode,
            timeProfileType = t.timeProfileType,
            peakStartTime = "18:00",
            peakEndTime = "22:00",
            hourlyProfile = t.hourlyProfile,
            simultaneousGroup = "",
            maxSimultaneousUnits = 1,
            dataSource = "Manufacturer",
            measurementMethod = "Datasheet",
            measurementDate = "",
            confidenceLevel = "High",
            notes = "Traceable manufacturer datasheet template"
        )
    }

    fun makePresetLoads(presetType: String): List<LoadEntity> {
        val list = mutableListOf<LoadEntity>()
        var counter = 1001

        fun addTemplate(name: String, quantity: Int, overrides: (LoadEntity) -> LoadEntity = { it }) {
            val templ = APPLIANCE_LIBRARY.find { it.name == name } ?: return
            var loadVal = createLoadFromTemplate(templ, counter++)
            loadVal = loadVal.copy(quantity = quantity, loadId = "LD-${counter - 1}")
            list.add(overrides(loadVal))
        }

        when (presetType) {
            "basic" -> {
                addTemplate("LED Bulb 12W", 8) { it.copy(dayHoursSummer = 2.0, nightHoursSummer = 5.0, dayHoursWinter = 2.0, nightHoursWinter = 6.0) }
                addTemplate("Split AC 1.5 Ton (Inverter)", 2) { it.copy(dayHoursSummer = 9.0, nightHoursSummer = 4.0, dayHoursWinter = 2.0, nightHoursWinter = 1.0, criticality = "Essential") }
                addTemplate("Refrigerator (Inverter)", 1) { it.copy(dayHoursSummer = 12.0, nightHoursSummer = 12.0, dayHoursWinter = 12.0, nightHoursWinter = 12.0, criticality = "Critical") }
                addTemplate("TV LED 55\"", 1) { it.copy(dayHoursSummer = 2.0, nightHoursSummer = 4.0, dayHoursWinter = 3.0, nightHoursWinter = 5.0, criticality = "Optional") }
                addTemplate("Water Pump 1 HP", 1) { it.copy(dayHoursSummer = 1.5, nightHoursSummer = 0.5, dayHoursWinter = 1.0, nightHoursWinter = 0.5, criticality = "Essential") }
                addTemplate("Water Heater 50L", 1) { it.copy(dayHoursSummer = 2.0, nightHoursSummer = 0.0, dayHoursWinter = 3.0, nightHoursWinter = 0.0, criticality = "Essential", deferrableLoad = true, shiftableToDaytime = true) }
                addTemplate("Ceiling Fan", 4) { it.copy(dayHoursSummer = 10.0, nightHoursSummer = 6.0, dayHoursWinter = 1.0, nightHoursWinter = 0.0) }
                addTemplate("Microwave Oven", 1) { it.copy(dayHoursSummer = 0.5, nightHoursSummer = 0.0, dayHoursWinter = 0.5, nightHoursWinter = 0.0) }
                addTemplate("Washing Machine", 1) { it.copy(dayHoursSummer = 1.0, nightHoursSummer = 0.0, dayHoursWinter = 1.0, nightHoursWinter = 0.0, deferrableLoad = true) }
                addTemplate("CCTV System (4 cam)", 1) { it.copy(dayHoursSummer = 12.0, nightHoursSummer = 12.0, dayHoursWinter = 12.0, nightHoursWinter = 12.0, criticality = "Critical") }
            }
            "professional" -> {
                addTemplate("LED Bulb 12W", 8) { it.copy(dayHoursSummer = 2.0, nightHoursSummer = 5.0, dayHoursWinter = 2.0, nightHoursWinter = 6.0) }
                addTemplate("Split AC 1.5 Ton (Inverter)", 2) { it.copy(dayHoursSummer = 9.0, nightHoursSummer = 4.0, dayHoursWinter = 2.0, nightHoursWinter = 1.0, criticality = "Essential") }
                addTemplate("Refrigerator (Inverter)", 1) { it.copy(dayHoursSummer = 12.0, nightHoursSummer = 12.0, dayHoursWinter = 12.0, nightHoursWinter = 12.0, criticality = "Critical") }
                addTemplate("TV LED 55\"", 1) { it.copy(dayHoursSummer = 2.0, nightHoursSummer = 4.0, dayHoursWinter = 3.0, nightHoursWinter = 5.0, criticality = "Optional") }
                addTemplate("Water Pump 1 HP", 1) { it.copy(dayHoursSummer = 1.5, nightHoursSummer = 0.5, dayHoursWinter = 1.0, nightHoursWinter = 0.5, criticality = "Essential") }
                addTemplate("Water Heater 50L", 1) { it.copy(dayHoursSummer = 2.0, nightHoursSummer = 0.0, dayHoursWinter = 3.0, nightHoursWinter = 0.0, criticality = "Essential", deferrableLoad = true, shiftableToDaytime = true) }
                addTemplate("Ceiling Fan", 4) { it.copy(dayHoursSummer = 10.0, nightHoursSummer = 6.0, dayHoursWinter = 1.0, nightHoursWinter = 0.0) }
                addTemplate("Microwave Oven", 1) { it.copy(dayHoursSummer = 0.5, nightHoursSummer = 0.0, dayHoursWinter = 0.5, nightHoursWinter = 0.0) }
                addTemplate("Washing Machine", 1) { it.copy(dayHoursSummer = 1.0, nightHoursSummer = 0.0, dayHoursWinter = 1.0, nightHoursWinter = 0.0, deferrableLoad = true) }
                addTemplate("CCTV System (4 cam)", 1) { it.copy(dayHoursSummer = 12.0, nightHoursSummer = 12.0, dayHoursWinter = 12.0, nightHoursWinter = 12.0, criticality = "Critical") }
                // Pro additions
                addTemplate("LED Floodlight 100W", 4) { it.copy(dayHoursSummer = 0.0, nightHoursSummer = 10.0, dayHoursWinter = 0.0, nightHoursWinter = 12.0) }
                addTemplate("Server (Small)", 1) { it.copy(dayHoursSummer = 12.0, nightHoursSummer = 12.0, dayHoursWinter = 12.0, nightHoursWinter = 12.0, criticality = "Critical") }
                addTemplate("CPAP Machine", 1) { it.copy(dayHoursSummer = 0.0, nightHoursSummer = 8.0, dayHoursWinter = 0.0, nightHoursWinter = 8.0, criticality = "Critical") }
                addTemplate("EV Charger Level 2 (7kW)", 1) { it.copy(dayHoursSummer = 0.0, nightHoursSummer = 6.0, dayHoursWinter = 0.0, nightHoursWinter = 6.0, criticality = "Optional", deferrableLoad = true, shiftableToDaytime = true) }
            }
            "commercial" -> {
                addTemplate("LED Panel 40W", 30) { it.copy(dayHoursSummer = 9.0, nightHoursSummer = 2.0, dayHoursWinter = 9.0, nightHoursWinter = 2.0, criticality = "Essential") }
                addTemplate("Central AC 3 Ton", 4) { it.copy(dayHoursSummer = 10.0, nightHoursSummer = 2.0, dayHoursWinter = 4.0, nightHoursWinter = 0.0, criticality = "Essential") }
                addTemplate("3Ø Motor 10 HP", 2) { it.copy(dayHoursSummer = 8.0, nightHoursSummer = 0.0, dayHoursWinter = 8.0, nightHoursWinter = 0.0, criticality = "Critical") }
                addTemplate("Compressor 3Ø 7.5kW", 1) { it.copy(dayHoursSummer = 6.0, nightHoursSummer = 0.0, dayHoursWinter = 6.0, nightHoursWinter = 0.0, criticality = "Critical") }
                addTemplate("Desktop Computer", 20) { it.copy(dayHoursSummer = 9.0, nightHoursSummer = 0.0, dayHoursWinter = 9.0, nightHoursWinter = 0.0, criticality = "Essential") }
                addTemplate("Photocopier", 2) { it.copy(dayHoursSummer = 4.0, nightHoursSummer = 0.0, dayHoursWinter = 4.0, nightHoursWinter = 0.0) }
                addTemplate("LED Tube Light 18W", 12) { it.copy(dayHoursSummer = 0.0, nightHoursSummer = 12.0, dayHoursWinter = 0.0, nightHoursWinter = 12.0, criticality = "Critical", operatingMode = "Continuous") }
            }
        }
        return list
    }
}
