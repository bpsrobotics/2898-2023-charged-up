package com.team2898.robot.subsystems

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.networktables.NetworkTableEvent
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase
import java.util.*

object Vision : SubsystemBase() {
    var currentRobotPose = Pose2d()
        private set

    private val instance = NetworkTableInstance.getDefault()
    private val table = instance.getTable("vision")
    private val topic = table.getDoubleArrayTopic("VisionPos")

    var x = 0.0
    var y = 0.0
    var z = 0.0

    private var lastFixTime = 0.0
    val timeSinceLastFix get() = Timer.getFPGATimestamp() - lastFixTime

    var r = 0.0
    var stdev = 0.0  // TODO: Make into Matrix

    val listeners = mutableListOf<(Pose2d, Double) -> Unit>()

    init {
        instance.addListener(topic.subscribe(doubleArrayOf(0.0, 0.0, 0.0, 0.0)), EnumSet.of(NetworkTableEvent.Kind.kValueAll)) { v ->
            lastFixTime = Timer.getFPGATimestamp()
            val arr = v.valueData.value.doubleArray
            x = arr[0]
            y = arr[1]
            z = arr[2]
            r = arr[3]
            currentRobotPose = Pose2d(x, y, Rotation2d.fromDegrees(r))
            listeners.forEach { it(currentRobotPose, lastFixTime) }
        }
    }
}
