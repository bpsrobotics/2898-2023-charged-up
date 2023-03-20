package com.team2898.robot.commands.Balance

import com.bpsrobotics.engine.utils.`M/s`
import com.bpsrobotics.engine.utils.MovingAverage
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase

class DriveToTipChargestation(private val direction : DriveDirection) : CommandBase() {
    private var pitch = Odometry.NavxHolder.navx.pitch.toDouble()
    private var pitchRate = 0.0
    private val timer = Timer()
    private var overTicks = 0
    private val averageRate = MovingAverage(15)
    private val stopTimer = Timer()
    init {
        stopTimer.stop()
    }

    override fun execute() {
        // Gets the time since last execute, then resets the timer
        val elapsedTime = timer.get()
        // Gathers the change in pitch since last execute
        pitchRate = (Odometry.NavxHolder.navx.pitch - pitch) / elapsedTime
        pitch = Odometry.NavxHolder.navx.pitch.toDouble()
        timer.reset()
        timer.start()

        if (pitchRate.isFinite()) {
            averageRate.add(pitchRate)
        }

        // Gets the current pitch and pitch of the robot

        if (averageRate.average > 10.0) {
            overTicks += 1
        } else {
            overTicks = 0
        }

        Drivetrain.stupidDrive(`M/s`(2.0 * direction.multiplier), `M/s`(2.0 * direction.multiplier))
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.fullStop()
    }

    override fun isFinished(): Boolean {
        return overTicks > 15
    }
}