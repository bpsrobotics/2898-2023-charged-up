package com.team2898.robot.commands

import com.team2898.robot.subsystems.Feeder
import com.team2898.robot.subsystems.Intake
import edu.wpi.first.wpilibj2.command.CommandBase

/**
 * Runs feeder until beambreak senses a tube is intaked
 */
class TubeCountFeederAuto : CommandBase() {
    private val startTubeCount = Feeder.tubeCount

    override fun initialize() {
        Intake.setSpeed(1.0)
    }

    override fun isFinished(): Boolean {
//        return startTubeCount > Feeder.tubeCount
        return false
//        return Feeder.countState.ordinal > 1 || startTubeCount > Feeder.tubeCount
    }

    override fun end(interrupted: Boolean) {
        Intake.setSpeed(0.0)

        //jank
//        Feeder.state = Feeder.FeederState.STOPPED
        Feeder.startIntaking(true)
    }
}
