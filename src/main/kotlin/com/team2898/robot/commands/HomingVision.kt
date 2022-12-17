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
//        println("HOMING STARTING")
    }
    override fun execute() {
        //Turns to find tag if not in cameras FOV
        if (!(Vision.inCameraRange)) {
//            println("TURNING")
            Drivetrain.stupidDrive(`M/s`(0.5), `M/s`(-0.5))
            return
        }
        // Makes sure the robot is not closer than 2 meters
        if (Vision.magnitude2D > 2) {
            // Throttle reduces as it gets closer to target
            val speedMultiplier = (log(Vision.magnitude2D, 10.0)*3).clamp(0.5,3.0)
            val speeds = DifferentialDrive.curvatureDriveIK(1.0, atan2(Vision.xdist, Vision.zdist) / 3.0, true)
            Drivetrain.stupidDrive(`M/s`(speeds.left * -speedMultiplier), `M/s`(speeds.right * -speedMultiplier))
        } else {
            Drivetrain.rawDrive(0.0,0.0) // Full stop
        }
//        println(Vision.timeSinceLastFix)
    }

    override fun isFinished(): Boolean {
        println("checking if finished")
        if (!(timer.hasElapsed(3.0))) { println("in grace period, not stopping"); return false }
//        println("dist: ${Vision.magnitude2D}")
        return  (Vision.magnitude2D <= 2) //Checks if it is closer than 2 meters
    }

    override fun end(interrupted: Boolean) {
        println("STOPPING ================================================================")
        Drivetrain.mode = Drivetrain.Mode.OPEN_LOOP
        Drivetrain.rawDrive(0.0,0.0) // Full stop
    }

}