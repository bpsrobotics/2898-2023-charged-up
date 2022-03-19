package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.TargetAlignUtils
import com.bpsrobotics.engine.utils.TrajectoryUtils.centerField
import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.Constants
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import com.team2898.robot.subsystems.Vision
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.atan2

class TargetAlign : CommandBase() {
    val controller = PIDController(1.0, 0.0, 0.0)
    val distanceController = PIDController(0.0, 0.0, 0.0)
    lateinit var rotation: Rotation2d

    init {
        addRequirements(Drivetrain)
    }

    override fun initialize() {
        controller.setpoint = 0.0
    }

    override fun execute() {
        if(!TargetAlignUtils.isAligned){
            val translation = Odometry.pose.translation.minus(centerField.translation)
            rotation = Rotation2d(atan2(translation.y, translation.x))
            val speeds = if (Vision.lastUpdated.get() < 0.25) {
                controller.setpoint = 0.0
                controller.calculate(Vision.angle.radiansValue())
            } else {
                controller.setpoint = rotation.radians
                controller.calculate(Odometry.pose.rotation.radians)
            }
            Drivetrain.stupidDrive(`M/s`(-speeds), `M/s`(speeds))
        }else{
            if (Vision.lastUpdated.get() < 0.25){
                distanceController.setpoint = Constants.SHOOT_DISTANCE
                Drivetrain.stupidDrive(`M/s`(distanceController.calculate(Vision.distance.value)), `M/s`(distanceController.calculate(Vision.distance.value)))
            }else{
                val translation = Odometry.pose.translation.minus(centerField.translation)
                distanceController.setpoint = Constants.SHOOT_DISTANCE
                Drivetrain.stupidDrive(`M/s`(distanceController.calculate(translation.getDistance(Translation2d()))), `M/s`(distanceController.calculate(translation.getDistance(Translation2d()))))
            }
        }
    }

    override fun isFinished(): Boolean {
        return TargetAlignUtils.isAligned && TargetAlignUtils.isCorrectDistance
    }
}