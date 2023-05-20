package com.team2898.robot.commands

import com.team2898.robot.subsystems.Intake
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase

/**
 * Runs intake forwards or backwards to take a gamepiece or deposit it
 * @author Ori
 */
class ActivateIntake(private val mode: RunningIntakes, private val power: Double = 0.75) : CommandBase() {
    private val timer = Timer()

    override fun initialize() {
        if (mode == RunningIntakes.RUNINTAKE) {
            Intake.runIntake(power)
        } else if (mode == RunningIntakes.RUNOUTTAKE) {
            Intake.runOuttake(power)
        }
        timer.start()
        timer.reset()
    }

    override fun isFinished(): Boolean {
        return timer.hasElapsed(0.75)
    }

    override fun end(interrupted: Boolean) {
        Intake.stopIntake()
    }

    enum class RunningIntakes {
        RUNINTAKE,
        RUNOUTTAKE
    }
}