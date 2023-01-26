package com.team2898.robot

import com.bpsrobotics.engine.utils.geometry.*
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj2.command.SubsystemBase
import com.bpsrobotics.engine.utils.ft
import com.bpsrobotics.engine.utils.toMeters

class Map(val community : Polygon,
          val chargingDock : Rectangle,
          val loadingBay : Polygon,
          val gridWall : Line
        ) {
}
private val redLoadingBay = Polygon(
    Coordinate(0.0.ft, 18.0.ft),
    Coordinate(11.0.ft, 18.0.ft),
    Coordinate(11.0.ft, 22.048.ft),
    Coordinate(22.0.ft, 22.048.ft),
    Coordinate(22.0.ft, 26.25.ft),
    Coordinate(0.0.ft, 26.25.ft),
)
private val blueLoadingBay = Polygon(
    Coordinate(4.5.ft,0.0.ft),
    Coordinate(4.5.ft,18.0.ft),
    Coordinate(11.0.ft,18.0.ft),
    Coordinate(11.0.ft,13.22.ft),
    Coordinate(16.07.ft,13.22.ft),
    Coordinate(16.07.ft, 0.0.ft)
)
private val blueChargeStation = Rectangle(
    Coordinate(9.56.ft,13.05.ft),
    Coordinate(15.9.ft,4.95.ft)
)
private val blueGridWall = Line(
    Coordinate(4.5.ft,0.0.ft),
    Coordinate(4.5.ft,18.0.ft),
    )

//TODO: Replace placeholder coordinates
val blueTeam = Map(

    //x + 4.5
    community = blueLoadingBay,
    chargingDock = blueChargeStation,
    loadingBay = redLoadingBay.reflectHorizontally(27.ft),
    gridWall = blueGridWall
)
val redTeam = Map(
    community = blueLoadingBay.reflectHorizontally(27.ft),
    chargingDock = blueChargeStation.reflectHorizontally(27.ft),
    loadingBay = redLoadingBay,
    gridWall = blueGridWall.reflectHorizontally(27.ft)


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
