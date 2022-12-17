package com.team2898.robot.commands

import edu.wpi.first.wpilibj.smartdashboard.Field2d
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import edu.wpi.first.wpilibj2.command.WaitCommand

class RobotAuto : CommandBase() {
    lateinit var autoCommandGroup: Command
    override fun initialize() {
        autoCommandGroup = SequentialCommandGroup (

            ParallelCommandGroup(
                //Runs the feeder
                TubeCountFeederAuto(),
                // Moves the robot to the drop-off zone
                SequentialCommandGroup(
                    DriveForward(1.0),
                    HomingVision()
                )
            ),
            //Deposits the bunny at the drop-off zone
            AutoOutake()
        )
        autoCommandGroup.schedule()
    }

    override fun isFinished(): Boolean {
        return autoCommandGroup.isFinished
    }

    override fun end(interrupted: Boolean) {
        autoCommandGroup.end(interrupted)
    }
}
