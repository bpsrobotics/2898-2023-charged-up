package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.TrajectoryUtils.invertTrajectory
import com.bpsrobotics.engine.utils.`M/s`
import com.pathplanner.lib.PathPlanner
import com.team2898.robot.commands.auto.FireLowBall
import com.team2898.robot.commands.auto.FollowPath
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.Field2d
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

class HideAuto : CommandBase() {
    lateinit var moveCommandGroup: Command
    private val field = Field2d()
    val timer = Timer()

    override fun initialize() {
        var firstPath: Trajectory = PathPlanner.loadPath("HideAuto", 5.0, 1.5) // TODO: Max Viable Speed
        val alliance = DriverStation.Alliance.Blue
//
//        if (alliance == DriverStation.Alliance.Red) {
//            firstPath = invertTrajectory(firstPath)
//        }
        field.getObject("traj").setTrajectory(firstPath)
        field.robotPose = firstPath.initialPose
        SmartDashboard.putData(field)

        moveCommandGroup = SequentialCommandGroup(
            FireLowBall(1),
        //    FollowPath(firstPath)
        )

        moveCommandGroup.schedule()

        timer.reset()
        timer.start()
    }

    override fun execute() {
        field.robotPose = Odometry.pose
        SmartDashboard.putData(field)

        if (timer.hasElapsed(1.0)) {
            Drivetrain.rawDrive(0.1, 0.1)
        }
    }

    override fun isFinished(): Boolean {
        return false
    }

    override fun end(interrupted: Boolean) {
        moveCommandGroup.end(interrupted)
    }
}
