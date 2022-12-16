package com.team2898.robot.commands

import com.team2898.robot.subsystems.Feeder
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase

class AutoFeeder() : CommandBase() {
    val timer = Timer()
    override fun execute(){
        Feeder.startIntaking(false)
    }
    override fun isFinished(): Boolean {
        return false
    }
}
