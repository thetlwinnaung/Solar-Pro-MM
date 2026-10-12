package com.example.engine

import com.example.model.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt

object SolarEngineeringEngine {

    // Default Location presets for Myanmar
    val LOCATION_PRESETS = listOf(
        LocationPreset(
            nameEn = "Mandalay (Central)",
            nameMy = "မန္တလေး (အလယ်ပိုင်း)",
            psh = 5.0,
            ambientTempC = 38.0,
            descriptionEn = "Mandalay & Central Dry Zone: High solar irradiance, 5.0 PSH avg, hot dry climate.",
            descriptionMy = "မန္တလေးနှင့် အပူပိုင်းဒေသ - နေရောင်ခြည်စွမ်းအင် အလွန်ကောင်းမွန် (၅.၀ နာရီ)၊ အပူချိန်မြင့်မား။"
        ),
        LocationPreset(
            nameEn = "Yangon (Lower Myanmar)",
            nameMy = "ရန်ကုန် (အောက်မြန်မာပြည်)",
            psh = 4.6,
            ambientTempC = 34.0,
            descriptionEn = "Yangon & Delta: 4.6 PSH avg, monsoon rain factor June-Sept, humid maritime.",
            descriptionMy = "ရန်ကုန်နှင့် မြစ်ဝကျွန်းပေါ် - နေရောင်ခြည် ပျမ်းမျှ (၄.၆ နာရီ)၊ မိုးရာသီကာလပါ ထည့်သွင်းစဉ်းစား။"
        ),
        LocationPreset(
            nameEn = "Naypyitaw (Union Territory)",
            nameMy = "နေပြည်တော် (ပြည်ထောင်စုနယ်မြေ)",
            psh = 4.9,
            ambientTempC = 36.0,
            descriptionEn = "Naypyitaw: 4.9 PSH avg, consistent sunshine hours, hot summer.",
            descriptionMy = "နေပြည်တော် - နေရောင်ခြည် ပျမ်းမျှ (၄.၉ နာရီ)၊ နေသာချိန် တည်ငြိမ်။"
        ),
        LocationPreset(
            nameEn = "Bagan / Magway / Pakokku",
            nameMy = "ပုဂံ / မကွေး / ပခုက္ကူ",
            psh = 5.2,
            ambientTempC = 40.0,
            descriptionEn = "Bagan Dry Zone: Exceptional solar potential 5.2 PSH, intense ambient heat.",
            descriptionMy = "ပုဂံ / မကွေး အပူပိုင်းဒေသ - နေရောင်ခြည်အကောင်းဆုံး (၅.၂ နာရီ)၊ အပူချိန်မြင့်။"
        ),
        LocationPreset(
            nameEn = "Taunggyi / Shan State (Highlands)",
            nameMy = "တောင်ကြီး / ရှမ်းပြည်နယ် (ကုန်းမြင့်)",
            psh = 4.8,
            ambientTempC = 26.0,
            descriptionEn = "Shan Highlands: 4.8 PSH avg, lower ambient temp increases panel efficiency.",
            descriptionMy = "ရှမ်းပြည်နယ် ကုန်းမြင့်ဒေသ - နေရောင်ခြည် (၄.၈ နာရီ)၊ အေးမြသဖြင့် ဆိုလာပြားစွမ်းဆောင်ရည်ပိုကောင်း။"
        )
    )

    // Standard Battery Pack Options (51.2V Nominal 16S LiFePO4)
    val BATTERY_PACK_PRESETS = listOf(
        BatteryPackSpec(ah = 100.0, voltage = 51.2, label = "5.12 kWh (100Ah / 51.2V Wall-mount / Rack)"),
        BatteryPackSpec(ah = 200.0, voltage = 51.2, label = "10.24 kWh (200Ah / 51.2V Floor/Rack Module)"),
        BatteryPackSpec(ah = 280.0, voltage = 51.2, label = "14.33 kWh (280Ah / 51.2V High Capacity)"),
        BatteryPackSpec(ah = 300.0, voltage = 51.2, label = "15.36 kWh (300Ah / 51.2V Heavy Duty)")
    )

    // Standard Tier 1 Solar Panels available in Myanmar market
    val PANEL_PRESETS = listOf(
        PanelSpec(
            modelName = "580W N-Type TOPCon (Tier 1 Bifacial)",
            ratedWatts = 580.0,
            voc = 51.2,
            vmp = 42.8,
            isc = 14.3,
            imp = 13.55,
            tempCoeffPmax = -0.30
        ),
        PanelSpec(
            modelName = "600W Tier 1 High Efficiency",
            ratedWatts = 600.0,
            voc = 52.4,
            vmp = 44.0,
            isc = 14.6,
            imp = 13.64,
            tempCoeffPmax = -0.30
        ),
        PanelSpec(
            modelName = "550W Standard Mono PERC",
            ratedWatts = 550.0,
            voc = 49.8,
            vmp = 41.9,
            isc = 14.0,
            imp = 13.13,
            tempCoeffPmax = -0.35
        )
    )

