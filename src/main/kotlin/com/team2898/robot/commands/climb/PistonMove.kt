package com.team2898.robot.commands.climb

import com.team2898.robot.subsystems.Climb
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase

class PistonMove(val dest: DoubleSolenoid.Value) : CommandBase() {
    var startTime = 0.0


    override fun initialize() {
        startTime = Timer.getFPGATimestamp()
        Climb.pistons(dest)
    }

    override fun isFinished(): Boolean {
        return (Timer.getFPGATimestamp() - startTime) > 1
    }

}