package com.team2898.robot

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj2.command.SubsystemBase

object FieldMap : SubsystemBase() {
    var teamColor = DriverStation.getAlliance()

    fun initialize() {
        teamColor = DriverStation.getAlliance()
        if (teamColor == DriverStation.Alliance.Blue) {
            println("team is blue")
        }
    }
}
