/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team2898.robot

import com.team2898.robot.commands.*
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard

/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
class RobotContainer {
    // The robot's subsystems and commands are defined here...
    private val leadingAutoCommand: Command = LeadingAuto()
    private val fiveBallAutoCommand: Command = FiveBallAuto()
    private val nonLeadingAutoCommand: Command = NonLeadingAuto()
    private val hideAutoCommand: Command = HideAuto()
    private val noopAutoCommand: Command = NoopAuto()

    // A chooser for which command to use for auto, i.e. one for right, middle, left, red, blue, etc
    private var autoCommandChooser: SendableChooser<Command> = SendableChooser()


    /**
     * The container for the robot.  Contains subsystems, OI devices, and commands.
     */
    init {
        autoCommandChooser.setDefaultOption("Hide Auto", hideAutoCommand)
        autoCommandChooser.addOption("Do Nothing Auto", noopAutoCommand)
        autoCommandChooser.addOption("Five Ball Auto", fiveBallAutoCommand)
        autoCommandChooser.addOption("Non-Leading Auto", nonLeadingAutoCommand)
        autoCommandChooser.addOption("Leading Auto", leadingAutoCommand)
        // Send the auto chooser
        SmartDashboard.putData("Auto mode", autoCommandChooser)
    }

    /**
     * Use this method to define your button->command mappings.  Buttons can be created by
     * instantiating a {@link GenericHID} or one of its subclasses ({@link
     * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then calling passing it to a
     * {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
     */

    fun getAutonomousCommand(): Command {
        // Return the selected command
        return autoCommandChooser.selected
    }
}
