/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team2898.robot

import com.team2898.robot.commands.autos.*
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
    private val noopAutoCommand: Command = NoAuto()


    // A chooser for which command to use for auto, i.e. one for right, middle, left, red, blue, etc
    private var autoCommandChooser: SendableChooser<Command> = SendableChooser()

    init {
        autoCommandChooser.setDefaultOption("No Auto", noopAutoCommand)
//        autoCommandChooser.addOption("Leading Auto", LeadingAuto())
//        autoCommandChooser.addOption("Top Middle Auto", TopMiddleAuto())
//        autoCommandChooser.addOption("Leave Auto", LeaveAuto())
        autoCommandChooser.addOption("Preload + Balance Auto", PreloadBalanceAuto())
        autoCommandChooser.addOption("Preload Auto", PreloadAuto())
        autoCommandChooser.addOption("Preload + Mobility Auto", PreloadMobilityAuto())
        autoCommandChooser.addOption("Balance Auto", BalanceAuto())
        autoCommandChooser.addOption("Leave Auto", LeaveAuto())
//        autoCommandChooser.addOption("Scoring Auto", ScoringAuto())

        // Send the auto chooser
        SmartDashboard.putData("Auto mode", autoCommandChooser)
    }

    fun getAutonomousCommand(): Command {
        // Return the selected command
//        return SequentialCommandGroup(SuperSimpleAuto(), SimpleBalance())
        return autoCommandChooser.selected
    }
}
