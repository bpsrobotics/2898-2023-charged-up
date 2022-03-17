package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.m
import com.bpsrobotics.engine.utils.rad
import edu.wpi.first.networktables.EntryListenerFlags
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Vision : SubsystemBase() {
    private val table = NetworkTableInstance.getDefault().getTable("vision")
    private val distanceEntry = table.getEntry("distance")
    private val angleEntry = table.getEntry("angle")

    // who knows what thread the listeners fire on
    private val lock = Any()

    val lastUpdated = Timer()
        get() = synchronized(lock) { field }

    // volatile = not cached, to avoid potential thread safety issues
    @Volatile
    var distance = 0.m
        private set  // only settable internally

    @Volatile
    var angle = 0.rad
        private set

    init {
        lastUpdated.start()
        distanceEntry.addListener({
            synchronized(lock) { lastUpdated.reset() }
            distance = it.value.double.m
            angle = angleEntry.value.double.rad
        }, EntryListenerFlags.kUpdate)
    }
}
