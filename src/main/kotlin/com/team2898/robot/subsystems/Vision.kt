package com.team2898.robot.subsystems

import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.geometry.Quaternion
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Confidence {
    fun get_confindence(x_dist: Double, y_dist: Double, speed: Double = 0.0) : Double {
        return 1.0
    }
}


object Vision : SubsystemBase() {
    var currentRobotPose = Pose3d()

}
