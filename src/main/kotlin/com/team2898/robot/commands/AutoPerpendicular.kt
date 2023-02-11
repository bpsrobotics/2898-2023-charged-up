package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.Constants.AUTOBALANCE_KP
import com.team2898.robot.Constants.AUTOBALANCE_KI
import com.team2898.robot.Constants.AUTOBALANCE_KD
import com.team2898.robot.Field
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj2.command.CommandBase
import com.team2898.robot.subsystems.Odometry.pose

/**
 * Turns to face the charge station
 * @author Ori
 */
class AutoPerpendicular : CommandBase() {
    private var yaw = Odometry.pose.rotation.degrees
    private val pid = PIDController(AUTOBALANCE_KP, AUTOBALANCE_KI, AUTOBALANCE_KD)
    private var teamColor = DriverStation.getAlliance()


    override fun execute() {
        yaw = Odometry.pose.rotation.degrees
        val yawPower = pid.calculate(yaw)
        teamColor = DriverStation.getAlliance()

        //This bool is literally just for testing
        val testing = true

        //TODO: Figure out which direction is negative rotation, and adjust rotation direction accordingly
        if (!Field.map.chargingDock.containsY(pose.y)) return

        if (Field.map.chargingDock.distToCenter(pose).x < 0) {
            if (yaw > 270 || yaw < 90) {
                Drivetrain.stupidDrive(`M/s`(-yawPower), `M/s`(yawPower))
            } else /* if (yaw in (90.0..270.0)) */ {
                Drivetrain.stupidDrive(`M/s`(yawPower), `M/s`(-yawPower))
            }
        } else /* On the other side of the charge station*/ {
            if (yaw < 90 || yaw > 270) {
                Drivetrain.stupidDrive(`M/s`(yawPower), `M/s`(-yawPower))
            } else /* if (yaw in (90.0..270.0)) */  {
                Drivetrain.stupidDrive(`M/s`(-yawPower), `M/s`(yawPower))
            }
        }

    }

    override fun isFinished(): Boolean {
        return (Field.map.chargingDock.contains(pose))
    }

}

