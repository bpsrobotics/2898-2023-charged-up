package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.TrajectoryUtils.endPose
import com.bpsrobotics.engine.utils.TrajectoryUtils.invertTrajectory
import com.pathplanner.lib.PathPlanner
import com.team2898.robot.commands.auto.FireLowBall
import com.team2898.robot.commands.auto.FollowPath
import com.team2898.robot.commands.auto.RunIntake
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Transform2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.Field2d
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import kotlin.math.PI

class LeadingAuto : CommandBase() {
    lateinit var moveCommandGroup: SequentialCommandGroup

    override fun initialize() {
        var firstPath: Trajectory = PathPlanner.loadPath("LeadingAuto", 8.0, 1.5) // TODO: Max Viable Speed
        val alliance = DriverStation.getAlliance()

        if (alliance == DriverStation.Alliance.Red) {
            firstPath = invertTrajectory(firstPath)
        }

        val field = Field2d()
        field.getObject("traj").setTrajectory(firstPath)
        field.robotPose = firstPath.initialPose
        SmartDashboard.putData(field)

        moveCommandGroup = SequentialCommandGroup(
            FireLowBall(1, firstPath.initialPose),
            ParallelCommandGroup(
                FollowPath(firstPath, true),
                RunIntake(
                    when (alliance) {
                        DriverStation.Alliance.Red -> RunIntake.Ball.RED_2
                        else -> RunIntake.Ball.BLUE_2
                    }
                ),
                RunIntake(
                    when (alliance) {
                        DriverStation.Alliance.Red -> RunIntake.Ball.RED_TERMINAL
                        else -> RunIntake.Ball.BLUE_TERMINAL
                    }
                ),
            ),
            FireLowBall(2, firstPath.endPose)
        )

        moveCommandGroup.schedule()
    }

    override fun execute() {

    }

    override fun isFinished(): Boolean {
        return false
    }

    override fun end(interrupted: Boolean) {
        moveCommandGroup.end(interrupted)
    }
}
