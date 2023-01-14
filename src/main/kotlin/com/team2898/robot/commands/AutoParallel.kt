package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.Constants.AUTOBALANCE_KP
import com.team2898.robot.Constants.AUTOBALANCE_KI
import com.team2898.robot.Constants.AUTOBALANCE_KD
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import com.team2898.robot.subsystems.Odometry.NavxHolder.navx
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.absoluteValue

class AutoParallel : CommandBase() {
    private var yaw = Odometry.pose.rotation.degrees
    private val pid = PIDController(AUTOBALANCE_KP, AUTOBALANCE_KI, AUTOBALANCE_KD)
    private var teamColor = DriverStation.getAlliance()


    override fun execute() {
        yaw = Odometry.pose.rotation.degrees
        val yawPower = pid.calculate(yaw)
        teamColor = DriverStation.getAlliance()

        //TODO: Figure out which direction is negative rotation, and adjust rotation direction accordingly
        //TODO: Implement proper rotation depending on whether or not the bot is on the left or right of charging station and the team it's one
        if (teamColor == DriverStation.Alliance.Blue) {
            //If (Right of charging station)
            if (yaw < 180) {
                Drivetrain.stupidDrive(`M/s`(yawPower),`M/s`(-yawPower))
            }
            else if (yaw > 180) {
                Drivetrain.stupidDrive(`M/s`(-yawPower),`M/s`(yawPower))
            }
            //else if (left of charging station)

        }
        else if (teamColor == DriverStation.Alliance.Red) {
            //If (Right of charing station)
            println("Team color is $teamColor")
        }

    }

    override fun isFinished(): Boolean {
        //TODO: Make it incorporate the position on the map
        if (teamColor == DriverStation.Alliance.Blue) {
            println("yaw absolute is ${yaw.absoluteValue}")
        }

        return (yaw.absoluteValue) > 3
    }

}

