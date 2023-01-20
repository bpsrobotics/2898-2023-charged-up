@file:Suppress("EXPERIMENTAL_API_USAGE", "OPT_IN_USAGE")

package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.*
import edu.wpi.first.wpilibj.AddressableLED
import edu.wpi.first.wpilibj.AddressableLEDBuffer
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

object RGBLEDHandler : SubsystemBase() {
    private val strips = listOf(LEDStrip(1, 10), LEDStrip(2, 42), LEDStrip(3, 69420))
    private val ledStripBuffer = RGBAArray(strips.sumOf { it.ledCount })
    private const val rainbowSpeed = 1.0 // Configure rainbow spacing, 0 to 2 PI
    private const val ledsPerFlagColor = 4 // Size of each color strip in flag color modes

    class LEDStrip(port: Int, val ledCount: Int) {
        val strip = AddressableLED(port)
        val buffer = AddressableLEDBuffer(ledCount)
    }

    /**
     * The current color mode.  Set it to the desired value and the subsystem will handle updating.
     */
    var mode = ColorMode.OFF  // reinitialized below in an init block so that it invokes the setter
        set(value) {
            // when the mode is set, have it set all the colors once because the flag modes
            // aren't run periodically
            field = value
            val t = Timer.getFPGATimestamp().seconds
            value.lambda.invoke(ledStripBuffer, t)
            setDataRGBA()
            for (item in strips) {
                item.strip.setData(item.buffer)
            }
            lastUpdate = t
        }

    init {
        mode = ColorMode.values().filter { it.randomlySelectable }.random()
    }

    enum class ColorMode(val lambda: RGBAArray.(time: Seconds) -> Unit, val updateRate: Hertz = (-1).hz, val randomlySelectable: Boolean = true) {
        OFF({ fill(RGBA(0x00, 0x00, 0x00)) }),

        // solid colors (use in match for signaling?)
        RED({ fill(RGBA(0xDF, 0x00, 0xFF)) }),  // 1510 red
        BLUE({ fill(RGBA(0x1B, 0x45, 0x9B)) }),  // 2898 blue
        GREEN({ fill(RGBA(0x00, 0xFF, 0x00)) }),
        ORANGE({ fill(RGBA(0xFF, 0xA5, 0x00)) }),
        PINK({ fill(RGBA(0xFF, 0xC0, 0xCB)) }),

        // pride flags
        RAINBOW({
            val time = Timer.getFPGATimestamp().seconds
            for (i in indices) {
                val pos = (i * rainbowSpeed + (time.value / 2)).mod(2 * PI)
                val r = (sin(pos) * 127 + 128).roundToInt().toUByte()
                val g = (sin(pos + (2 * PI) / 3) * 127 + 128).roundToInt().toUByte()
                val b = (sin(pos - (2 * PI) / 3) * 127 + 128).roundToInt().toUByte()

                set(i, RGBA(r, g, b))
            }
        }, 5.hz),
        TRANS({ stripes(RGBAArray.of(
            RGBA(0x55, 0xCD, 0xFC),
            RGBA(0xF7, 0xA8, 0xB8),
            RGBA(0xFF, 0xFF, 0xFF),
            RGBA(0xF7, 0xA8, 0xB8),
            RGBA(0x55, 0xCD, 0xFC)))
        }),
        ENBY({ stripes(RGBAArray.of(
            RGBA(0xFF, 0xF4, 0x30),
            RGBA(0xFF, 0xFF, 0xFF),
            RGBA(0x9C, 0x59, 0xD1),
            RGBA(0x00, 0x00, 0x00)))
        }),
        ACE({ stripes(RGBAArray.of(
            RGBA(0x00, 0x00, 0x00),
            RGBA(0xA4, 0xA4, 0xA4),
            RGBA(0xFF, 0xFF, 0xFF),
            RGBA(0x81, 0x00, 0x81)))
        }),
        PAN({ stripes(RGBAArray.of(
            RGBA(0xFF, 0x1B, 0x8D),
            RGBA(0xFF, 0xDA, 0x00),
            RGBA(0x1B, 0xB3, 0xFF)))
        }),
        LESBIAN({ stripes(RGBAArray.of(
            RGBA(0xD6, 0x29, 0x00),
            RGBA(0xFF, 0x9B, 0x55),
            RGBA(0xFF, 0xFF, 0xFF),
            RGBA(0xD4, 0x61, 0xA6),
            RGBA(0xA5, 0x00, 0x62)))
        }),
        BI({ stripes(RGBAArray.of(
            RGBA(0xD0, 0x00, 0x70),
            RGBA(0x8C, 0x47, 0x99),
            RGBA(0x00, 0x32, 0xA0)))
        }),
        UKRAINE({ stripes(RGBAArray.of(
            RGBA(0x00, 0x5B, 0xBB),
            RGBA(0xFF, 0xD5, 0x00)))
        }),

