package com.team2898.robot.commands

import com.pathplanner.lib.PathPlanner
import com.team2898.robot.commands.auto.FireHighBall
import com.team2898.robot.commands.auto.FollowPath
import com.team2898.robot.commands.auto.RunIntake
import com.team2898.robot.subsystems.Intake
import com.team2898.robot.subsystems.Odometry
import com.team2898.robot.subsystems.Shooter
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.Field2d
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.*

class NonLeadingHighAuto : CommandBase() {
    lateinit var moveCommandGroup: Command
    private val field = Field2d()

    override fun initialize() {
        var firstPath: Trajectory = PathPlanner.loadPath("NonLeadingHighAuto", 5.0, 1.5) // DO NOT CHANGE VEL/ACCEL
        val alliance = DriverStation.Alliance.Blue

//        if (alliance == DriverStation.Alliance.Red) {
//            firstPath = invertTrajectory(firstPath)
//        }

        field.getObject("traj").setTrajectory(firstPath)
        field.robotPose = firstPath.initialPose
        SmartDashboard.putData(field)

        moveCommandGroup = SequentialCommandGroup(
            ParallelDeadlineGroup(
                SequentialCommandGroup(
                    WaitCommand(1.5),
                    FollowPath(firstPath, true),
                ),
                FireHighBall(Shooter.ShooterPowers(0.24, 0.75)),
                RunIntake(
                    when (alliance) {
                        DriverStation.Alliance.Red -> RunIntake.Ball.RED_3
                        else -> RunIntake.Ball.BLUE_1
                    }
                )
            ),
            InstantCommand(Intake::closeIntake),
            WaitCommand(0.2),
            InstantCommand(Intake::stopIntake),
            FireHighBall(Shooter.ShooterPowers(0.08, 0.7))
        )

        /*
        testing data

        first shot: 0.25, 0.76
        second shot: 0.08, 0.7
        1 - both in
        2 - both in
        3 - both in
        4 - first went over
        */

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
