package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.TrajectoryUtils.centerField
import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.abs
import kotlin.math.atan2

class TargetAlign : CommandBase() {
    val controller = PIDController(1.0, 0.0, 0.0)
    lateinit var rotation: Rotation2d

    init {
        addRequirements(Drivetrain)
    }

    override fun initialize() {
        val translation = Odometry.pose.translation.minus(centerField.translation)
        rotation = Rotation2d(atan2(translation.y, translation.x))
        controller.setpoint = rotation.radians
    }

    override fun execute() {
        val speeds = controller.calculate(Odometry.pose.rotation.radians)
        Drivetrain.stupidDrive(`M/s`(speeds), `M/s`(-speeds))
    }

    override fun isFinished(): Boolean {
        return abs(rotation.radians - Odometry.pose.rotation.radians) < 0.02
    }
}