    // Default Appliance List tailored for Myanmar households and shops
    fun getDefaultAppliances(): List<Appliance> = listOf(
        Appliance(
            id = "app_ac_1hp",
            nameEn = "1.0 HP Inverter Air Conditioner",
            nameMy = "၁.၀ ကောင် အင်ဗာတာ လေအေးပေးစက်",
            watts = 750.0,
            surgeMultiplier = 1.5,
            quantity = 1,
            dayHours = 4.0,
            nightHours = 4.0,
            isInductive = true
        ),
        Appliance(
            id = "app_fridge",
            nameEn = "Inverter Refrigerator (2-Door)",
            nameMy = "အင်ဗာတာ ရေခဲသေတ္တာ (၂ တံခါး)",
            watts = 150.0,
            surgeMultiplier = 2.0,
            quantity = 1,
            dayHours = 12.0,
            nightHours = 12.0,
            isInductive = true
        ),
        Appliance(
            id = "app_water_pump",
            nameEn = "1.0 HP Domestic Water Pump",
            nameMy = "၁.၀ ကောင် ရေတင်မော်တာ",
            watts = 750.0,
            surgeMultiplier = 4.0,
            quantity = 1,
            dayHours = 1.0,
            nightHours = 0.0,
            isInductive = true
        ),
        Appliance(
            id = "app_rice_cooker",
            nameEn = "Electric Rice Cooker",
            nameMy = "လျှပ်စစ် ထမင်းပေါင်းအိုး",
            watts = 700.0,
            surgeMultiplier = 1.0,
            quantity = 1,
            dayHours = 1.5,
            nightHours = 0.0,
            isInductive = false
        ),
        Appliance(
            id = "app_induction_stove",
            nameEn = "Induction Cooker / Hotplate",
            nameMy = "လျှပ်စစ် မီးဖို (Induction Cooker)",
            watts = 1800.0,
            surgeMultiplier = 1.0,
            quantity = 0,
            dayHours = 1.0,
            nightHours = 0.0,
            isInductive = false
        ),
        Appliance(
            id = "app_lights",
            nameEn = "LED Home Lighting (10W x 10)",
            nameMy = "အိမ်သုံး LED မီးသီးများ (၁၀ ဝပ် x ၁၀ လုံး)",
            watts = 100.0,
            surgeMultiplier = 1.0,
            quantity = 1,
            dayHours = 2.0,
            nightHours = 6.0,
            isInductive = false
        ),
        Appliance(
            id = "app_ceiling_fans",
            nameEn = "Ceiling Fans (75W each)",
            nameMy = "မျက်နှာကျက် ပန်ကာများ (၇၅ ဝပ်)",
            watts = 150.0, // 2 fans
            surgeMultiplier = 1.3,
            quantity = 1,
            dayHours = 6.0,
            nightHours = 6.0,
            isInductive = true
        ),
        Appliance(
            id = "app_smart_tv",
            nameEn = "Smart TV 55\" & Soundbar",
            nameMy = "စမတ်တီဗွီ ၅၅ လက်မ နှင့် စပီကာ",
            watts = 140.0,
            surgeMultiplier = 1.0,
            quantity = 1,
            dayHours = 2.0,
            nightHours = 4.0,
            isInductive = false
        ),
        Appliance(
            id = "app_wifi_cctv",
            nameEn = "Wi-Fi Router & CCTV System",
            nameMy = "ဝိုင်ဖိုင် ရူတာ နှင့် CCTV လုံခြုံရေးကင်မရာ",
            watts = 50.0,
            surgeMultiplier = 1.0,
            quantity = 1,
            dayHours = 12.0,
            nightHours = 12.0,
            isInductive = false
        ),
        Appliance(
            id = "app_desktop_pc",
            nameEn = "Desktop Computer / Workstation",
            nameMy = "ကွန်ပျူတာ စားပွဲတင်စက် / ရုံးသုံး",
            watts = 350.0,
            surgeMultiplier = 1.2,
            quantity = 0,
            dayHours = 6.0,
            nightHours = 0.0,
            isInductive = false
        )
    )

    /**
     * Sizing Core:
     * Computes Inverter kW, LiFePO4 Battery kWh @ 51.2V DC Nominal, and PV Array kWp.
     */
    fun calculateSystemSize(
        appliances: List<Appliance>,
        psh: Double = 5.0,
        panelSpec: PanelSpec = PANEL_PRESETS[0],
        selectedBatteryPackAh: Double = 100.0,
        autonomyDays: Double = 1.0,
        batteryDod: Double = 0.80, // 80% DoD for 6000 cycles
        batteryEfficiency: Double = 0.95,
        inverterEfficiency: Double = 0.93,
        systemPerformanceRatio: Double = 0.78 // Accounts for Mandalay heat derating & dust
    ): SystemSizingResult {
        var continuousWatts = 0.0
        var maxMotorSurgeAddition = 0.0
        var totalSurgeWatts = 0.0
        var daytimeKwh = 0.0
        var nighttimeKwh = 0.0

        for (app in appliances) {
            val appContinuous = app.totalWatts
            continuousWatts += appContinuous

            val appSurge = app.peakSurgeWatts
            totalSurgeWatts += appSurge
            val surgeDiff = appSurge - appContinuous
            if (surgeDiff > maxMotorSurgeAddition) {
                maxMotorSurgeAddition = surgeDiff
            }

            daytimeKwh += app.dayKwh
            nighttimeKwh += app.nightKwh
        }

        val totalDailyKwh = daytimeKwh + nighttimeKwh
        val continuousKw = continuousWatts / 1000.0

        // Inverter Sizing:
        // Continuous kW * 1.25 safety headroom + motor surge capacity
        val continuousWithSafetyKw = continuousKw * 1.25
        val surgeTotalKw = (continuousWatts + maxMotorSurgeAddition) / 1000.0

        // Select standard hybrid inverter rating available in Myanmar (Deye, Luxpower, Growatt, SRNE)
        val standardInverterSizes = listOf(3.6, 5.0, 6.0, 8.0, 10.0, 12.0, 15.0)
        var recommendedInverterKw = standardInverterSizes.firstOrNull { it >= continuousWithSafetyKw } ?: 15.0
        if (continuousWithSafetyKw > 15.0) {
            recommendedInverterKw = ceil(continuousWithSafetyKw)
        }

        val inverterModel = when {
            recommendedInverterKw <= 3.6 -> "3.6 kW Hybrid Inverter (Single Phase 230V, 48V/51.2V DC)"
            recommendedInverterKw <= 5.0 -> "5.0 kW Hybrid Inverter (Single Phase 230V, 48V/51.2V DC - Deye/Luxpower)"
            recommendedInverterKw <= 6.0 -> "6.0 kW Hybrid Inverter (Single Phase 230V, 48V/51.2V DC - Deye/Luxpower)"
            recommendedInverterKw <= 8.0 -> "8.0 kW Hybrid Inverter (Single Phase 230V, 48V/51.2V DC - Dual MPPT)"
            recommendedInverterKw <= 10.0 -> "10.0 kW Hybrid Inverter (Single or Three Phase 400V, Low/High Voltage)"
            recommendedInverterKw <= 12.0 -> "12.0 kW Hybrid Inverter (Three Phase 400V / 230V Parallel Capable)"
            else -> "${recommendedInverterKw.roundToInt()} kW Paralleled Hybrid System"
        }

        // LiFePO4 Battery Sizing @ 51.2V Nominal:
        // Usable energy needed at night = (Nighttime kWh * Autonomy) / Inverter Efficiency
        val usableBatteryKwhNeeded = (nighttimeKwh * autonomyDays) / inverterEfficiency
        // Gross Battery Bank = Usable / (DoD * Battery roundtrip efficiency)
        val grossBatteryKwhNeeded = if (usableBatteryKwhNeeded > 0.0) {
            usableBatteryKwhNeeded / (batteryDod * batteryEfficiency)
        } else 0.0

        // Total Ah required at 51.2V DC
        val nominalVoltage = 51.2
        val totalBatteryAhNeeded = if (grossBatteryKwhNeeded > 0.0) {
            (grossBatteryKwhNeeded * 1000.0) / nominalVoltage
        } else 0.0

        // Parallel Pack Calculation:
        val packsCount = if (totalBatteryAhNeeded > 0.0) {
            max(1, ceil(totalBatteryAhNeeded / selectedBatteryPackAh).toInt())
        } else 1

        val installedBatteryKwh = (packsCount * selectedBatteryPackAh * nominalVoltage) / 1000.0

        // Battery maximum discharge current @ low voltage cutoff (44.0V) under full inverter load
        val cutoffVoltage = 44.0
        val maxBatteryDischargeCurrentA = (recommendedInverterKw * 1000.0) / (cutoffVoltage * inverterEfficiency)

        // PV Array Sizing:
        // Daily total energy / (PSH * PR)
        val effectivePsh = max(1.0, psh)
        val requiredPvKwp = if (totalDailyKwh > 0.0) {
            totalDailyKwh / (effectivePsh * systemPerformanceRatio)
        } else 0.0

        val panelWattKw = panelSpec.ratedWatts / 1000.0
        val numberOfPanels = max(1, ceil(requiredPvKwp / panelWattKw).toInt())
        val actualInstalledPvKwp = (numberOfPanels * panelSpec.ratedWatts) / 1000.0

        // String configuration check:
        // Modern hybrid inverters (e.g. Deye 5kW) have MPPT voltage range 150V - 450V, Max Voc 500V.
        // Cold temperature Voc (+3.5% at 15°C cold winter morning in Mandalay)
        val vocColdPerPanel = panelSpec.voc * 1.035
        // Max panels in series to stay below 500V limit
        val maxPanelsSeries = floor(500.0 / vocColdPerPanel).toInt() // usually 9 or 10

        var parallelStrings = 1
        var panelsPerString = numberOfPanels

        if (numberOfPanels > maxPanelsSeries) {
            // Need 2 strings (either 2 separate MPPTs or 2 strings paralleled)
            parallelStrings = 2
            panelsPerString = ceil(numberOfPanels / 2.0).toInt()
        }

        val stringVocCold = panelsPerString * vocColdPerPanel
        val stringVmp = panelsPerString * panelSpec.vmp
        val arrayIsc = parallelStrings * panelSpec.isc

        return SystemSizingResult(
            continuousLoadKw = continuousKw,
            surgeLoadKw = surgeTotalKw,
            recommendedInverterKw = recommendedInverterKw,
            inverterModelSuggestion = inverterModel,
            daytimeKwh = daytimeKwh,
            nighttimeKwh = nighttimeKwh,
            totalDailyKwh = totalDailyKwh,
            requiredPvKwp = requiredPvKwp,
            actualInstalledPvKwp = actualInstalledPvKwp,
            numberOfPanels = numberOfPanels,
            panelsPerString = panelsPerString,
            parallelStrings = parallelStrings,
            stringVocCold = stringVocCold,
            stringVmp = stringVmp,
            arrayIsc = arrayIsc,
            usableBatteryKwhNeeded = usableBatteryKwhNeeded,
            grossBatteryKwhNeeded = grossBatteryKwhNeeded,
            totalBatteryAhNeeded = totalBatteryAhNeeded,
            batteryPacksCount = packsCount,
            selectedPackAh = selectedBatteryPackAh,
            installedBatteryKwh = installedBatteryKwh,
            maxBatteryDischargeCurrentA = maxBatteryDischargeCurrentA,
            autonomyDays = autonomyDays,
            pshUsed = psh
        )
    }

