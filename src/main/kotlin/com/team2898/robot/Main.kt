/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team2898.robot

import edu.wpi.first.wpilibj.RobotBase

/**
 * This starts the program, do not modify!
 */
class Main {
    /**
     * Main initialization function. Do not perform any initialization here.
     * 
     * If you change your main robot class, change the parameter type.
    */
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = RobotBase.startRobot(::Robot)
    }
}
