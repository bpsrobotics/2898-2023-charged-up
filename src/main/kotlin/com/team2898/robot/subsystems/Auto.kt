package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.MetersPerSecond
import com.bpsrobotics.engine.utils.m
import com.team2898.robot.Constants
import com.team2898.robot.Constants.DRIVETRAIN_KS
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.absoluteValue
import kotlin.math.max

class Auto : CommandBase() {
    override fun initialize() {
        Odometry.reset()
        val t = Drivetrain.trajectoryMaker.builder()
            .start(Pose2d())
            .point(1.m,   0.m)
            .point(1.5.m, 0.m)
            .point(1.m,   1.m)
            .point(0.5.m, 0.m)
            .point(0.m,   0.m)
            .end(Pose2d(-0.1, 0.0, Rotation2d.fromDegrees(180.0)))
            .build()

        Drivetrain.follow(t)
    }

    override fun execute() {
//        Drivetrain.stupidDrive(MetersPerSecond(-1.0), MetersPerSecond(-1.0))
//        Drivetrain.rawDrive(DRIVETRAIN_KS.value, DRIVETRAIN_KS.value)
        if (max(Drivetrain.leftEncoder.rate.absoluteValue, Drivetrain.rightEncoder.rate.absoluteValue) > 3.0) {
            Drivetrain.mode = Drivetrain.Mode.OPEN_LOOP
            println("STOPPING")
        }
    }
}
