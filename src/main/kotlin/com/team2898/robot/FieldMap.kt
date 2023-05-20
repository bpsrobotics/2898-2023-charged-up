package com.team2898.robot

import com.bpsrobotics.engine.utils.DistanceUnit
import com.bpsrobotics.engine.utils.geometry.*
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj2.command.SubsystemBase
import com.bpsrobotics.engine.utils.ft
import com.bpsrobotics.engine.utils.m
import edu.wpi.first.math.geometry.Pose2d

/**
 * Type of scoring location
 */
enum class ScoringType {
    CONE, CUBE
}

/**
 * Stores the type and desired robot location for a scoring location
 */
data class ScoreSpot(
        val RobotPosition: Coordinate,
        val type: ScoringType
        ){
    /**
     * @return Y distance to give pose
     * @param pose Pose to find distance to
     */
    fun distance(pose: Pose2d): Double{
        return RobotPosition.ydistance(pose)
    }
    /**
     * @return ScoreSpot object reflected over the give vertical line
     * @param x Vertical line to reflect over
     */
    fun reflectHorizontally(x: DistanceUnit): ScoreSpot{
        return ScoreSpot(RobotPosition.reflectHorizontally(x), type)
    }
}

/**
 * Stores scoring locations for the robot to align to
 * @property Cone1 The cone scoring location closest to 0, 0
 * @property Cube The cube scoring location
 * @property Cone2 The cone scoring location furthest from 0, 0
 */
data class Grid(
        val Cone1: ScoreSpot,
        val Cube: ScoreSpot,
        val Cone2: ScoreSpot
){
    private val scoringSpots = arrayOfNulls<ScoreSpot>(3)
    init {
        scoringSpots[0] = Cone1
        scoringSpots[1] = Cube
        scoringSpots[2] = Cone2
    }
    /**
     * @param pose Pose to find distance from
     * @return Y distance from the pose */
    fun distance(pose: Pose2d): Double{
        return Cube.RobotPosition.ydistance(pose)
    }
    /**
     * @param coordinate Coordinate to find distance from
     * @return Y distance from the pose */
    fun distance(coordinate: Coordinate): Double{
        return Cube.RobotPosition.ydistance(coordinate)
    }

    /**
     * @return Closest scoring spot to the Pose2d given
     * @param pose Pose to compare scoring spot locations to
     */
    fun getClosestSpot(pose: Pose2d): ScoreSpot{
        var closestSpot = Cone1
        scoringSpots.forEach {
            it ?: run { return@forEach }
            if (it.distance(pose) < closestSpot.distance(pose)) { closestSpot = it }
        }
        return closestSpot
    }

    /**
     * @return Grid object reflected over the give vertical line
     * @param x Vertical line to reflect over
     */
    fun reflectHorizontally(x: DistanceUnit): Grid{
        return Grid(
                Cone1.reflectHorizontally(x),
                Cube.reflectHorizontally(x),
                Cone2.reflectHorizontally(x)
        )
    }
}

/**
 * Used to store the locations the robot needs to be in to score game pieces
 * @property Grid1 Bottom grid object
 * @property Grid2 Middle grid object
 * @property Grid3 Top grid object
 * @property gridWall Line along which the grid starts.
 */
data class ScoringLocations(
        val Grid1: Grid,
        val Grid2: Grid,
        val Grid3: Grid,
        val gridWall : Line,
) {
    val grids = arrayOfNulls<Grid>(3)

    init {
        grids[0] = Grid1
        grids[1] = Grid2
        grids[2] = Grid3
    }
    /**
     * @param coords Coordinates to use to find closest grid
     * @return Grid object coordinate is closest to
     */
    fun getClosestGrid(coords: Coordinate): Grid {
        var closestGrid = Grid1
        grids.forEach {
            it ?: run { return@forEach }
            if (it.distance(coords) < closestGrid.distance(coords)) {
                closestGrid = it
            }
        }
        return closestGrid
    }

    /**
     * @param pose Pose2d to use to find closest grid
     * @return Grid object pose is closest to
     */
    fun getClosestGrid(pose: Pose2d): Grid {
        return getClosestGrid(Coordinate(pose))
    }

    /**
     * @return Returns the grid object the pose is facing
     * @param pose Pose2d to use to find the grid
     * @param gridWallDirection The rotation that would be perpendicular to the grid wall.
     */
    fun getFacedGrid(pose: Pose2d, gridWallDirection: Double): Grid {
        val gridWallIntersection = gridWall.intersection(pose)
                ?: when (pose.rotation.degrees) {
                    in -gridWallDirection..gridWallDirection -> return Grid3
                    else -> return Grid1
                }
        return getClosestGrid(gridWallIntersection)
    }

    /**
     * @param pose Pose2d of the robot
     * @return The closest ScoreSpot the robot
     */
    fun getClosestScoringSpot(pose: Pose2d): ScoreSpot {
        return getClosestGrid(pose).getClosestSpot(pose)
    }

    /**
     * @param x Line to reflect over.
     * @return ScoringLocations object with the x positions reflected over the given line
     */
    fun reflectHorizontally(x: DistanceUnit): ScoringLocations {
        return ScoringLocations(
                Grid1.reflectHorizontally(x),
                Grid2.reflectHorizontally(x),
                Grid3.reflectHorizontally(x),
                gridWall.reflectHorizontally(x)
        )
    }
}

