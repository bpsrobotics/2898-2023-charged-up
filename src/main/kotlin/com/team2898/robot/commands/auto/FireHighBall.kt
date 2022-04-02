package com.team2898.robot.commands.auto

import com.team2898.robot.subsystems.Feeder
import com.team2898.robot.subsystems.Shooter
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase

class FireHighBall(private val speeds: Shooter.ShooterSpeeds) : CommandBase() {
    private val timer = Timer()

    init {
        addRequirements(Shooter)
    }

    override fun initialize() {
        Shooter.spinUp(speeds)
        timer.reset()
        timer.start()
    }

    override fun execute() {
//        if (!startedShooter && currentDistance.value < 2.0) {
////            Shooter.spinUp(Meters(centerField.translation.getDistance(location.translation)))
//            startedShooter = true
//        } else if (
//            startedShooter &&
//            currentDistance.value < 1.0 &&
//            max(Odometry.vels.leftMetersPerSecond, Odometry.vels.rightMetersPerSecond) < 0.25
//        ) {
//            Feeder.shoot()
//        } else if (startedShooter && currentDistance.value > 3.0) {
////            Shooter.stopShooter()
//            completed = true
//        }
        if (timer.hasElapsed(2.0)) {
            Feeder.forceShoot()
        }
    }

    override fun isFinished(): Boolean {
        return timer.hasElapsed(3.0)
    }
}