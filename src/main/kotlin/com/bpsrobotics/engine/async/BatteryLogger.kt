package com.bpsrobotics.engine.async

import com.bpsrobotics.engine.utils.Amps
import com.bpsrobotics.engine.utils.Millis
import com.bpsrobotics.engine.utils.volts
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.PowerDistribution
import java.io.File

object BatteryLogger {
    private val baseDir = File("/home/lvuser/power-logs").apply { mkdir() }
    private val logFile = if (DriverStation.isFMSAttached()) {
        baseDir.resolve(DriverStation.getEventName() + "-" + DriverStation.getMatchType() + "-match-" + DriverStation.getMatchNumber() + ".txt").apply { createNewFile() }
    } else {
        baseDir.resolve("noncomp").apply { mkdir() }.run { (listFiles() ?: emptyArray()).maxByOrNull { it.nameWithoutExtension.toIntOrNull() ?: 0 } ?: resolve("0.txt") }
    }
    private val pdp = PowerDistribution()
    private val startVoltage = pdp.voltage.volts
    private var minVoltage = startVoltage
    private var maxCurrent = Amps(0.0)

    init {
        AsyncLooper.loop(Millis(1000L / 50), "min voltage") {
            if (pdp.voltage < minVoltage.value) minVoltage = pdp.voltage.volts
            if (pdp.totalCurrent > maxCurrent.value) maxCurrent = Amps(pdp.totalCurrent)
        }
        AsyncLooper.loop(Millis(10_000), "power logger") {
            logFile.writeText("initial voltage = $startVoltage\n" +
                    "min voltage = $minVoltage\n" +
                    "max current = $maxCurrent\n" +
                    "total energy = ${pdp.totalEnergy} joules")
        }
    }
}