        // effects that modify existing data in the buffer
        SLIDE_LEFT({
            array.copyInto(temp.array, 0, 1)
            temp[temp.size - 1] = temp[0]
            temp.array.copyInto(array, 0, 0, size)
        }, 5.hz, false),
        SLIDE_RIGHT({
            array.copyInto(temp.array, 1)
            temp[0] = temp[temp.size - 1]
            temp.array.copyInto(array, 0, 0, size)
        }, 5.hz, false),
        RANDOM_BLINK({
            for (i in indices) {
                val old = this[i]
                this[i] = RGBA(old.r, old.g, old.b, (Random.nextInt(0, 1) * 255).toUByte())
            }
        }, 5.hz, false),
        RANDOM_BRIGHTNESS({
            for (i in indices) {
                val old = this[i]
                this[i] = RGBA(old.r, old.g, old.b, (old.a.toInt() + Random.nextInt(-25, 25)).coerceIn(0, 255).toUByte())
            }
        }, 5.hz, false),
        COLOR_DRIFT({
            for (i in indices) {
                val hsv = ColorSpaceConversions.RGBtoHSV(this[i])
                val h = (hsv.h.toInt() + Random.nextInt(-5, 5)).mod(180)
                this[i] = ColorSpaceConversions.HSVtoRGB(HSVA(h, hsv.s, hsv.v))
            }
        }, 5.hz, false)
    }

    init {
        for (item in strips) {
            item.strip.setData(item.buffer)
            item.strip.start()
        }
    }

    private fun RGBAArray.stripes(colors: RGBAArray) {
        for (i in indices) {
            val color = colors[(i / ledsPerFlagColor).mod(colors.size)]
            set(i, color)
        }
    }

    private val temp = RGBAArray(ledStripBuffer.size + 1)

    /**
     * Copies the data from [ledStripBuffer] to the actual LED buffer, multiplying by alpha to
     * adjust the brightness
     */
    private fun setDataRGBA() {
        var i = 0

        for (item in strips) {
            for (idx in 0 until item.ledCount) {
                val color = ledStripBuffer[i++]
                item.buffer.setRGB(idx,
                    color.r.toInt() * color.a.toInt() / 255,
                    color.g.toInt() * color.a.toInt() / 255,
                    color.b.toInt() * color.a.toInt() / 255
                )
            }
        }
    }

    /** The last time the current mode's update lambda was invoked. */
    private var lastUpdate = 0.seconds

    override fun periodic() {
        // if the current mode requires periodic updates, and it's been long enough since the last
        // update, then call the mode's update method and copy the data over to the strip.
        if (mode.updateRate.value > 0.0) {
            val time = Timer.getFPGATimestamp().seconds

            val timeSince = time - lastUpdate

            if (timeSince.value > 1 / mode.updateRate.hertzValue()) {
                mode.lambda.invoke(ledStripBuffer, time)
                setDataRGBA()
                lastUpdate = time
            }

            for (item in strips) {
                item.strip.setData(item.buffer)
            }
        }
    }
}
