/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team2898.robot

import com.team2898.robot.commands.TeleOp
import com.team2898.robot.subsystems.*
import edu.wpi.first.cameraserver.CameraServer
import edu.wpi.first.wpilibj.PowerDistribution
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
class Robot : TimedRobot() {

    // Note: 'lateinit' means you can declare a non-nullable variable and then first set it later
    lateinit var autoCommand: Command

    lateinit var robotContainer: RobotContainer

    /**
     * This function is run when the robot is first started up and should be used for any
     * initialization code.
     */
    override fun robotInit() {
        // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
        // autonomous chooser on the dashboard.
        robotContainer = RobotContainer()
        // Automatically grab auto command to ensure m_autonomousCommand is defined before teleopInit is run
        autoCommand = robotContainer.getAutonomousCommand()

        CameraServer.startAutomaticCapture()

        // initialize battery logger
        if (RobotBase.isReal()) {
//            BatteryLogger
        }

        Intake
        Vision
        Drivetrain

        SmartDashboard.putData("odometry", Odometry)
        SmartDashboard.putData("climb", Climb)
        SmartDashboard.putData("feeder", Feeder)
        SmartDashboard.putData("shooter", Shooter)
    }

    /**
     * This function is called every robot packet, no matter the mode. Use this for items like
     * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
     *
     * <p>This runs after the mode specific periodic functions, but before
     * LiveWindow and SmartDashboard integrated updating.
     */
    override fun robotPeriodic() {
        // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
        // commands, running already-scheduled commands, removing finished or interrupted commands,
        // and running subsystem periodic() methods.  This must be called from the robot's periodic
        // block in order for anything in the Command-based framework to work.
        CommandScheduler.getInstance().run()
        Odometry.update()
    }

    /**
     * This function is called once each time the robot enters Disabled mode.
     */
    override fun disabledInit() {
    }

    val pdp = PowerDistribution(60, PowerDistribution.ModuleType.kRev)

    override fun disabledPeriodic() {
        pdp.switchableChannel = false
    }

    /**
     * This autonomous runs the autonomous command selected by your {@link RobotContainer} class.
     */
    override fun autonomousInit() {
        autoCommand = robotContainer.getAutonomousCommand()

        // schedule the autonomous command (example)
        autoCommand.let { autoCommand.schedule() }
    }

    /**
     * This function is called periodically during autonomous.
     */
    override fun autonomousPeriodic() {
    }

    override fun teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        autoCommand.let { autoCommand.cancel() }
        Climb.ignoreLimits()
        Drivetrain.defaultCommand = TeleOp()
    }

    /**
     * This function is called periodically during operator control.
     */
    override fun teleopPeriodic() {
    }

    override fun testInit() {
        // Cancels all running commands at the start of test mode.
        CommandScheduler.getInstance().cancelAll()
    }

    /**
     * This function is called periodically during test mode.
     */
    override fun testPeriodic() {
    }
}
