package com.team2898.robot

import com.bpsrobotics.engine.odometry.PoseProvider
import com.bpsrobotics.engine.utils.*
import com.team2898.robot.subsystems.Odometry
import com.team2898.robot.subsystems.Vision
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.Field2d
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class VisionPoseProvider(private val other: PoseProvider) : PoseProvider {
    override var pose = Pose2d()
        private set  // not settable outside this object
        // the public fields don't need syncing because everything that

    // offsets from the other pose provider
    private var xOffset = 0.m
    private var yOffset = 0.m
    private var rotOffset = 0.rad

    // the weight to give vision values in the averaging
    private val visionWeight = 0.04
    // the weight to give the previous calculated value in the averaging
    private val otherWeight = 1 - visionWeight

//    private val otherProvF = Field2d()
    private val provF = Field2d()

    init {
//        SmartDashboard.putData("otherproviderfield", otherProvF)
        SmartDashboard.putData("providerfield", provF)
    }

    override fun update() {
//        otherProvF.robotPose = Odometry.otherProvider.pose
        provF.getObject("other").pose = Odometry.otherProvider.pose
        provF.robotPose = pose

        other.update()
        Vision.periodic()
        if (/*Timer.getFPGATimestamp() - Vision.lastUpdated.value > 0.1*/Vision.distance.value == 0.0 && Vision.angle.value == 0.0) {
            pose = Pose2d(other.pose.x + xOffset.value, other.pose.y + yOffset.value, other.pose.rotation)
            SmartDashboard.putNumber("lastupdated", Timer.getFPGATimestamp())
            return
        }

        val visionDistance = Vision.distance
        val visionAngle = Vision.angle
        val absoluteAngle = other.pose.rotation.radians.rad + rotOffset

        val otherX = other.pose.x.m + xOffset
        val otherY = other.pose.y.m + yOffset

        val visionPose = getXY(visionDistance, visionAngle, absoluteAngle)

        val finalPose = Pose2d(
            visionPose.x * visionWeight + otherX.value * otherWeight,
            visionPose.y * visionWeight + otherY.value * otherWeight,
            other.pose.rotation
        )

        xOffset = other.pose.x.m - finalPose.x.m
        yOffset = other.pose.y.m - finalPose.y.m

        pose = finalPose
    }

    private fun getXY(dist: Meters, angle: Radians, absoluteAngle: Radians): Translation2d {
        val c = (PI / 2).rad - (absoluteAngle - angle)
        val x = cos(c.value) * dist.value + 8.2296  // field offset
        val y = sin(c.value) * dist.value + 4.1148  // field offset
        return Translation2d(x, y)
    }

    override fun reset(x: Meters, y: Meters, theta: Degrees) {
        other.reset(x, y, theta)
        xOffset = 0.m
        yOffset = 0.m
        rotOffset = 0.rad
        Odometry.otherProvider.reset(x, y, theta)
    }
}
