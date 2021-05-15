package com.teamXXXX.robot.subsystems

import com.bpsrobotics.engine.odometry.DifferentialDrivePoseProvider
import com.bpsrobotics.engine.odometry.PoseProvider
import com.bpsrobotics.engine.utils.NAVX
import edu.wpi.first.wpilibj.Sendable
import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder

object Odometry : Sendable {
    private val gyro = NAVX()
    private val odometry: PoseProvider = DifferentialDrivePoseProvider(gyro, Drivetrain.leftEncoder, Drivetrain.rightEncoder)

    /** Call once per tick.  Updates the internal [PoseProvider]. */
    fun update() {
        odometry.update()
    }

    /** Gets the current robot pose.  Avoid calling repeatedly, may be expensive. */
    fun pose(): Pose2d {
        return odometry.pose
    }

    /** Initializes the pose to send to the SmartDashboard */
    override fun initSendable(builder: SendableBuilder?) {
        builder?.addDoubleArrayProperty("pose-2d", {
            val pose = pose()  // Only get once
            return@addDoubleArrayProperty doubleArrayOf(pose.x, pose.y, pose.rotation.degrees)
        }, null)
    }
}
