package com.team2898.robot

import edu.wpi.first.networktables.NetworkTableInstance

object DriverDashboard {
    val table = NetworkTableInstance.getDefault().getTable("dashboard")

    fun number(key: String, value: Double) {
        table.getEntry(key).setDouble(value)
    }

    fun string(key: String, value: String) {
        table.getEntry(key).setString(value)
    }

    fun boolean(key: String, value: Boolean) {
        table.getEntry(key).setBoolean(value)
    }
}