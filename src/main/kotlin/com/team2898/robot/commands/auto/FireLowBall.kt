package com.team2898.robot.commands.auto

import com.team2898.robot.subsystems.Feeder
import com.team2898.robot.subsystems.Shooter
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase

class FireLowBall(private val ballCount: Int) : CommandBase() {
    private val timer = Timer()

    init {
        addRequirements(Shooter)
    }

    override fun initialize() {
        Shooter.dumpSpinUp()
        timer.reset()
        timer.start()
    }

    override fun execute() {
        if (timer.hasElapsed(1.5)) {
            Feeder.forceShoot()
        }

    }

    override fun isFinished(): Boolean {
        return if (ballCount == 2) timer.hasElapsed(3.0) else timer.hasElapsed(3.0)
    }
}