package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase

class SimpleBalance : CommandBase() {

    private var pitch = Odometry.NavxHolder.navx.pitch.toDouble() - 16.5
    private var pitchRate = 0.0
    private val timer = Timer()
    private var balanced = false

    override fun execute() {
        // Gets the time since last execute, then resets the timer
        val elapsedTime = timer.get()
        timer.reset()
        timer.start()

        // Gathers the change in pitch since last execute
        pitchRate = ((Odometry.NavxHolder.navx.pitch - 16.5) - pitch) / elapsedTime

        // Gets the current pitch and pitch of the robot
        pitch = Odometry.NavxHolder.navx.pitch.toDouble() - 16.5

        if (pitchRate > 3.0) {
            Drivetrain.stupidDrive(`M/s`(0.0), `M/s`(0.0))
            balanced = true
        } else if (!balanced) {
            //TODO: Adjust driving speed
            Drivetrain.stupidDrive(`M/s`(0.1), `M/s`(0.1))
        }
    }

    override fun isFinished(): Boolean {
        return false
    }
}