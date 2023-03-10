package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.bpsrobotics.engine.utils.MovingAverage
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.CommandBase

/**
 * Balances on the charging dock by simply moving forward or backward based on it's pitch
 * @author Ori
 */
class SimpleBalance : CommandBase() {

    private var pitch = Odometry.NavxHolder.navx.pitch.toDouble()
    private var pitchRate = 0.0
    private val timer = Timer()
    private var balanced = false
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

//        if (pitchRate.isNaN() || pitchRate.isInfinite()) {
//            pitchRate = averageRate.average
//        }
        if (pitchRate.isFinite()) {
            averageRate.add(pitchRate)
        }

        // Gets the current pitch and pitch of the robot

        if (averageRate.average > 10.0) {
            overTicks += 1
        } else {
            overTicks = 0
        }

        SmartDashboard.putNumber("rate", pitchRate)
        SmartDashboard.putNumber("avg rate", averageRate.average)
        SmartDashboard.putNumber("median rate", averageRate.median)
        SmartDashboard.putNumber("over", overTicks.toDouble())

        if (stopTimer.hasElapsed(1.25)) {
            Drivetrain.brakeMode()
//            Drivetrain.stupidDrive(`M/s`(0.0), `M/s`(0.0))
            Drivetrain.rawDrive(0.0, 0.0)
            Drivetrain.mode = Drivetrain.Mode.OPEN_LOOP
            return
        }

        if (overTicks > 15 || balanced) {
            val s = 0.6
            Drivetrain.stupidDrive(`M/s`(s), `M/s`(s))
            if (!balanced) {
                stopTimer.reset()
                stopTimer.start()
            }
            balanced = true
        } else {
            val s = -0.5
            Drivetrain.stupidDrive(`M/s`(s), `M/s`(s))
        }
    }

    override fun isFinished(): Boolean {
        return false
    }
}