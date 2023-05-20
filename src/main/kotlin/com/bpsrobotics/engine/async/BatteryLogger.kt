package com.bpsrobotics.engine.async

import com.bpsrobotics.engine.utils.Amps
import com.bpsrobotics.engine.utils.Millis
import com.bpsrobotics.engine.utils.volts
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.PowerDistribution
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType.kRev
import java.io.File
import java.time.Instant

class BatteryLogger(val pdp: PowerDistribution) {
    private val baseDir = File("/home/lvuser/power-logs").apply { mkdir() }
    private val logFile = if (DriverStation.isFMSAttached()) {
        // If we are in comp, we use the "event-matchtype-matchnum-replay-replaynum.txt" format
        baseDir.resolve("${DriverStation.getEventName()}-" +
                "${DriverStation.getMatchType()}" +
                "-${DriverStation.getMatchNumber()}" +
                "-replay-${DriverStation.getReplayNumber()}.txt")
            .apply { createNewFile() }
    } else {
        // If we're not in a comp match, we use the "noncomp" directory
        // Files are created with the "0.txt", "1.txt", "2.txt" etc naming scheme
        val noncompDir = baseDir.resolve("noncomp")
        noncompDir.mkdir()

//        val existingLogs = noncompDir.listFiles()!!
//        val highestNumber = existingLogs.maxOfOrNull { fi -> fi.nameWithoutExtension.toInt() }
//            ?: 0
//        val newFilename = "${highestNumber + 1}.txt"
        val newFilename = "${Instant.now()}.txt"
        val file = noncompDir.resolve(newFilename)
        file.createNewFile()

        file
    }
    private val startVoltage = pdp.voltage.volts
    private var minVoltage = startVoltage
    private var maxCurrent = Amps(0.0)

    init {
        pdp.clearStickyFaults()
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
