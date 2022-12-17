package com.team2898.robot.commands

import com.team2898.robot.subsystems.Feeder
import edu.wpi.first.wpilibj2.command.CommandBase
/** Runs feeder until beambreak senses a tube is intaked */
class TubeCountFeederAuto() : CommandBase() {
    val startTubeCount = Feeder.tubeCount
    override fun execute(){
        Feeder.startIntaking(false)
    }
    override fun isFinished(): Boolean {
        return startTubeCount > Feeder.tubeCount
    }
}