    /**
     * Switchgear Sizing complying with IEC 60364-7-712:
     * Generates protection devices for DC Solar, Battery, AC Grid, AC Load, and Earthing.
     */
    fun calculateSwitchgearProtection(
        sizing: SystemSizingResult,
        panelSpec: PanelSpec
    ): List<SwitchgearItem> {
        val items = mutableListOf<SwitchgearItem>()

        // 1. DC String Fuses (IEC 60364-7-712.533)
        // Required when 3 or more strings connected in parallel
        val fuseCurrentMin = 1.25 * 1.25 * panelSpec.isc // 1.56 * Isc
        val standardFuseRatings = listOf(15.0, 20.0, 25.0, 30.0, 32.0)
        val selectedFuseRating = standardFuseRatings.firstOrNull { it >= fuseCurrentMin } ?: 32.0
        val isFuseMandatory = sizing.parallelStrings >= 3

        items.add(
            SwitchgearItem(
                code = "DC-FUSE",
                titleEn = "DC String Fuses (gPV 1000V DC)",
                titleMy = "ဆိုလာပြား DC ဖျူးစ် (gPV ၁၀၀၀ ဗို့)",
                ratingEn = "${selectedFuseRating.roundToInt()}A / 1000V DC (10x38mm Cylindrical gPV)",
                ratingMy = "${selectedFuseRating.roundToInt()} အမ်ပီယာ / ၁၀၀၀ ဗို့ DC (10x38mm)",
                specDetailsEn = "Rated In >= 1.25 * 1.25 * Isc (${String.format("%.1f", fuseCurrentMin)}A). Protection against reverse overcurrent. " +
                        if (isFuseMandatory) "MANDATORY as system has >= 3 parallel strings."
                        else "Recommended for individual string isolation and maintenance (${sizing.parallelStrings} string(s)).",
                specDetailsMy = "တွက်ချက်မှု In >= 1.25 * 1.25 * Isc (${String.format("%.1f", fuseCurrentMin)}A)။ ပြောင်းပြန်စီးဝင်သော လျှပ်စီးကို ကာကွယ်ရန်။ " +
                        if (isFuseMandatory) "လိုင်း ၃ လိုင်းနှင့်အထက် ပြိုင်လျက်တပ်ဆင်ထားပါက မဖြစ်မနေ တပ်ဆင်ရပါမည်။"
                        else "လိုင်းခွဲ ပြုပြင်ထိန်းသိမ်းမှုလွယ်ကူစေရန် အကြံပြုထားသည်။",
                standardRef = "IEC 60364-7-712 Clause 712.533",
                category = SwitchgearCategory.DC_SOLAR
            )
        )

        // 2. DC Isolator MCB (PV Array)
        val minIsolatorVoltage = sizing.stringVocCold * 1.25
        val minIsolatorCurrent = sizing.arrayIsc * 1.25
        val standardDcMcbCurrents = listOf(16.0, 20.0, 25.0, 32.0, 40.0, 50.0, 63.0)
        val selectedDcMcbCurrent = standardDcMcbCurrents.firstOrNull { it >= minIsolatorCurrent } ?: 63.0
        val dcVoltageRating = if (minIsolatorVoltage > 500) "1000V DC" else "600V / 1000V DC"

        items.add(
            SwitchgearItem(
                code = "DC-MCB",
                titleEn = "PV Array DC Isolator MCB (2-Pole)",
                titleMy = "ဆိုလာလိုင်း DC ပင်မဘရိတ်ကာ (2-Pole MCB)",
                ratingEn = "${selectedDcMcbCurrent.roundToInt()}A 2P / $dcVoltageRating (Curve C, Non-polarized)",
                ratingMy = "${selectedDcMcbCurrent.roundToInt()} အမ်ပီယာ / $dcVoltageRating (DC သီးသန့် ဘရိတ်ကာ)",
                specDetailsEn = "Sized for Ue >= 1.25 * Voc_cold (${String.format("%.1f", minIsolatorVoltage)}V) and In >= 1.25 * Isc (${String.format("%.1f", minIsolatorCurrent)}A). Must be true DC rated with arc chute mechanism.",
                specDetailsMy = "သတ်မှတ်ချက် Ue >= 1.25 * Voc_cold (${String.format("%.1f", minIsolatorVoltage)}V) နှင့် In >= 1.25 * Isc (${String.format("%.1f", minIsolatorCurrent)}A)။ စစ်မှန်သော DC Arc Chute ပါဝင်သည့် ဘရိတ်ကာ ဖြစ်ရပါမည်။ AC ဘရိတ်ကာ လုံးဝအသုံးမပြုရပါ။",
                standardRef = "IEC 60364-7-712 Clause 712.536.2.2",
                warningEn = "NEVER use standard AC MCBs on DC solar strings! DC arcs do not cross zero and will cause dangerous electrical fires.",
                warningMy = "ဆိုလာလိုင်းတွင် AC ဘရိတ်ကာ လုံးဝမသုံးရပါ။ မီးလောင်ကျွမ်းမှု အန္တရာယ် အလွန်ကြီးမားပါသည်။",
                category = SwitchgearCategory.DC_SOLAR
            )
        )

        // 3. DC Surge Protective Device (SPD)
        val dcSpdVoltage = if (sizing.stringVocCold > 500) "1000V DC" else "600V DC"
        items.add(
            SwitchgearItem(
                code = "DC-SPD",
                titleEn = "PV DC Surge Protective Device (Type 2 SPD)",
                titleMy = "ဆိုလာလိုင်း မိုးကြိုးနှင့် ဗို့အားမြင့်ကာကွယ်မှု (Type 2 DC SPD)",
                ratingEn = "Type 2 SPD, Uc = $dcSpdVoltage, In = 20kA, Imax = 40kA, Up <= 2.8kV",
                ratingMy = "Type 2 SPD, ဗို့အား $dcSpdVoltage, In 20kA / Imax 40kA",
                specDetailsEn = "Max continuous operating voltage Uc >= 1.2 * Voc_cold (${String.format("%.1f", sizing.stringVocCold)}V). Protects inverter electronics from induced lightning overvoltages during Myanmar monsoon season.",
                specDetailsMy = "မန္တလေးနှင့် မြန်မာနိုင်ငံ မိုးကြိုးလျှပ်စီးထစ်ချုန်းမှုများမှ အင်ဗာတာပျက်စီးခြင်းကို ကာကွယ်ရန်။ မြေစိုက်လိုင်းနှင့် ခိုင်မာစွာ ချိတ်ဆက်ရပါမည်။",
                standardRef = "IEC 61643-31 / IEC 60364-7-712.534",
                category = SwitchgearCategory.DC_SOLAR
            )
        )

        // 4. Battery DC MCCB / High Current Disconnector
        val batteryDischargeCurrent = sizing.maxBatteryDischargeCurrentA
        val minBatteryMccbRating = batteryDischargeCurrent * 1.25
        val standardMccbRatings = listOf(100.0, 125.0, 160.0, 200.0, 250.0, 300.0, 400.0)
        val selectedBatteryMccb = standardMccbRatings.firstOrNull { it >= minBatteryMccbRating } ?: 400.0

        items.add(
            SwitchgearItem(
                code = "BATT-MCCB",
                titleEn = "LiFePO4 Battery DC MCCB / Disconnect Switch",
                titleMy = "လစ်သီယမ်ဘက်ထရီ DC MCCB ပင်မဘရိတ်ကာ",
                ratingEn = "${selectedBatteryMccb.roundToInt()}A 2P DC MCCB (Rated >= 125V DC, Breaking Capacity Icu >= 25kA)",
                ratingMy = "${selectedBatteryMccb.roundToInt()} အမ်ပီယာ DC MCCB (ဗို့အား ၁၂၅V DC အထက်၊ 25kA Breaking Capacity)",
                specDetailsEn = "Max Continuous Discharge: ${String.format("%.1f", batteryDischargeCurrent)}A @ 44.0V Cutoff. Sized with 1.25x safety factor. High interrupting capacity (>= 25kA) is mandatory because LiFePO4 batteries deliver extreme short-circuit fault current.",
                specDetailsMy = "အင်ဗာတာ အပြည့်အဝမောင်းနှင်ချိန် ဘက်ထရီမှ စီးဆင်းသော အမြင့်ဆုံးလျှပ်စီး: ${String.format("%.1f", batteryDischargeCurrent)}A။ လစ်သီယမ်ဘက်ထရီသည် Short-Circuit ဖြစ်ပါက လျှပ်စီးအလွန်မြင့်သဖြင့် စံချိန်မီ DC MCCB မဖြစ်မနေ အသုံးပြုရမည်။",
                standardRef = "IEC 60947-2 (DC Utilization Cat. DC-22B / DC-23B)",
                warningEn = "Must possess high breaking capacity (AIC >= 20kA). Standard AC MCCBs cannot clear DC lithium short-circuits safely.",
                warningMy = "Standard AC MCCB မသုံးရပါ။ လစ်သီယမ်ဘက်ထရီ Short ဖြစ်ပါက ဖြတ်တောက်နိုင်စွမ်းရှိသော DC MCCB ဖြစ်ရပါမည်။",
                category = SwitchgearCategory.DC_BATTERY
            )
        )

        // 5. Battery Class T / NH High-Speed DC Fuse
        items.add(
            SwitchgearItem(
                code = "BATT-FUSE",
                titleEn = "High-Speed DC Fast-Acting Fuse (Class T / NH00)",
                titleMy = "ဘက်ထရီ အမြန်ပြတ် လျှပ်စစ်ဖျူးစ် (Class T / NH00 DC Fuse)",
                ratingEn = "${selectedBatteryMccb.roundToInt()}A DC Fuse (Interrupting Rating 20kA - 50kA)",
                ratingMy = "${selectedBatteryMccb.roundToInt()} အမ်ပီယာ အမြန်ပြတ် DC Fuse",
                specDetailsEn = "Secondary ultra-fast protection against catastrophic battery bus bar direct short circuits. Clears arc faults within milliseconds before BMS damage.",
                specDetailsMy = "ဘက်ထရီအတွင်းပိုင်း သို့မဟုတ် ကေဘယ်လိုင်း တိုက်ရိုက်ထိခိုက်ပြတ်တောက်မှုဖြစ်ပါက မီလီစက္ကန့်အတွင်း အမြန်ဖြတ်တောက်ပေးသည့် အထူးဖျူးစ်။",
                standardRef = "UL 248-15 / IEC 60269-4",
                category = SwitchgearCategory.DC_BATTERY
            )
        )

        // 6. AC Grid Input MCB (Single Phase 230V or Three Phase)
        val acInputCurrent = (sizing.recommendedInverterKw * 1000.0) / 230.0
        val minAcInputMcb = acInputCurrent * 1.25
        val standardAcMcbs = listOf(20.0, 25.0, 32.0, 40.0, 50.0, 63.0, 80.0, 100.0)
        val selectedAcInputMcb = standardAcMcbs.firstOrNull { it >= minAcInputMcb } ?: 63.0

        items.add(
            SwitchgearItem(
                code = "AC-IN-MCB",
                titleEn = "AC Grid / Generator Input MCB (2-Pole)",
                titleMy = "အစိုးရမီတာလိုင်း / မီးစက် အဝင်ဘရိတ်ကာ (2P MCB)",
                ratingEn = "${selectedAcInputMcb.roundToInt()}A 2P (230V AC, Curve C, 6kA / 10kA)",
                ratingMy = "${selectedAcInputMcb.roundToInt()} အမ်ပီယာ 2P AC ဘရိတ်ကာ (Curve C)",
                specDetailsEn = "Inverter AC charging & grid pass-through rating: ${String.format("%.1f", acInputCurrent)}A * 1.25 safety factor. Isolates EPC grid connection safely.",
                specDetailsMy = "အင်ဗာတာ ဘက်ထရီအားသွင်းခြင်းနှင့် မီတာမီး ဖြတ်သန်းစီးဆင်းမှု လျှပ်စီး ${String.format("%.1f", acInputCurrent)}A အတွက် ၁.၂၅ ဆ ဘေးကင်းလုံခြုံရေးဖြင့် တွက်ချက်ထားသည်။",
                standardRef = "IEC 60898-1 / IEC 60947-2",
                category = SwitchgearCategory.AC_GRID
            )
        )

        // 7. AC Din-Rail Over & Under Voltage / Current Protector (Critical for Myanmar!)
        items.add(
            SwitchgearItem(
                code = "AC-VAPROT",
                titleEn = "Automatic Over/Under Voltage & Current Protector",
                titleMy = "အလိုအလျောက် ဗို့အားမြင့်/နိမ့် အကာအကွယ် (V/A Protector)",
                ratingEn = "63A / 230V Din-Rail Digital Display (e.g. TOMZN / SINOTIMER)",
                ratingMy = "၆၃ အမ်ပီယာ / ၂၃၀ ဗို့ ဒစ်ဂျစ်တယ် ဗို့အားထိန်းစနစ်",
                specDetailsEn = "RECOMMENDED SETTINGS FOR MYANMAR GRID: Over-voltage cutoff: 260V | Under-voltage cutoff: 175V | Delay reconnect timer: 30-60s. Prevents erratic transformer surges and brownouts from burning the hybrid inverter AC motherboard.",
                specDetailsMy = "မြန်မာနိုင်ငံ လျှပ်စစ်လိုင်းအတွက် အကြံပြုသတ်မှတ်ချက်များ - အမြင့်ဗို့အား ဖြတ်တောက်မှု: 260V | အနိမ့်ဗို့အား ဖြတ်တောက်မှု: 175V | ပြန်လည်ဆက်သွယ်ချိန်: ၃၀ မှ ၆၀ စက္ကန့်။ မီးအားအတက်အကျကြောင့် အင်ဗာတာ မီးလောင်ပျက်စီးခြင်းမှ ကာကွယ်ပေးသည်။",
                standardRef = "Myanmar EPC & Power Quality Field Guideline",
                category = SwitchgearCategory.AC_GRID
            )
        )

        // 8. AC Grid Surge Protective Device (AC SPD)
        items.add(
            SwitchgearItem(
                code = "AC-SPD",
                titleEn = "AC Mains Surge Protective Device (Type 2 SPD)",
                titleMy = "AC မီးလိုင်း မိုးကြိုးနှင့် ဗို့အားလှိုင်းကာကွယ်မှု (Type 2 AC SPD)",
                ratingEn = "Type 2 SPD, Uc = 275V / 385V AC (1P+N), In = 20kA, Imax = 40kA",
                ratingMy = "Type 2 AC SPD, ဗို့အား 275V / 385V AC, In 20kA / Imax 40kA",
                specDetailsEn = "Clamps utility line lightning switching transients and indirect strikes before reaching the inverter input terminal.",
                specDetailsMy = "အစိုးရ မီးတိုင်လိုင်းမှ ဝင်ရောက်လာသော မိုးကြိုးလျှပ်စီးလှိုင်းနှင့် ဗို့အားလှိုင်းများကို မြေစိုက်လိုင်းသို့ စွန့်ထုတ်ပေးသည်။",
                standardRef = "IEC 61643-11",
                category = SwitchgearCategory.AC_GRID
            )
        )

        // 9. AC Output / Backup Load MCB
        val acOutputCurrent = (sizing.recommendedInverterKw * 1000.0) / 230.0
        val minAcOutputMcb = acOutputCurrent * 1.25
        val selectedAcOutputMcb = standardAcMcbs.firstOrNull { it >= minAcOutputMcb } ?: 63.0

        items.add(
            SwitchgearItem(
                code = "AC-OUT-MCB",
                titleEn = "AC Inverter Backup Load Output MCB (2-Pole)",
                titleMy = "အင်ဗာတာ အထွက် / အရေးပေါ်မီးသုံးစွဲမှု ဘရိတ်ကာ (2P MCB)",
                ratingEn = "${selectedAcOutputMcb.roundToInt()}A 2P (230V AC, Curve C, 6kA)",
                ratingMy = "${selectedAcOutputMcb.roundToInt()} အမ်ပီယာ 2P AC ဘရိတ်ကာ (Curve C)",
                specDetailsEn = "Guards inverter UPS/Backup output port against downstream short-circuits and appliance branch overloads.",
                specDetailsMy = "အင်ဗာတာမှ အိမ်တွင်းအရေးပေါ်သုံး လျှပ်စစ်ပစ္စည်းများသို့ သွားသော လိုင်းအတွက် ဝန်ပိုနှင့် ဝါယာရှော့ခ် အကာအကွယ်။",
                standardRef = "IEC 60898-1",
                category = SwitchgearCategory.AC_LOAD
            )
        )

        // 10. Residual Current Device (RCD / ELCB)
        items.add(
            SwitchgearItem(
                code = "AC-RCD",
                titleEn = "Residual Current Device (Type A 30mA RCD / RCBO)",
                titleMy = "လျှပ်စစ်ဓာတ်လိုက်မှု အကာအကွယ် (၃၀ မီလီအမ်ပီယာ RCD / ELCB)",
                ratingEn = "63A 2P, 30mA Sensitivity, Type A (AC & Pulsating DC Residual Current)",
                ratingMy = "၆၃ အမ်ပီယာ / ၃၀ မီလီအမ်ပီယာ Type A ဓာတ်လိုက်မှုကာကွယ်ရေး",
                specDetailsEn = "Life-safety protection against electric shock and earth leakage faults on home circuits. Type A detects pulsed DC leakage currents from modern electronics.",
                specDetailsMy = "လူကို လျှပ်စစ်ဓာတ်လိုက်မှု အန္တရာယ်နှင့် လျှပ်စစ်ယိုစိမ့်မှုမှ ကာကွယ်ရန် ၃၀ မီလီအမ်ပီယာ ထိခိုက်မှုတွင် ချက်ချင်းဖြတ်တောက်ပေးသည်။",
                standardRef = "IEC 61008-1 / IEC 62423",
                category = SwitchgearCategory.AC_LOAD
            )
        )

        // 11. Earthing & Lightning Bonding
        items.add(
            SwitchgearItem(
                code = "EARTH-SYS",
                titleEn = "Grounding Electrode & Equipotential Bonding",
                titleMy = "မြေစိုက်စနစ် နှင့် ပင်မမြေစိုက်တိုင် (Earthing System)",
                ratingEn = "Target Resistance < 5.0 Ohms (5/8\" x 8-10ft Copper-Bonded Steel Rod)",
                ratingMy = "မြေစိုက်ခုခံမှု ၅ အုမ်း အောက် (၅/၈ လက်မ x ၈ မှ ၁၀ ပေ ကြေးနီကပ် သံတိုင်)",
                specDetailsEn = "Solar panel anodized frames (6-10 mm² yellow-green), Inverter chassis, AC/DC SPDs (16 mm² copper), and Main Distribution Board must connect to an equipotential earth busbar.",
                specDetailsMy = "ဆိုလာပြားဘောင်များ၊ အင်ဗာတာ ကိုယ်ထည်၊ SPD မိုးကြိုးကာကွယ်ရေးပစ္စည်းများကို ပင်မမြေစိုက်ဘားနှင့် ချိတ်ဆက်ကာ မြေစိုက်ခုခံမှု ၅ အုမ်းအောက် ရရှိအောင် ပြုလုပ်ရပါမည်။",
                standardRef = "IEC 60364-5-54 & IEEE 142",
                category = SwitchgearCategory.EARTHING
            )
        )

        return items
    }

