package com.team2898.robot.commands

import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Feeder
import com.team2898.robot.subsystems.Intake
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase

class AutoIntakeTimed(private val runTime: Double) : CommandBase() {
    val timer = Timer()
    override fun initialize(){
        timer.reset()
        timer.start()
        Intake.setSpeed(0.5)
    }
    override fun isFinished(): Boolean {
        return timer.hasElapsed(runTime)
    }
    override fun end(interrupted: Boolean) {
        Intake.setSpeed(0.0)
    }
}
class AutoIntakeMaxItems(private val maxItems: Int) : CommandBase() {
    private val startingItems = Feeder.tubeCount
    override fun initialize(){
        Intake.setSpeed(0.5)
    }
    override fun isFinished(): Boolean {
        return Feeder.tubeCount > 3 || (Feeder.tubeCount - startingItems) < maxItems
    }
    override fun end(interrupted: Boolean) {
        Intake.setSpeed(0.0)
    }
}