package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.bpsrobotics.engine.utils.Sugar.clamp
import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj2.command.CommandBase
import com.team2898.robot.subsystems.Vision
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import kotlin.math.atan2
import kotlin.math.log

/** Robot moves within 1 meter of target, assuming Apriltag is within visual range of camera */
class HomingVision : CommandBase() {
    val timer = Timer()
    override fun initialize() {
        timer.reset()
        timer.start()
    }
    override fun execute() {
        // Makes sure the robot is not closer than 2 meters
        if (Vision.magnitude2D > 2) {
            // Throttle reduces as it gets closer to target
            val throttle = log(Vision.magnitude2D, 5.0).clamp(0.5,1.0)
            val speeds = DifferentialDrive.curvatureDriveIK(throttle, atan2(Vision.xdist, Vision.zdist) / 3.0, true)
            Drivetrain.stupidDrive(`M/s`(speeds.left * -1), `M/s`(speeds.right * -1))
        } else {
            Drivetrain.rawDrive(0.0,0.0) // Full stop
        }
        println(Vision.timeSinceLastFix)
    }

    override fun isFinished(): Boolean {
        if (!(timer.hasElapsed(1.0))) { return false }
        println("dist: ${Vision.magnitude2D}")
        return (Vision.inCameraRange) || (Vision.magnitude2D <= 2) //Checks if it is closer than 2 meters
    }

    override fun end(interrupted: Boolean) {
        println("STOPPING ================================================================")
        Drivetrain.mode = Drivetrain.Mode.OPEN_LOOP
        Drivetrain.rawDrive(0.0,0.0) // Full stop
    }

}