    /**
     * Copper Cable Cross-Section Calculations:
     * Specific formulas for PV string cable, Battery Superflex, AC Grid/Load lines, and Earthing.
     * Copper resistivity ρ = 0.0175 Ω·mm²/m.
     * Formula: ΔV = (2 * L * I * ρ) / S
     */
    fun calculateCables(
        sizing: SystemSizingResult,
        panelSpec: PanelSpec,
        pvCableLengthM: Double = 25.0,
        batteryCableLengthM: Double = 2.0,
        acCableLengthM: Double = 15.0
    ): List<CableCalculation> {
        val rho = 0.0175 // Copper resistivity at 20°C in Ω·mm²/m
        val list = mutableListOf<CableCalculation>()

        // 1. PV String Cable
        // Current = Imp; Voltage = String Vmp
        val pvCurrent = panelSpec.imp
        val pvVoltage = max(1.0, sizing.stringVmp)
        // Select cross-section aiming for ΔV < 1.5%
        val pvSizes = listOf(4.0, 6.0, 10.0)
        var bestPvSize = 4.0
        for (s in pvSizes) {
            val dV = (2.0 * pvCableLengthM * pvCurrent * rho) / s
            val dVPercent = (dV / pvVoltage) * 100.0
            bestPvSize = s
            if (dVPercent <= 1.5) break
        }
        val pvDv = (2.0 * pvCableLengthM * pvCurrent * rho) / bestPvSize
        val pvDvPercent = (pvDv / pvVoltage) * 100.0
        val pvLossWatts = pvCurrent * pvCurrent * ((2.0 * pvCableLengthM * rho) / bestPvSize)

        list.add(
            CableCalculation(
                circuitNameEn = "PV Array String DC Cable",
                circuitNameMy = "ဆိုလာပြားမှ အင်ဗာတာသို့ DC လိုင်းကြိုး",
                lengthMeters = pvCableLengthM,
                currentAmps = pvCurrent,
                systemVoltage = pvVoltage,
                recommendedCrossSectionMm2 = bestPvSize,
                actualVoltageDropV = pvDv,
                voltageDropPercent = pvDvPercent,
                powerLossWatts = pvLossWatts,
                cableTypeEn = "Tinned Copper Double-Insulated XLPO Solar Cable (1500V DC, UV Resistant)",
                cableTypeMy = "နေရောင်ဒဏ်ခံ ၁၅၀၀ ဗို့ DC သီးသန့် အထူးဆိုလာကြိုး (XLPO Solar Cable)",
                isCompliant = pvDvPercent <= 2.0
            )
        )

        // 2. Battery DC Superflex Cable
        // Current = Max discharge current; Voltage = 51.2V DC Nominal
        val battCurrent = sizing.maxBatteryDischargeCurrentA
        val battVoltage = 51.2
        val battSizes = listOf(25.0, 35.0, 50.0, 70.0, 95.0, 120.0)
        var bestBattSize = 25.0
        for (s in battSizes) {
            val dV = (2.0 * batteryCableLengthM * battCurrent * rho) / s
            val dVPercent = (dV / battVoltage) * 100.0
            bestBattSize = s
            // For battery, voltage drop MUST be < 1.0% (<0.51V) to prevent low-voltage error trips!
            if (dVPercent <= 0.8) break
        }
        val battDv = (2.0 * batteryCableLengthM * battCurrent * rho) / bestBattSize
        val battDvPercent = (battDv / battVoltage) * 100.0
        val battLossWatts = battCurrent * battCurrent * ((2.0 * batteryCableLengthM * rho) / bestBattSize)

        list.add(
            CableCalculation(
                circuitNameEn = "LiFePO4 Battery Bank DC Superflex Cable",
                circuitNameMy = "လစ်သီယမ်ဘက်ထရီ ချိတ်ဆက် အထူးကေဘယ်ကြိုး (Superflex)",
                lengthMeters = batteryCableLengthM,
                currentAmps = battCurrent,
                systemVoltage = battVoltage,
                recommendedCrossSectionMm2 = bestBattSize,
                actualVoltageDropV = battDv,
                voltageDropPercent = battDvPercent,
                powerLossWatts = battLossWatts,
                cableTypeEn = "Multi-Strand Pure Tinned Copper Superflex Battery / Welding Cable (0.6/1kV)",
                cableTypeMy = "အပူခံ အမျှင်နု ကြေးနီစစ်စစ် ဘက်ထရီသီးသန့် ကေဘယ်ကြိုးကြီး",
                isCompliant = battDvPercent <= 1.0
            )
        )

        // 3. AC Grid Input Cable
        val acCurrent = (sizing.recommendedInverterKw * 1000.0) / 230.0
        val acVoltage = 230.0
        val acSizes = listOf(4.0, 6.0, 10.0, 16.0, 25.0)
        var bestAcInSize = 4.0
        for (s in acSizes) {
            val dV = (2.0 * acCableLengthM * acCurrent * rho) / s
            val dVPercent = (dV / acVoltage) * 100.0
            bestAcInSize = s
            if (dVPercent <= 1.5) break
        }
        val acInDv = (2.0 * acCableLengthM * acCurrent * rho) / bestAcInSize
        val acInDvPercent = (acInDv / acVoltage) * 100.0
        val acInLossWatts = acCurrent * acCurrent * ((2.0 * acCableLengthM * rho) / bestAcInSize)

        list.add(
            CableCalculation(
                circuitNameEn = "AC Grid Input Line (Meter to Inverter)",
                circuitNameMy = "အစိုးရမီတာမှ အင်ဗာတာသို့ AC အဝင်လိုင်းကြိုး",
                lengthMeters = acCableLengthM,
                currentAmps = acCurrent,
                systemVoltage = acVoltage,
                recommendedCrossSectionMm2 = bestAcInSize,
                actualVoltageDropV = acInDv,
                voltageDropPercent = acInDvPercent,
                powerLossWatts = acInLossWatts,
                cableTypeEn = "PVC / XLPE Insulated Annealed Copper Conductor Cable (600/1000V)",
                cableTypeMy = "ကြေးနီစစ်စစ် PVC/XLPE လျှပ်ကာကြိုး (ဥပမာ - ရွှေကြိုး၊ ပုဂံကြိုး စံချိန်မီ)",
                isCompliant = acInDvPercent <= 2.5
            )
        )

        // 4. AC Output / Backup Load Cable
        list.add(
            CableCalculation(
                circuitNameEn = "AC Backup Loads Output Line",
                circuitNameMy = "အင်ဗာတာမှ အရေးပေါ်မီးသုံးစွဲမှု ဘုတ်သို့ လိုင်းကြိုး",
                lengthMeters = acCableLengthM,
                currentAmps = acCurrent,
                systemVoltage = acVoltage,
                recommendedCrossSectionMm2 = bestAcInSize,
                actualVoltageDropV = acInDv,
                voltageDropPercent = acInDvPercent,
                powerLossWatts = acInLossWatts,
                cableTypeEn = "PVC / XLPE Insulated Multi-core Copper Cable",
                cableTypeMy = "အိမ်တွင်းဖြန့်ဝေရေး ကြေးနီစစ်စစ် လိုင်းကြိုး",
                isCompliant = acInDvPercent <= 2.5
            )
        )

        // 5. Earthing Conductor
        list.add(
            CableCalculation(
                circuitNameEn = "Earthing & Lightning Down Conductor",
                circuitNameMy = "ပင်မမြေစိုက်လိုင်း နှင့် မိုးကြိုးဆင်းကြိုး",
                lengthMeters = 15.0,
                currentAmps = 40.0,
                systemVoltage = 230.0,
                recommendedCrossSectionMm2 = 16.0,
                actualVoltageDropV = 0.0,
                voltageDropPercent = 0.0,
                powerLossWatts = 0.0,
                cableTypeEn = "16.0 mm² High-Conductivity Green-Yellow Insulated / Bare Copper Cable",
                cableTypeMy = "၁၆ စကွဲယာမီလီမီတာ ကြေးနီစစ်စစ် အစိမ်းဝါ မြေစိုက်ကြိုးကြီး",
                isCompliant = true
            )
        )

        return list
    }

