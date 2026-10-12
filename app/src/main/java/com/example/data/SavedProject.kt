package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_projects")
data class SavedProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val clientName: String,
    val location: String,
    val psh: Double,
    val inverterKw: Double,
    val batteryKwh: Double,
    val pvKwp: Double,
    val panelCount: Int,
    val totalDailyKwh: Double,
    val monthlySavingsMmk: Double,
    val estimatedCostMmk: Double,
    val paybackYears: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
