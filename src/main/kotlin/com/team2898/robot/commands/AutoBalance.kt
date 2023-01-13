package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.Constants
import com.team2898.robot.subsystems.Odometry
import com.team2898.robot.Constants.AUTOBALANCE_KP
import com.team2898.robot.Constants.AUTOBALANCE_KI
import com.team2898.robot.Constants.AUTOBALANCE_KD
import com.bpsrobotics.engine.utils.NAVX
import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.wpilibj.Timer
import kotlin.math.absoluteValue


class AutoBalance : CommandBase() {
    private val pid = PIDController(AUTOBALANCE_KP, AUTOBALANCE_KI, AUTOBALANCE_KD)
    private val navx = NAVX()
    private var pitch = navx.pitch.toDouble()
    private var roll = navx.roll.toDouble()
    private val timer = Timer()
    private var elapsedTime = 0.0

    private var pitchRate = 0.0
    private var rollRate = 0.0

    override fun initialize() {
        navx.zeroYaw()
    }
    override fun execute() {

        //Gets the time since last execute, then resets the timer
        elapsedTime = timer.get()
        timer.reset()
        timer.start()

        //Gathers the change in the pitch and the change in roll since last execute
        pitchRate = (navx.pitch - pitch) / elapsedTime
        rollRate = (navx.roll - roll) / elapsedTime

        //Gets the current pitch and roll of the robot
        pitch = navx.pitch.toDouble()
        roll = navx.pitch.toDouble()

        //Gets how much power is needed to re-align the bot
        val pitchPower = pid.calculate(pitch)
        val rollPower = pid.calculate(roll)

        Drivetrain.stupidDrive(`M/s` (pitchPower), `M/s`(pitchPower))


    }

    override fun isFinished(): Boolean {
        //Test this to make sure the angles work properly for balance
        //Finishes if both rotations are close to zero and haven't changed quickly
        return (pitch > -5 || pitch < 5) && (roll > -5 || roll < 5) && (pitchRate < 0.3 && rollRate < 0.3)
    }
}