    /**
     * Myanmar MOEP / MESC Residential Tiered Electricity Tariff Calculation:
     * - Tier 1 (1–50 units): 50 MMK / unit
     * - Tier 2 (51–100 units): 100 MMK / unit
     * - Tier 3 (101–200 units): 150 MMK / unit
     * - Tier 4 (201+ units): 300 MMK / unit
     *
     * Plus Myanmar load-shedding diesel generator avoidance savings calculation.
     */
    fun calculateTariffAndRoi(
        sizing: SystemSizingResult,
        customDailyUnitsKwh: Double? = null,
        hoursBlackoutPerDay: Double = 4.0, // Default 4 hours/day load shedding in Myanmar
        dieselGenCostPerKwhMmk: Double = 2200.0, // Typical diesel gen running cost in MMK/unit
        estimatedTurnkeyCostLakhs: Double? = null // Cost in Lakhs MMK (1 Lakh = 100,000 MMK)
    ): TariffCalculationResult {
        val dailyUnits = customDailyUnitsKwh ?: sizing.totalDailyKwh
        val monthlyUnits = dailyUnits * 30.0

        // Monthly solar generation = PV kWp * PSH * PR * 30 days
        val monthlySolarGenKwh = sizing.actualInstalledPvKwp * sizing.pshUsed * 0.78 * 30.0
        val netMonthlyGridKwh = max(0.0, monthlyUnits - monthlySolarGenKwh)

        // 1. Bill WITHOUT solar
        val (billWithoutSolar, t1U, t1C, t2U, t2C, t3U, t3C, t4U, t4C) = computeMoepTieredBill(monthlyUnits)

        // 2. Bill WITH solar
        val (billWithSolar, _, _, _, _, _, _, _, _) = computeMoepTieredBill(netMonthlyGridKwh)

        val monthlyGridSavingsMmk = max(0.0, billWithoutSolar - billWithSolar)

        // 3. Generator replacement savings during load shedding (blackouts):
        // If solar + LiFePO4 battery powers the house during 4 hours of daily blackout,
        // it avoids running an expensive diesel/petrol generator.
        val avoidedBlackoutDailyKwh = (dailyUnits / 24.0) * hoursBlackoutPerDay
        val monthlyGenFuelSavingsMmk = avoidedBlackoutDailyKwh * 30.0 * dieselGenCostPerKwhMmk

        val totalMonthlySavingsMmk = monthlyGridSavingsMmk + monthlyGenFuelSavingsMmk
        val annualSavingsMmk = totalMonthlySavingsMmk * 12.0

        // Turnkey System Estimated Cost:
        // If not specified by user, estimate realistic Myanmar market turnkey pricing:
        // Panels ~ 650 MMK/Watt
        // Inverter ~ 2.5 - 3.5 Million MMK (5kW Deye)
        // LiFePO4 Battery ~ 7.5 - 9.0 Million MMK per 10kWh pack
        // Switchgear box + Cables + Mounting + Installation labor ~ 2.0 - 3.0 Million MMK
        val turnkeyCostMmk = if (estimatedTurnkeyCostLakhs != null && estimatedTurnkeyCostLakhs > 0) {
            estimatedTurnkeyCostLakhs * 100_000.0
        } else {
            val panelCost = sizing.actualInstalledPvKwp * 1000.0 * 680.0
            val inverterCost = when {
                sizing.recommendedInverterKw <= 3.6 -> 2_500_000.0
                sizing.recommendedInverterKw <= 5.0 -> 3_500_000.0
                sizing.recommendedInverterKw <= 6.0 -> 4_200_000.0
                sizing.recommendedInverterKw <= 8.0 -> 6_000_000.0
                sizing.recommendedInverterKw <= 10.0 -> 8_000_000.0
                else -> sizing.recommendedInverterKw * 850_000.0
            }
            val batteryCost = sizing.installedBatteryKwh * 850_000.0 // ~8.5 Lakhs per kWh installed
            val bosLaborCost = 2_800_000.0 // Balance of system (breakers, SPDs, box, cables, rails, labor)
            panelCost + inverterCost + batteryCost + bosLaborCost
        }

        val paybackYears = if (annualSavingsMmk > 0) turnkeyCostMmk / annualSavingsMmk else 99.0
        val tenYearNetSavingsMmk = (annualSavingsMmk * 10.0) - turnkeyCostMmk

        return TariffCalculationResult(
            monthlyUnitsKwh = monthlyUnits,
            monthlySolarGenKwh = monthlySolarGenKwh,
            netMonthlyGridKwh = netMonthlyGridKwh,
            billWithoutSolarMmk = billWithoutSolar,
            billWithSolarMmk = billWithSolar,
            monthlyGridSavingsMmk = monthlyGridSavingsMmk,
            monthlyGenFuelSavingsMmk = monthlyGenFuelSavingsMmk,
            totalMonthlySavingsMmk = totalMonthlySavingsMmk,
            annualSavingsMmk = annualSavingsMmk,
            estimatedTurnkeyCostMmk = turnkeyCostMmk,
            paybackYears = paybackYears,
            tenYearNetSavingsMmk = tenYearNetSavingsMmk,
            tier1Units = t1U,
            tier1CostMmk = t1C,
            tier2Units = t2U,
            tier2CostMmk = t2C,
            tier3Units = t3U,
            tier3CostMmk = t3C,
            tier4Units = t4U,
            tier4CostMmk = t4C
        )
    }

