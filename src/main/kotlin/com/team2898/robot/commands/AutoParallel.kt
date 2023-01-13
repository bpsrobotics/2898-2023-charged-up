package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.Constants.AUTOBALANCE_KP
import com.team2898.robot.Constants.AUTOBALANCE_KI
import com.team2898.robot.Constants.AUTOBALANCE_KD
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry.NavxHolder.navx
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.absoluteValue

class AutoParallel : CommandBase() {
    private var yaw = navx.yaw.toDouble()
    private val pid = PIDController(AUTOBALANCE_KP, AUTOBALANCE_KI, AUTOBALANCE_KD)


    override fun initialize() {
        navx.zeroYaw()
    }

    override fun execute() {
        val yawPower = pid.calculate(yaw)

        //NOTE: Add an if statement depending on which direction is faster to rotate in
        Drivetrain.stupidDrive(`M/s`(yawPower),`M/s`(-yawPower))
    }

    override fun isFinished(): Boolean {
        return (navx.yaw.absoluteValue) > 3
    }

}

