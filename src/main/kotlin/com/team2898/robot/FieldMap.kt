package com.team2898.robot

import com.bpsrobotics.engine.utils.Rectangle
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj2.command.SubsystemBase

object FieldMap : SubsystemBase() {
    private var teamColor: DriverStation.Alliance = DriverStation.getAlliance()
    private lateinit var communityCoordinates: Rectangle
    private lateinit var chargingDock: Rectangle
    private lateinit var loadingBay: Rectangle
    fun initialize() {
        //TODO: Replace placeholder coordinates
        teamColor = DriverStation.getAlliance()
        if (teamColor == DriverStation.Alliance.Blue) {
            communityCoordinates = Rectangle(0.0,0.0,0.0,0.0)
            chargingDock = Rectangle(0.0,0.0,0.0,0.0)
            loadingBay = Rectangle(0.0,0.0,0.0,0.0)
        }
        else {
            communityCoordinates = Rectangle(0.0,0.0,0.0,0.0)
            chargingDock = Rectangle(0.0,0.0,0.0,0.0)
            loadingBay = Rectangle(0.0,0.0,0.0,0.0)
        }
    }
}
