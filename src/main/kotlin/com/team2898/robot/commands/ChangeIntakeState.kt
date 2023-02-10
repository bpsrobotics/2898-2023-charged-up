package com.team2898.robot.commands

import com.team2898.robot.subsystems.Intake
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase

/**
 * Opens or closes the intake
 * @author Ori
 */
class ChangeIntakeState(private val mode: IntakeState) : CommandBase() {
    private val timer = Timer()
    override fun initialize() {
        if (mode == IntakeState.INTAKEOPEN) {
            Intake.intakeOpen()
        } else if (mode == IntakeState.INTAKECLOSE) {
            Intake.intakeClose()
        }

        timer.start()
    }

    override fun isFinished(): Boolean {
        return (timer.hasElapsed(0.5))
    }
    enum class IntakeState{
        INTAKEOPEN,
        INTAKECLOSE

    }
}
