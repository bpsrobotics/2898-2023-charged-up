package com.team2898.robot.commands.climb

import com.bpsrobotics.engine.utils.Meters
import com.team2898.robot.subsystems.Climb
import com.team2898.robot.subsystems.Intake
import edu.wpi.first.wpilibj2.command.CommandBase

class ElevatorMove(val dest: Meters, val loaded: Boolean) : CommandBase() {
    /* TODO: Configurable speed? */
    override fun initialize() {
        Climb.arms(dest, loaded)
    }

    override fun isFinished(): Boolean {
        return Climb.isFinished
    }

    override fun execute() {
        Intake.openIntake()
    }
}