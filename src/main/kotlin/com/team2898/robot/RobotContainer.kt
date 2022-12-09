/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team2898.robot

import com.team2898.robot.commands.HomingVision
import com.team2898.robot.commands.RobotAuto
import com.team2898.robot.commands.TestAuto
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.InstantCommand

/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
class RobotContainer {
    private val noopAutoCommand: Command = InstantCommand({})

    // A chooser for which command to use for auto, i.e. one for right, middle, left, red, blue, etc
    private var autoCommandChooser: SendableChooser<Command> = SendableChooser()

    init {
        autoCommandChooser.setDefaultOption("Do Nothing Auto", noopAutoCommand)
        // Send the auto chooser
        SmartDashboard.putData("Auto mode", autoCommandChooser)
    }

    fun getAutonomousCommand(): Command {
        // Return the selected command
        return HomingVision()
    }
}
