package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.Constants.AUTOBALANCE_KP
import com.team2898.robot.Constants.AUTOBALANCE_KI
import com.team2898.robot.Constants.AUTOBALANCE_KD
import com.team2898.robot.Field
import com.team2898.robot.blueTeam
import com.team2898.robot.redTeam
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.absoluteValue
import com.team2898.robot.subsystems.Odometry.pose

class AutoParallel : CommandBase() {
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
        if (pose.y in (Field.map.chargingDock.y1..Field.map.chargingDock.y2)) {
            if (pose.x > Field.map.chargingDock.x2) {
                if (yaw > 270 || yaw < 90) {
                    Drivetrain.stupidDrive(`M/s`(-yawPower), `M/s`(yawPower))
                } else if (yaw in (90.0..270.0)) {
                    Drivetrain.stupidDrive(`M/s`(yawPower), `M/s`(-yawPower))
                }
            } else if (pose.x < Field.map.chargingDock.x1) {
                if (yaw < 90 || yaw > 270) {
                    Drivetrain.stupidDrive(`M/s`(yawPower), `M/s`(-yawPower))
                } else if (yaw in (90.0..270.0)) {
                    Drivetrain.stupidDrive(`M/s`(-yawPower), `M/s`(yawPower))
                }
            }
        }
    }

    override fun isFinished(): Boolean {
        return (Field.map.chargingDock.contains(pose))
    }

}

