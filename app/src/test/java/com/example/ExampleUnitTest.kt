package com.example

import com.example.engine.SolarEngineeringEngine
import com.example.model.PanelSpec
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testMoepTieredTariffCalculation() {
        // Test 250 units:
        // Tier 1 (50 units * 50 MMK) = 2,500 MMK
        // Tier 2 (50 units * 100 MMK) = 5,000 MMK
        // Tier 3 (100 units * 150 MMK) = 15,000 MMK
        // Tier 4 (50 units * 300 MMK) = 15,000 MMK
        // Total expected = 37,500 MMK
        val defaultAppliances = SolarEngineeringEngine.getDefaultAppliances()
        val sizing = SolarEngineeringEngine.calculateSystemSize(
            appliances = defaultAppliances,
            psh = 5.0
        )
        val tariffResult = SolarEngineeringEngine.calculateTariffAndRoi(
            sizing = sizing,
            customDailyUnitsKwh = 250.0 / 30.0,
            hoursBlackoutPerDay = 0.0
        )

        assertEquals(37500.0, tariffResult.billWithoutSolarMmk, 0.1)
        assertEquals(50.0, tariffResult.tier1Units, 0.01)
        assertEquals(2500.0, tariffResult.tier1CostMmk, 0.01)
        assertEquals(50.0, tariffResult.tier2Units, 0.01)
        assertEquals(5000.0, tariffResult.tier2CostMmk, 0.01)
        assertEquals(100.0, tariffResult.tier3Units, 0.01)
        assertEquals(15000.0, tariffResult.tier3CostMmk, 0.01)
        assertEquals(50.0, tariffResult.tier4Units, 0.01)
        assertEquals(15000.0, tariffResult.tier4CostMmk, 0.01)
    }

    @Test
    fun testSolarSystemSizingMandalay5Psh() {
        val appliances = SolarEngineeringEngine.getDefaultAppliances()
        val sizing = SolarEngineeringEngine.calculateSystemSize(
            appliances = appliances,
            psh = 5.0,
            selectedBatteryPackAh = 100.0,
            autonomyDays = 1.0,
            batteryDod = 0.80
        )

        assertTrue(sizing.continuousLoadKw > 0)
        assertTrue(sizing.surgeLoadKw >= sizing.continuousLoadKw)
        assertTrue(sizing.recommendedInverterKw >= sizing.continuousLoadKw * 1.25)
        assertTrue(sizing.installedBatteryKwh > 0)
        assertTrue(sizing.actualInstalledPvKwp > 0)
        assertTrue(sizing.numberOfPanels >= 1)
        assertTrue(sizing.maxBatteryDischargeCurrentA > 50.0) // 5kW / 44V / 0.93 ~ 122A
    }

    @Test
    fun testSwitchgearComplianceIec60364() {
        val appliances = SolarEngineeringEngine.getDefaultAppliances()
        val panel = SolarEngineeringEngine.PANEL_PRESETS[0]
        val sizing = SolarEngineeringEngine.calculateSystemSize(appliances = appliances, psh = 5.0, panelSpec = panel)
        val switchgear = SolarEngineeringEngine.calculateSwitchgearProtection(sizing = sizing, panelSpec = panel)

        assertTrue(switchgear.any { it.code == "DC-FUSE" })
        assertTrue(switchgear.any { it.code == "DC-MCB" })
        assertTrue(switchgear.any { it.code == "DC-SPD" })
        assertTrue(switchgear.any { it.code == "BATT-MCCB" })
        assertTrue(switchgear.any { it.code == "AC-VAPROT" })
        assertTrue(switchgear.any { it.code == "AC-SPD" })
        assertTrue(switchgear.any { it.code == "AC-RCD" })
    }

    @Test
    fun testCableCalculationsAndVoltageDrop() {
        val appliances = SolarEngineeringEngine.getDefaultAppliances()
        val panel = SolarEngineeringEngine.PANEL_PRESETS[0]
        val sizing = SolarEngineeringEngine.calculateSystemSize(appliances = appliances, psh = 5.0, panelSpec = panel)
        val cables = SolarEngineeringEngine.calculateCables(
            sizing = sizing,
            panelSpec = panel,
            pvCableLengthM = 25.0,
            batteryCableLengthM = 2.0,
            acCableLengthM = 15.0
        )

        val battCable = cables.first { it.circuitNameEn.contains("Battery") }
        assertTrue("Battery voltage drop must be <= 1%", battCable.voltageDropPercent <= 1.0)

        val pvCable = cables.first { it.circuitNameEn.contains("PV Array") }
        assertTrue("PV voltage drop must be <= 2%", pvCable.voltageDropPercent <= 2.0)
    }
}
