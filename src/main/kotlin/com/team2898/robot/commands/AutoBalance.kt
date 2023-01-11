package com.team2898.robot.commands

import com.team2898.robot.Constants
import com.team2898.robot.subsystems.Odometry
import com.team2898.robot.Constants.AUTOBALANCE_KP
import com.team2898.robot.Constants.AUTOBALANCE_KI
import com.team2898.robot.Constants.AUTOBALANCE_KD
import com.bpsrobotics.engine.utils.NAVX
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.math.controller.PIDController

class AutoBalance : CommandBase() {
    val pid = PIDController(AUTOBALANCE_KP, AUTOBALANCE_KI, AUTOBALANCE_KD)
    val navx = NAVX()


}