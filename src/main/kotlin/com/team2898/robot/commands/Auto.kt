package com.team2898.robot.commands

import edu.wpi.first.wpilibj2.command.CommandBase
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import kotlin.math.absoluteValue
import kotlin.math.max

class Auto : CommandBase() {
    override fun initialize() {
        val t = Drivetrain.trajectoryMaker.builder()
            .start(Pose2d())
//            .point(0.5.m, 0.m)

//            .point(1.m, 1.m)
//            .point(0.m, 2.m)
//            .point((-1).m, 1.m)
//            .point((-0.5).m, (0.5).m)
//            .point(0.m, 0.m)
//
//            .point(1.m, 1.m)
//            .point(0.m, 2.m)
//            .point((-1).m, 1.m)
//            .point((-0.5).m, (0.5).m)

//            .point(1.m, 1.m)
//            .point(0.m, 2.m)
//            .point((-1).m, 1.m)
//
//            .point(0.m, 0.m)
//
//            .point(1.m, (-1).m)
//            .point(0.m, (-2).m)
//            .point((-1).m, (-1).m)
//
//            .point(0.m, 0.m)
//
//            .point(1.m, 1.m)
//            .point(0.m, 2.m)
//            .point((-1).m, 1.m)
//
//            .point(0.m, 0.m)
//
//            .point(1.m, (-1).m)
//            .point(0.m, (-2).m)
//            .point((-1).m, (-1).m)
//
//            .point(0.m, 0.m)
//
//            .point(1.m, 1.m)
//            .point(0.m, 2.m)
//            .point((-1).m, 1.m)
//
//            .point(0.m, 0.m)
//
//            .point(1.m, (-1).m)
//            .point(0.m, (-2).m)
//            .point((-1).m, (-1).m)
//
//            .point(0.m, 0.m)
//
//            .point(1.m, 1.m)
//            .point(0.m, 2.m)
//            .point((-1).m, 1.m)
//
//            .point(0.m, 0.m)
//
//            .point(1.m, (-1).m)
//            .point(0.m, (-2).m)
//            .point((-1).m, (-1).m)

            .point(2.0, -2.0)
            .point(0.0, -4.0)
            .point(-2.0, -2.0)

            .point(0.0, 0.0)

            .point(2.0, 2.0)
            .point(0.0, 4.0)
            .point(-2.0, 2.0)


            .point(0.0, 0.0)


            .point(2.0, -2.0)
            .point(0.0, -4.0)
            .point(-2.0, -2.0)

            .point(0.0, 0.0)

            .point(2.0, 2.0)
            .point(0.0, 4.0)
            .point(-2.0, 2.0)

            .end(Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)))
            .build()
        Odometry.reset()
        println()

        Drivetrain.follow(t)
    }

    override fun execute() {
//        Drivetrain.stupidDrive(MetersPerSecond(-1.0), MetersPerSecond(-1.0))
//        Drivetrain.rawDrive(DRIVETRAIN_KS.value + DRIVETRAIN_KV, DRIVETRAIN_KS.value + DRIVETRAIN_KV)
        if (max(Drivetrain.leftEncoder.rate.absoluteValue, Drivetrain.rightEncoder.rate.absoluteValue) > 5.0) {
            Drivetrain.mode = Drivetrain.Mode.OPEN_LOOP
            println("STOPPING")
        }
    }
}
