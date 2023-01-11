package com.team2898.robot.commands

import com.team2898.robot.Constants
import com.team2898.robot.subsystems.Odometry
import com.team2898.robot.Constants.AUTOBALANCE_KP
import com.team2898.robot.Constants.AUTOBALANCE_KI
import com.team2898.robot.Constants.AUTOBALANCE_KD
import com.bpsrobotics.engine.utils.NAVX
import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.math.controller.PIDController

class AutoBalance : CommandBase() {
    private val pid = PIDController(AUTOBALANCE_KP, AUTOBALANCE_KI, AUTOBALANCE_KD)
    private val navx = NAVX()
    private var pitch = navx.pitch
    private var roll = navx.roll

    override fun execute() {
        pitch = navx.pitch
        var powerNeeded = pid.calculate(pitch.toDouble())

        /*if (pitch > 0) {
            //Since the rotation is greater than 0, the robot needs to drive forward?
        }
        else if (pitch < 0) {
            //Since rotation is less than 0, the bot needs to drive backward?
        }
        else if (pitch.toDouble() == 90.0) {
            //This is for when the robot completely aligns
        }
        else {println("pitch is " + pitch + ", roll is " + roll)}
        //This line is just for bug testing
        */



    }
    override fun isFinished(): Boolean {
        return (pitch > -5 || pitch < 5) && navx.rate < 0.3
    }
}