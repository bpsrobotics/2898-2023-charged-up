package com.team2898.robot.commands.Balance

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.absoluteValue
/** Drives until the robot is level (Off the charge station) and for a bit longer to ensure bumpers leave community
 * @property direction The direction in which to drive
 * */
class ChargestationMobility(private val direction: DriveDirection) : CommandBase() {
    /** Pitch of the robot */
    private var pitch = Odometry.NavxHolder.navx.pitch.toDouble()
    /** Ensures the robot drives for long enough, and not too long. */
    private val timer = Timer()
    override fun execute() {
        Drivetrain.stupidDrive(`M/s`(2.0 * direction.multiplier), `M/s`(2.0 * direction.multiplier))
    }
    override fun end(interrupted: Boolean) {
        //Stops robot
        Drivetrain.fullStop()
    }
    override fun isFinished(): Boolean {
        // If it has been at least 0.2 seconds, and the robot is relatively level, stop.
        // If it has been more than 0.5 seconds, something likely went wrong; Stop.
        return (timer.hasElapsed(0.2) && pitch.absoluteValue < 2.5) || timer.hasElapsed(0.5)
    }
}