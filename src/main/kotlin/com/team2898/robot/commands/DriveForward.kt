package com.team2898.robot.commands
import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase

/**
 * Drives at speed for seconds
 * @param speed Speed of both wheels in M/S. Make the speed negative to move backwards. Max 1 M/S
 * @param seconds Seconds the robot should drive
 */
class DriveForward(var seconds : Double, var speed: Double) : CommandBase() {
    val timer = Timer()

    override fun initialize() {
        timer.start()
        timer.reset()
        if(speed > 1){
            speed = 1.0
        }
        if(speed < -1){
            speed = -1.0
        }
    }

    override fun execute() {
        Drivetrain.stupidDrive(`M/s`(speed), `M/s`(speed))
    }

    override fun isFinished(): Boolean {
        return timer.hasElapsed(seconds)
//        return timer.hasElapsed(1.3)
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.stupidDrive(`M/s`(0.0), `M/s`(0.0))
    }
}