//robot_length = 0.93
val robot_scoring_pos = 1.7.m
val blueScoring = ScoringLocations(
        Grid1 = Grid(
                ScoreSpot(Coordinate(robot_scoring_pos, 1.682.ft), ScoringType.CONE),
                ScoreSpot(Coordinate(robot_scoring_pos, 3.518.ft), ScoringType.CUBE),
                ScoreSpot(Coordinate(robot_scoring_pos, 5.349.ft), ScoringType.CONE)
        ),
        Grid2 = Grid(
                ScoreSpot(Coordinate(robot_scoring_pos, 7.182.ft), ScoringType.CONE),
                ScoreSpot(Coordinate(robot_scoring_pos, 9.018.ft), ScoringType.CUBE),
                ScoreSpot(Coordinate(robot_scoring_pos, 10.849.ft), ScoringType.CONE)
        ),
        Grid3 = Grid(
                ScoreSpot(Coordinate(robot_scoring_pos, 12.682.ft), ScoringType.CONE),
                ScoreSpot(Coordinate(robot_scoring_pos, 14.518.ft), ScoringType.CUBE),
                ScoreSpot(Coordinate(robot_scoring_pos, 16.349.ft), ScoringType.CONE)
        ),
        gridWall = Line(
                Coordinate(4.5.ft,0.0.ft),
                Coordinate(4.5.ft,18.0.ft),
        )
)
/**
 * Used to store Alliance-specific locations
 * @property community The community zone on the field, stored as a Polygon
 * @property chargingDock The alliance's charging dock, stored as a Rectangle
 * @property loadingBay The alliance's loading bay, stored as a Polygon
 * @property scoring The alliance's scoring area, containing information about the grids, and grid wall
 * */
data class Map(
        val rotation: Double,
        val community : Polygon,
          val chargingDock : Rectangle,
          val loadingBay : Polygon,
        val scoring: ScoringLocations
        ) {
    /**
     * @return A string correlating to the zone the robot is in (Community, charging dock, loading bay, other)
     * @param position The robots position
     * @author Ozy King
     */
    fun get_location(position: Coordinate): String{
        return when {
            community.contains(position)    -> "community"
            chargingDock.contains(position) -> "charging dock"
            loadingBay.contains(position)   -> "loading bay"
            else                            -> "other"
        }
    }
    fun facing_community(pose: Pose2d): Boolean{
        return (pose.rotation.degrees < rotation +90 && pose.rotation.degrees > rotation - 90)
    }
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


/** Map of the blue alliance side
 * @see Field
 * */
val blueTeam = Map(
        rotation = -90.0,
        community = blueLoadingBay,
        chargingDock = blueChargeStation,
        loadingBay = redLoadingBay.reflectHorizontally(8.2296.m),
        scoring = blueScoring
).apply { println("BLUE FIELD: $this") }
/** Map of the red alliance side
 * @see Field
 * */
val redTeam = Map(
        rotation = 90.0,
        community = blueLoadingBay.reflectHorizontally(8.2296.m),
        chargingDock = blueChargeStation.reflectHorizontally(8.2296.m),
        loadingBay = redLoadingBay,
        scoring =  blueScoring.reflectHorizontally(8.2296.m)
).apply { println("RED FIELD: $this") }
/** Map of the field based off of the Driverstation alliance
 * @author Ozy King
 * @property map Includes your alliances specific zones
 * @property red_team Red alliance specific zones
 * @property blue_team Blue alliance specific zones
 * @author Ozy King
 * */
object Field : SubsystemBase() {
    var teamColor: DriverStation.Alliance = DriverStation.getAlliance()
    lateinit var map : Map
    val red_team = redTeam
    val blue_team = blueTeam
    fun initialize() {
        map = if (teamColor == DriverStation.Alliance.Blue) {
            blueTeam
        } else {
            redTeam
        }
    }

    /**
     * Debugging tool
     * @param position The robots position
     * @return A string correlating to the robots position on the field
     * @author Ozy King
     */
    fun get_area(position: Coordinate): String{
        val red = red_team.get_location(position)
        if (red != "other") return "Red " + red
        val blue = blue_team.get_location(position)
        if (blue != "other") return "Blue " + blue
        return "Other"
    }
}
