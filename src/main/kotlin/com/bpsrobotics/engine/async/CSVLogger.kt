package com.bpsrobotics.engine.async

import com.bpsrobotics.engine.utils.Millis
import edu.wpi.first.wpilibj.Timer
import java.io.File

class CSVLogger(name: String, timesPerSecond: Double, vararg fields: Pair<String, () -> Double>) {
    init {
        val baseDir = File("/home/lvuser/logs/$name/")
        baseDir.mkdirs()

        // find the highest existing number, or -1 if there isn't one
        val maximumNumber = baseDir.list()?.maxOfOrNull {
            it.removeSuffix(".csv").toIntOrNull() ?: -1
        } ?: -1

        // create the next file in sequence
        val file = baseDir.resolve("${maximumNumber + 1}.csv")
        file.createNewFile()

        val writer = file.bufferedWriter()
        writer.write("time," + fields.joinToString(",", postfix = "\n") { it.first })

        AsyncLooper.loop(Millis((1000 / timesPerSecond).toLong()), "logger '$name'") {
            val time = Timer.getFPGATimestamp()
            val fieldsToWrite = listOf(time) + fields.map { it.second() }

            // suppress blocking method call warning
            runCatching {
                writer.write(fieldsToWrite.joinToString(",", postfix = "\n") { it.toString() })
            }
        }
    }
}