package com.team2898.robot

import com.bpsrobotics.engine.utils.geometry.*
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj2.command.SubsystemBase
import com.bpsrobotics.engine.utils.ft
class Map(val community : Polygon,
          val chargingDock : Rectangle,
          val loadingBay : Rectangle
        ) {
}

//TODO: Replace placeholder coordinates
val blueTeam = Map(
    community = Polygon(
        Coordinate(0.0,0.0),
        Coordinate.new(18.ft,0.ft)
    ),
    chargingDock = Rectangle(0.0,0.0,0.0,0.0),
    loadingBay = Rectangle(0.0,0.0,0.0,0.0)
)
val redTeam = Map(
    community = Polygon(Coordinate(0.0,0.0)),
    chargingDock = Rectangle(0.0,0.0,0.0,0.0),
    loadingBay = Rectangle(0.0,0.0,0.0,0.0)
)
object Field : SubsystemBase() {
    private var teamColor: DriverStation.Alliance = DriverStation.getAlliance()
    lateinit var map : Map;
    fun initialize() {
        map = if (teamColor == DriverStation.Alliance.Blue) {
            blueTeam
        } else {
            redTeam
        }
    }
}
