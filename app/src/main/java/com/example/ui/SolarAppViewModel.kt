package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.SavedProject
import com.example.engine.SolarEngineeringEngine
import com.example.model.*
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SolarAppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val projectDao = db.savedProjectDao()

    val savedProjects: StateFlow<List<SavedProject>> = projectDao.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Language setting
    private val _language = MutableStateFlow(Language.EN)
    val language: StateFlow<Language> = _language.asStateFlow()

    // Navigation tab index
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // Location / Climate
    private val _selectedLocationIndex = MutableStateFlow(0) // Default Mandalay (5.0 PSH)
    val selectedLocationIndex: StateFlow<Int> = _selectedLocationIndex.asStateFlow()

    private val _customPsh = MutableStateFlow<Double?>(null)
    val customPsh: StateFlow<Double?> = _customPsh.asStateFlow()

    // Solar Panel Spec
    private val _selectedPanelIndex = MutableStateFlow(0)
    val selectedPanelIndex: StateFlow<Int> = _selectedPanelIndex.asStateFlow()

    // Battery Pack Spec (100Ah, 200Ah, 280Ah, 300Ah @ 51.2V)
    private val _selectedBatteryAh = MutableStateFlow(100.0)
    val selectedBatteryAh: StateFlow<Double> = _selectedBatteryAh.asStateFlow()

    private val _batteryDod = MutableStateFlow(0.80) // 80% DoD
    val batteryDod: StateFlow<Double> = _batteryDod.asStateFlow()

    private val _autonomyDays = MutableStateFlow(1.0)
    val autonomyDays: StateFlow<Double> = _autonomyDays.asStateFlow()

    // Cable lengths
    private val _pvCableLength = MutableStateFlow(25.0)
    val pvCableLength: StateFlow<Double> = _pvCableLength.asStateFlow()

    private val _batteryCableLength = MutableStateFlow(2.0)
    val batteryCableLength: StateFlow<Double> = _batteryCableLength.asStateFlow()

    private val _acCableLength = MutableStateFlow(15.0)
    val acCableLength: StateFlow<Double> = _acCableLength.asStateFlow()

    // Tariff & Blackout parameters
    private val _hoursBlackoutPerDay = MutableStateFlow(4.0) // 4 hrs load shedding in Mandalay/Yangon
    val hoursBlackoutPerDay: StateFlow<Double> = _hoursBlackoutPerDay.asStateFlow()

    private val _dieselGenCostPerKwhMmk = MutableStateFlow(2200.0)
    val dieselGenCostPerKwhMmk: StateFlow<Double> = _dieselGenCostPerKwhMmk.asStateFlow()

    private val _customTurnkeyCostLakhs = MutableStateFlow<Double?>(null)
    val customTurnkeyCostLakhs: StateFlow<Double?> = _customTurnkeyCostLakhs.asStateFlow()

    // Appliances list
    private val _appliances = MutableStateFlow(SolarEngineeringEngine.getDefaultAppliances())
    val appliances: StateFlow<List<Appliance>> = _appliances.asStateFlow()

    // Notification / Toast message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == Language.EN) Language.MY else Language.EN
    }

    fun setLanguage(lang: Language) {
        _language.value = lang
    }

    fun setLocationIndex(index: Int) {
        _selectedLocationIndex.value = index
        _customPsh.value = null
    }

    fun setCustomPsh(psh: Double?) {
        _customPsh.value = psh
    }

    fun setPanelIndex(index: Int) {
        _selectedPanelIndex.value = index
    }

    fun setBatteryPackAh(ah: Double) {
        _selectedBatteryAh.value = ah
    }

    fun setBatteryDod(dod: Double) {
        _batteryDod.value = dod
    }

    fun setAutonomyDays(days: Double) {
        _autonomyDays.value = days
    }

    fun setPvCableLength(length: Double) {
        _pvCableLength.value = length.coerceAtLeast(1.0)
    }

    fun setBatteryCableLength(length: Double) {
        _batteryCableLength.value = length.coerceAtLeast(0.5)
    }

    fun setAcCableLength(length: Double) {
        _acCableLength.value = length.coerceAtLeast(1.0)
    }

    fun setHoursBlackoutPerDay(hours: Double) {
        _hoursBlackoutPerDay.value = hours.coerceIn(0.0, 24.0)
    }

    fun setCustomTurnkeyCostLakhs(lakhs: Double?) {
        _customTurnkeyCostLakhs.value = lakhs
    }

    fun updateApplianceQuantity(id: String, newQty: Int) {
        _appliances.value = _appliances.value.map {
            if (it.id == id) it.copy(quantity = newQty.coerceAtLeast(0)) else it
        }
    }

    fun updateApplianceHours(id: String, dayHours: Double, nightHours: Double) {
        _appliances.value = _appliances.value.map {
            if (it.id == id) it.copy(
                dayHours = dayHours.coerceIn(0.0, 24.0),
                nightHours = nightHours.coerceIn(0.0, 24.0)
            ) else it
        }
    }

    fun updateApplianceWatts(id: String, watts: Double) {
        _appliances.value = _appliances.value.map {
            if (it.id == id) it.copy(watts = watts.coerceAtLeast(1.0)) else it
        }
    }

    fun addCustomAppliance(
        nameEn: String,
        nameMy: String,
        watts: Double,
        surgeMultiplier: Double,
        quantity: Int,
        dayHours: Double,
        nightHours: Double,
        isInductive: Boolean
    ) {
        val newApp = Appliance(
            id = "custom_${System.currentTimeMillis()}",
            nameEn = nameEn.ifBlank { "Custom Appliance" },
            nameMy = nameMy.ifBlank { "အခြားသုံးစွဲမှု" },
            watts = watts,
            surgeMultiplier = surgeMultiplier,
            quantity = quantity,
            dayHours = dayHours,
            nightHours = nightHours,
            isInductive = isInductive
        )
        _appliances.value = _appliances.value + newApp
    }

    fun removeAppliance(id: String) {
        _appliances.value = _appliances.value.filterNot { it.id == id }
    }

    fun resetToDefaults() {
        _appliances.value = SolarEngineeringEngine.getDefaultAppliances()
        _selectedLocationIndex.value = 0
        _selectedPanelIndex.value = 0
        _selectedBatteryAh.value = 100.0
        _batteryDod.value = 0.80
        _autonomyDays.value = 1.0
        _hoursBlackoutPerDay.value = 4.0
        _userMessage.value = "Reset to Mandalay 5.0 PSH standard engineering defaults."
    }

    // Helper getters for current state
    fun getCurrentPsh(): Double {
        return _customPsh.value ?: SolarEngineeringEngine.LOCATION_PRESETS[_selectedLocationIndex.value].psh
    }

    fun getCurrentPanelSpec(): PanelSpec {
        return SolarEngineeringEngine.PANEL_PRESETS.getOrElse(_selectedPanelIndex.value) {
            SolarEngineeringEngine.PANEL_PRESETS[0]
        }
    }

    fun getCurrentSizing(): SystemSizingResult {
        return SolarEngineeringEngine.calculateSystemSize(
            appliances = _appliances.value,
            psh = getCurrentPsh(),
            panelSpec = getCurrentPanelSpec(),
            selectedBatteryPackAh = _selectedBatteryAh.value,
            autonomyDays = _autonomyDays.value,
            batteryDod = _batteryDod.value
        )
    }

    fun getCurrentSwitchgear(): List<SwitchgearItem> {
        val sizing = getCurrentSizing()
        return SolarEngineeringEngine.calculateSwitchgearProtection(
            sizing = sizing,
            panelSpec = getCurrentPanelSpec()
        )
    }

    fun getCurrentCables(): List<CableCalculation> {
        val sizing = getCurrentSizing()
        return SolarEngineeringEngine.calculateCables(
            sizing = sizing,
            panelSpec = getCurrentPanelSpec(),
            pvCableLengthM = _pvCableLength.value,
            batteryCableLengthM = _batteryCableLength.value,
            acCableLengthM = _acCableLength.value
        )
    }

    fun getCurrentTariff(): TariffCalculationResult {
        val sizing = getCurrentSizing()
        return SolarEngineeringEngine.calculateTariffAndRoi(
            sizing = sizing,
            hoursBlackoutPerDay = _hoursBlackoutPerDay.value,
            dieselGenCostPerKwhMmk = _dieselGenCostPerKwhMmk.value,
            estimatedTurnkeyCostLakhs = _customTurnkeyCostLakhs.value
        )
    }

    fun saveProject(title: String, clientName: String, notes: String) {
        viewModelScope.launch {
            val sizing = getCurrentSizing()
            val tariff = getCurrentTariff()
            val locName = SolarEngineeringEngine.LOCATION_PRESETS[_selectedLocationIndex.value].nameEn
            val psh = getCurrentPsh()

            val proj = SavedProject(
                title = title.ifBlank { "Solar Project #${System.currentTimeMillis() % 1000}" },
                clientName = clientName.ifBlank { "Client" },
                location = locName,
                psh = psh,
                inverterKw = sizing.recommendedInverterKw,
                batteryKwh = sizing.installedBatteryKwh,
                pvKwp = sizing.actualInstalledPvKwp,
                panelCount = sizing.numberOfPanels,
                totalDailyKwh = sizing.totalDailyKwh,
                monthlySavingsMmk = tariff.totalMonthlySavingsMmk,
                estimatedCostMmk = tariff.estimatedTurnkeyCostMmk,
                paybackYears = tariff.paybackYears,
                notes = notes
            )
            projectDao.insertProject(proj)
            _userMessage.value = "Project saved successfully to local database!"
        }
    }

    fun deleteProject(project: SavedProject) {
        viewModelScope.launch {
            projectDao.deleteProject(project)
            _userMessage.value = "Project removed."
        }
    }

    fun generateBomReportText(): String {
        val sizing = getCurrentSizing()
        val switchgear = getCurrentSwitchgear()
        val cables = getCurrentCables()
        val tariff = getCurrentTariff()
        val panel = getCurrentPanelSpec()
        val loc = SolarEngineeringEngine.LOCATION_PRESETS[_selectedLocationIndex.value]

        return buildString {
            appendLine("==================================================")
            appendLine("  SOLAR PV ELECTRICAL DESIGN & PROTECTION SCHEDULE")
            appendLine("  IEC 60364-7-712 COMPLIANT (MYANMAR FRAMEWORK)")
            appendLine("==================================================")
            appendLine("Location: ${loc.nameEn} | PSH: ${sizing.pshUsed} hrs/day")
            appendLine("Connected Daily Demand: ${String.format("%.2f", sizing.totalDailyKwh)} kWh/day (Day: ${String.format("%.2f", sizing.daytimeKwh)} kWh, Night: ${String.format("%.2f", sizing.nighttimeKwh)} kWh)")
            appendLine("Continuous Load: ${String.format("%.2f", sizing.continuousLoadKw)} kW | Peak Motor Surge: ${String.format("%.2f", sizing.surgeLoadKw)} kW")
            appendLine()
            appendLine("1. INVERTER & BATTERY STORAGE SIZING:")
            appendLine("   - Inverter: ${sizing.inverterModelSuggestion}")
            appendLine("   - LiFePO4 Battery Bank: ${sizing.installedBatteryKwh} kWh @ 51.2V DC (${sizing.batteryPacksCount}x ${sizing.selectedPackAh.roundToInt()}Ah Packs in Parallel)")
            appendLine("   - Max Continuous Battery Discharge Current: ${String.format("%.1f", sizing.maxBatteryDischargeCurrentA)} A @ 44.0V Cutoff")
            appendLine("   - Usable DoD: 80% (Long life cycle)")
            appendLine()
            appendLine("2. PV ARRAY CONFIGURATION:")
            appendLine("   - Panel Model: ${panel.modelName} (${panel.ratedWatts.roundToInt()}W)")
            appendLine("   - Total Panels: ${sizing.numberOfPanels} Units (${String.format("%.2f", sizing.actualInstalledPvKwp)} kWp)")
            appendLine("   - Array Configuration: ${sizing.parallelStrings} String(s) x ${sizing.panelsPerString} Panels in series")
            appendLine("   - String Voc (Cold 15°C): ${String.format("%.1f", sizing.stringVocCold)} V | Vmp: ${String.format("%.1f", sizing.stringVmp)} V")
            appendLine("   - Array Isc: ${String.format("%.2f", sizing.arrayIsc)} A")
            appendLine()
            appendLine("3. SWITCHGEAR PROTECTION (IEC 60364-7-712):")
            for (sw in switchgear) {
                appendLine("   [${sw.code}] ${sw.titleEn}")
                appendLine("     Rating: ${sw.ratingEn}")
                appendLine("     Standard: ${sw.standardRef}")
            }
            appendLine()
            appendLine("4. COPPER CABLE CROSS-SECTIONS:")
            for (cb in cables) {
                appendLine("   - ${cb.circuitNameEn}: ${cb.recommendedCrossSectionMm2} mm² (${cb.cableTypeEn})")
                appendLine("     Length: ${cb.lengthMeters}m | Current: ${String.format("%.1f", cb.currentAmps)}A | Voltage Drop: ${String.format("%.2f", cb.voltageDropPercent)}%")
            }
            appendLine()
            appendLine("5. MYANMAR MOEP TARIFF & ROI:")
            appendLine("   - Monthly Consumption: ${String.format("%.1f", tariff.monthlyUnitsKwh)} Units (kWh)")
            appendLine("   - Monthly Solar Generation: ${String.format("%.1f", tariff.monthlySolarGenKwh)} Units")
            appendLine("   - MOEP Bill Without Solar: ${String.format("%,.0f", tariff.billWithoutSolarMmk)} MMK/mo")
            appendLine("   - MOEP Bill With Solar: ${String.format("%,.0f", tariff.billWithSolarMmk)} MMK/mo")
            appendLine("   - Monthly Grid Savings: ${String.format("%,.0f", tariff.monthlyGridSavingsMmk)} MMK/mo")
            appendLine("   - Load Shedding Generator Fuel Avoided: ${String.format("%,.0f", tariff.monthlyGenFuelSavingsMmk)} MMK/mo")
            appendLine("   - Total Monthly Savings: ${String.format("%,.0f", tariff.totalMonthlySavingsMmk)} MMK/mo")
            appendLine("   - Annual Savings: ${String.format("%,.0f", tariff.annualSavingsMmk)} MMK/year")
            appendLine("   - Estimated Turnkey Cost: ${String.format("%,.0f", tariff.estimatedTurnkeyCostMmk)} MMK (~${String.format("%.1f", tariff.estimatedTurnkeyCostMmk / 100_000.0)} Lakhs MMK)")
            appendLine("   - Simple Payback Period: ${String.format("%.1f", tariff.paybackYears)} Years")
            appendLine("==================================================")
        }
    }
}
