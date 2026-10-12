package com.example.model

enum class Language {
    EN, MY
}

data class Appliance(
    val id: String,
    val nameEn: String,
    val nameMy: String,
    val watts: Double,
    val surgeMultiplier: Double = 1.0,
    val quantity: Int = 1,
    val dayHours: Double = 0.0,
    val nightHours: Double = 0.0,
    val isInductive: Boolean = false
) {
    val totalWatts: Double get() = watts * quantity
    val peakSurgeWatts: Double get() = watts * quantity * surgeMultiplier
    val dayKwh: Double get() = (totalWatts * dayHours) / 1000.0
    val nightKwh: Double get() = (totalWatts * nightHours) / 1000.0
    val totalDailyKwh: Double get() = dayKwh + nightKwh
}

data class LocationPreset(
    val nameEn: String,
    val nameMy: String,
    val psh: Double,
    val ambientTempC: Double,
    val descriptionEn: String,
    val descriptionMy: String
)

data class PanelSpec(
    val modelName: String,
    val ratedWatts: Double = 580.0,
    val voc: Double = 51.2,
    val vmp: Double = 42.8,
    val isc: Double = 14.3,
    val imp: Double = 13.55,
    val tempCoeffPmax: Double = -0.30 // %/°C
)

data class BatteryPackSpec(
    val ah: Double,
    val voltage: Double = 51.2,
    val label: String
) {
    val kwh: Double get() = (ah * voltage) / 1000.0
}

data class SystemSizingResult(
    val continuousLoadKw: Double,
    val surgeLoadKw: Double,
    val recommendedInverterKw: Double,
    val inverterModelSuggestion: String,
    val daytimeKwh: Double,
    val nighttimeKwh: Double,
    val totalDailyKwh: Double,
    val requiredPvKwp: Double,
    val actualInstalledPvKwp: Double,
    val numberOfPanels: Int,
    val panelsPerString: Int,
    val parallelStrings: Int,
    val stringVocCold: Double,
    val stringVmp: Double,
    val arrayIsc: Double,
    val usableBatteryKwhNeeded: Double,
    val grossBatteryKwhNeeded: Double,
    val totalBatteryAhNeeded: Double,
    val batteryPacksCount: Int,
    val selectedPackAh: Double,
    val installedBatteryKwh: Double,
    val maxBatteryDischargeCurrentA: Double,
    val autonomyDays: Double,
    val pshUsed: Double
)

data class SwitchgearItem(
    val code: String,
    val titleEn: String,
    val titleMy: String,
    val ratingEn: String,
    val ratingMy: String,
    val specDetailsEn: String,
    val specDetailsMy: String,
    val standardRef: String,
    val warningEn: String? = null,
    val warningMy: String? = null,
    val category: SwitchgearCategory
)

enum class SwitchgearCategory {
    DC_SOLAR,
    DC_BATTERY,
    AC_GRID,
    AC_LOAD,
    EARTHING
}

data class CableCalculation(
    val circuitNameEn: String,
    val circuitNameMy: String,
    val lengthMeters: Double,
    val currentAmps: Double,
    val systemVoltage: Double,
    val recommendedCrossSectionMm2: Double,
    val actualVoltageDropV: Double,
    val voltageDropPercent: Double,
    val powerLossWatts: Double,
    val cableTypeEn: String,
    val cableTypeMy: String,
    val isCompliant: Boolean
)

data class TariffCalculationResult(
    val monthlyUnitsKwh: Double,
    val monthlySolarGenKwh: Double,
    val netMonthlyGridKwh: Double,
    val billWithoutSolarMmk: Double,
    val billWithSolarMmk: Double,
    val monthlyGridSavingsMmk: Double,
    val monthlyGenFuelSavingsMmk: Double,
    val totalMonthlySavingsMmk: Double,
    val annualSavingsMmk: Double,
    val estimatedTurnkeyCostMmk: Double,
    val paybackYears: Double,
    val tenYearNetSavingsMmk: Double,
    val tier1Units: Double,
    val tier1CostMmk: Double,
    val tier2Units: Double,
    val tier2CostMmk: Double,
    val tier3Units: Double,
    val tier3CostMmk: Double,
    val tier4Units: Double,
    val tier4CostMmk: Double
)
