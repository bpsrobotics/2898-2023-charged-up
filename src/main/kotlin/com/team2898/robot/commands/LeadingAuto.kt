package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.TrajectoryUtils.invertTrajectory
import com.pathplanner.lib.PathPlanner
import com.team2898.robot.commands.auto.FireLowBall
import com.team2898.robot.commands.auto.FollowPath
import com.team2898.robot.commands.auto.RunIntake
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.Field2d
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.*

class LeadingAuto : CommandBase() {
    lateinit var moveCommandGroup: Command
    private val field = Field2d()

    override fun initialize() {
        var firstPath: Trajectory = PathPlanner.loadPath("LeadingAutoP1", 8.0, 1.5) // TODO: Max Viable Speed
        var secondPath: Trajectory = PathPlanner.loadPath("LeadingAutoP2", 8.0, 1.5) // TODO: Max Viable Speed
        val alliance = DriverStation.Alliance.Blue

//        if (alliance == DriverStation.Alliance.Red) {
//            firstPath = invertTrajectory(firstPath)
//            secondPath = invertTrajectory(secondPath)
//        }
        field.getObject("traj").setTrajectory(firstPath.concatenate(secondPath))
        field.robotPose = firstPath.initialPose
        SmartDashboard.putData(field)

        moveCommandGroup = SequentialCommandGroup(
            FireLowBall(1),
            ParallelDeadlineGroup(
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
            FireLowBall(2),
            ParallelCommandGroup(
                FollowPath(secondPath),
                RunIntake(
                    when (alliance) {
                        DriverStation.Alliance.Red -> RunIntake.Ball.RED_1
                        else -> RunIntake.Ball.BLUE_3
                    }
                )
            )
        )

        moveCommandGroup.schedule()
    }

    override fun execute() {
        field.robotPose = Odometry.pose
        SmartDashboard.putData(field)
    }

    override fun isFinished(): Boolean {
        return false
    }

    override fun end(interrupted: Boolean) {
        moveCommandGroup.end(interrupted)
    }
}
