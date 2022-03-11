package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.m
import com.bpsrobotics.engine.utils.rad
import com.bpsrobotics.engine.utils.seconds
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Vision : SubsystemBase() {
    private val table = NetworkTableInstance.getDefault().getTable("vision")
    private val distanceEntry = table.getEntry("distance")
    private val angleEntry = table.getEntry("angle")

    // private so they can be used in a threadsafe manner
    private var internalDistance = 0.m
    private var internalAngle = 0.rad

    // TODO: questionably thread safe
    val lastUpdated = Timer()
    var distance = 0.m
    var angle = 0.rad

    init {
        lastUpdated.start()
        distanceEntry.addListener({
            // every time the vision values are updated, copy them to the class fields
            // synchronized for thread safety
            synchronized(Vision) {
                lastUpdated.reset()
                internalDistance = it.value.double.m
                internalAngle = angleEntry.value.double.rad
            }
        }, 0)
    }

    override fun periodic() {
        // copy the grabbed values to the public fields
        synchronized(Vision) {
            distance = internalDistance
            angle = internalAngle
        }
        distance = distanceEntry.getDouble(0.0).m
        angle = angleEntry.getDouble(0.0).rad
    }
}
