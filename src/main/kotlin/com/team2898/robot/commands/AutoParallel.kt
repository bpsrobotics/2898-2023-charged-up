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
        if (pose.x !in (Field.map.chargingDock.x1..Field.map.chargingDock.x2)) {
                if (teamColor == DriverStation.Alliance.Blue || testing) {
                    if (Field.map.chargingDock.x2 < pose.x) {
                        if ((yaw - 270) < 0.5) {
                            Drivetrain.stupidDrive(`M/s`(2.0), `M/s`(2.0))
                        } else if (yaw < 180) {
                            Drivetrain.stupidDrive(`M/s`(yawPower), `M/s`(-yawPower))
                        } else if (yaw > 180) {
                            Drivetrain.stupidDrive(`M/s`(-yawPower), `M/s`(yawPower))
                        }
                    } else if (Field.map.chargingDock.x1 > pose.x) {
                        if ((yaw - 90) < 0.5) {
                            Drivetrain.stupidDrive(`M/s`(2.0), `M/s`(2.0))
                        } else if (yaw < 180) {
                            Drivetrain.stupidDrive(`M/s`(yawPower), `M/s`(-yawPower))
                        } else if (yaw > 180) {
                            Drivetrain.stupidDrive(`M/s`(-yawPower), `M/s`(yawPower))
                        }
                    }

                } else if (teamColor == DriverStation.Alliance.Red) {
                    if (pose.x < Field.map.chargingDock.x1) {
                        if ((yaw - 90) < 0.5) {
                            Drivetrain.stupidDrive(`M/s`(2.0), `M/s`(2.0))
                        } else if (yaw < 180) {
                            Drivetrain.stupidDrive(`M/s`(yawPower), `M/s`(-yawPower))
                        } else if (yaw > 180) {
                            Drivetrain.stupidDrive(`M/s`(-yawPower), `M/s`(yawPower))
                        }
                    } else if (Field.map.chargingDock.x2 < pose.x) {
                        if ((yaw - 270) < 0.5) {
                            Drivetrain.stupidDrive(`M/s`(2.0), `M/s`(2.0))
                        } else if (yaw < 180) {
                            Drivetrain.stupidDrive(`M/s`(yawPower), `M/s`(-yawPower))
                        } else if (yaw > 180) {
                            Drivetrain.stupidDrive(`M/s`(-yawPower), `M/s`(yawPower))
                        }
                    }
                }
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

