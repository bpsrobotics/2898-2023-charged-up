package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.deg
import com.bpsrobotics.engine.utils.m
import com.pathplanner.lib.PathPlanner
import edu.wpi.first.wpilibj2.command.CommandBase

class Auto : CommandBase() {
    override fun initialize() {
//        val t = Drivetrain.trajectoryMaker.builder()
//            .start(Pose2d())
//            .point(Translation2d(1.0, 0.0))
//            .point(Translation2d(1.5, 0.5))
//            .point(Translation2d(1.0, 1.0))
//            .point(Translation2d(0.5, 0.0))
//            .point(Translation2d(0.0, 0.0))
//            .end(Pose2d(-0.1, 0.0, Rotation2d.fromDegrees(180.0)))
//            .build()
//        val trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve("PathWeaver/output/Test.wpilib.json")
//        val t = TrajectoryUtil.fromPathweaverJson(trajectoryPath)
//
//        Drivetrain.follow(t)

        val path = PathPlanner.loadPath("HideAuto", 0.5, 0.2)
        val pathInitialPoseProvider = path.initialPose


        Odometry.reset(pathInitialPoseProvider.x.m, pathInitialPoseProvider.y.m, pathInitialPoseProvider.rotation.degrees.deg)
        Drivetrain.follow(path)
    }

    override fun execute() {
//        Drivetrain.stupidDrive(MetersPerSecond(2.0), MetersPerSecond(2.0))
//        Drivetrain.rawDrive(DRIVETRAIN_KS.value, DRIVETRAIN_KS.value)
//        if (max(Drivetrain.leftEncoder.rate.absoluteValue, Drivetrain.rightEncoder.rate.absoluteValue) > 3.0) {
//            Drivetrain.mode = Drivetrain.Mode.OPEN_LOOP
//            println("STOPPING")
//        }
    }
}
