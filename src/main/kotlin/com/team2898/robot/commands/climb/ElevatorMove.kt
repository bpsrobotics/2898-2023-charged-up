package com.team2898.robot.commands.climb

import com.bpsrobotics.engine.utils.Meters
import com.team2898.robot.subsystems.Climb
import edu.wpi.first.wpilibj2.command.CommandBase

class ElevatorMove(val dest: Meters) : CommandBase() {
    /* TODO: Configurable speed? */
    override fun initialize() {
        Climb.elevator.goTo(dest)
    }

    override fun isFinished(): Boolean {
        return Climb.elevator.isFinished()
    }
}