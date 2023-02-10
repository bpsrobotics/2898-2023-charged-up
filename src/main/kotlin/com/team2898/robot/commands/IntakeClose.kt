package com.team2898.robot.commands

import com.team2898.robot.subsystems.Intake
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase
class IntakeClose : CommandBase() {
    private val timer = Timer()
    override fun initialize() {
        Intake.intakeClose()
        timer.start()
    }

    override fun isFinished(): Boolean {
        return (timer.hasElapsed(0.5))
    }
}
