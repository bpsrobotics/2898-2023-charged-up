package com.team2898.robot.commands

import edu.wpi.first.wpilibj.smartdashboard.Field2d
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import edu.wpi.first.wpilibj2.command.WaitCommand

class RobotAuto : CommandBase() {
    lateinit var moveCommandGroup: Command
    override fun initialize() {

        moveCommandGroup = SequentialCommandGroup(
            TestAuto(),
            HomingVision()
        )
        moveCommandGroup.schedule()
    }

    override fun isFinished(): Boolean {
        return false
    }

    override fun end(interrupted: Boolean) {
        moveCommandGroup.end(interrupted)
    }
}