    /**
     * Exact calculation of Myanmar MOEP residential tiered electricity bill:
     * Tier 1: 1 - 50 units @ 50 MMK
     * Tier 2: 51 - 100 units (50 units) @ 100 MMK
     * Tier 3: 101 - 200 units (100 units) @ 150 MMK
     * Tier 4: 201+ units @ 300 MMK
     */
    private fun computeMoepTieredBill(units: Double): TierBreakdown {
        var remaining = units
        var bill = 0.0

        // Tier 1: 1-50 units @ 50 MMK
        val t1U = remaining.coerceAtMost(50.0).coerceAtLeast(0.0)
        val t1C = t1U * 50.0
        bill += t1C
        remaining -= t1U

        // Tier 2: 51-100 units (next 50 units) @ 100 MMK
        val t2U = remaining.coerceAtMost(50.0).coerceAtLeast(0.0)
        val t2C = t2U * 100.0
        bill += t2C
        remaining -= t2U

        // Tier 3: 101-200 units (next 100 units) @ 150 MMK
        val t3U = remaining.coerceAtMost(100.0).coerceAtLeast(0.0)
        val t3C = t3U * 150.0
        bill += t3C
        remaining -= t3U

        // Tier 4: 201+ units @ 300 MMK
        val t4U = remaining.coerceAtLeast(0.0)
        val t4C = t4U * 300.0
        bill += t4C

        return TierBreakdown(bill, t1U, t1C, t2U, t2C, t3U, t3C, t4U, t4C)
    }

    private data class TierBreakdown(
        val totalBill: Double,
        val t1U: Double, val t1C: Double,
        val t2U: Double, val t2C: Double,
        val t3U: Double, val t3C: Double,
        val t4U: Double, val t4C: Double
    )
}
