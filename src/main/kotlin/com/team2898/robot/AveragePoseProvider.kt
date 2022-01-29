package com.team2898.robot

import com.bpsrobotics.engine.odometry.PoseProvider
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.interfaces.Gyro

class AveragePoseProvider(
    private vararg val providers: PoseProvider, private val gyro: Gyro
) : PoseProvider {
    override val pose: Pose2d
        get() {
            var x = 0.0
            var y = 0.0
            for (p in providers) {
                val a = p.pose
                x += a.x
                y += a.y
            }
            return Pose2d(x / providers.size, y / providers.size, gyro.rotation2d)
        }

    override fun update() {}

    override fun reset() {
        providers.forEach { it.reset() }
    }
}