package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.bpsrobotics.engine.utils.MetersPerSecondSquared
import com.pathplanner.lib.PathPlanner
import com.pathplanner.lib.PathPlannerTrajectory
import com.team2898.robot.Constants.DRIVETRAIN_MAX_ACCELERATION
import com.team2898.robot.Constants.DRIVETRAIN_MAX_VELOCITY
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.DriverStation.Alliance
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase

/**
 * A command that follows a path until it is finished or times out.
 *
 * @param filename The filename of the path to load
 * @author Max Leibowitz
 */
class PathFollowCommand(val filename: String, private val resetOdometry: Boolean, maxVel: `M/s` = DRIVETRAIN_MAX_VELOCITY, maxAccel: MetersPerSecondSquared = DRIVETRAIN_MAX_ACCELERATION) : CommandBase() {
    private var path = PathPlanner.loadPath(filename, maxVel.value, maxAccel.value)!!
    private val time = Timer()

    init {
//        val alliance = DriverStation.getAlliance()
        val alliance = Alliance.Red
        path = PathPlannerTrajectory.transformTrajectoryForAlliance(path, alliance)
    }

    override fun initialize() {
        if (resetOdometry) {
            Odometry.reset(path.initialPose)
        }
        Drivetrain.follow(path)
        time.start()
    }

//    override fun execute() {
//        println("Following $filename path --------------------------------------------------------------")
//    }

    override fun end(interrupted: Boolean) {
        Drivetrain.mode = Drivetrain.Mode.OPEN_LOOP
        Drivetrain.rawDrive(0.0, 0.0)
    }

    override fun isFinished(): Boolean {
        return Drivetrain.mode != Drivetrain.Mode.CLOSED_LOOP || time.hasElapsed(path.totalTimeSeconds * 1.1)
    }
}
