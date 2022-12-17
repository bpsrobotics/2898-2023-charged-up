package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import edu.wpi.first.wpilibj.smartdashboard.Field2d
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import edu.wpi.first.wpilibj2.command.WaitCommand

class RobotAuto : CommandBase() {
    private lateinit var autoCommandGroup: Command
    override fun initialize() {
        autoCommandGroup = SequentialCommandGroup (

            ParallelRaceGroup(
                //Runs the feeder
                TubeCountFeederAuto(),
                // Moves the robot forward to pick up the bunny
                DriveForward(0.5, `M/s`(1.0))
            ),
            // Drive to the dropoff
            HomingVision(),
            // Deposits the bunny at the drop-off zone
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
