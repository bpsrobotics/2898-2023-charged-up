package com.team2898.robot.commands.Balance

import com.bpsrobotics.engine.utils.`M/s`
import com.bpsrobotics.engine.utils.MovingAverage
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase
/** Drives until the charge station starts to tip
 * @property direction The direction in which to drive
 * */
class DriveToTipChargestation(private val direction : DriveDirection) : CommandBase() {
    /** Robots pitch */
    private var pitch = Odometry.NavxHolder.navx.pitch.toDouble()
    /** Change in pitch over time */
    private var pitchRate = 0.0
    /** Stores the time since the last execute, for calculating pitchRate */
    private val frameRateTimer = Timer()
    /** Number of ticks average pitchRate has been greater than 10 */
    private var overTicks = 0
    /** Stores the last 15 iterations of pitchRate*/
    private val averageRate = MovingAverage(15)

    override fun execute() {
        // Gets the time since last execute, then resets the timer
        val elapsedTime = frameRateTimer.get()
        // Gathers the change in pitch since last execute
        pitchRate = (Odometry.NavxHolder.navx.pitch - pitch) / elapsedTime
        pitch = Odometry.NavxHolder.navx.pitch.toDouble()

        frameRateTimer.reset()
        frameRateTimer.start()

        // If there isn't a divide by zero error, add the averageRate
        if (pitchRate.isFinite()) {
            averageRate.add(pitchRate)
        }

        // If the NavX still says robot is tipping, add to over ticks
        if (averageRate.average > 10.0) {
            overTicks += 1
        } else {
            overTicks = 0
        }

        Drivetrain.stupidDrive(`M/s`(2.0 * direction.multiplier), `M/s`(2.0 * direction.multiplier))
    }

    override fun end(interrupted: Boolean) {
        //Stop robot
        Drivetrain.fullStop()
    }

    override fun isFinished(): Boolean {
        // if NavX says robot is tipping for 15 ticks, stop.
        return overTicks > 15
    }
}