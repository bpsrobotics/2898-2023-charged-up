package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.ColorSpaceConversions.HSVtoRGB
import com.bpsrobotics.engine.utils.ColorSpaceConversions.RGBtoHSV
import edu.wpi.first.wpilibj.AddressableLED
import edu.wpi.first.wpilibj.AddressableLEDBuffer
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

object RGBLEDHandler : SubsystemBase() {
    private const val ledCount = 200 // TODO: How many WS2812s do we have
    private val ledStrip = AddressableLED(8) // TODO: PWM Port Constant
    private val finalLEDStripBuffer = AddressableLEDBuffer(ledCount)

    private val ledStripBuffer = IntArray(ledCount * 4) // RGBA
    private var toggle = true
    private const val rainbowSpeed = 1.0 // Configure rainbow spacing, 0 to 2 PI
    private const val ledsPerFlagColor = 4 // Size of each color strip in flag color modes

    enum class ColorSets {
        RED,
        BLUE,
        GREEN,
        ORANGE,
        PINK,
        RAINBOW,
        TRANS,
        ENBY,
        ACE,
        PAN
    }

    init {
        ledStrip.setData(finalLEDStripBuffer)
        ledStrip.start()
    }


    fun rainbow() = sequence {
        var currentSinePos = 0.0

        while (true) {
            currentSinePos = currentSinePos.mod(2 * PI)
            currentSinePos += rainbowSpeed

            val r = (sin(currentSinePos) * 127 + 128).roundToInt()
            val g = (sin(currentSinePos + (2 * PI) / 3) * 127 + 128).roundToInt()
            val b = (sin(currentSinePos - (2 * PI) / 3) * 127 + 128).roundToInt()

            yield(intArrayOf(r, g, b))
        }
    }

    fun setColors(colors: IntArray) {
        for (n in ledStripBuffer.indices) {
            ledStripBuffer[n] = colors[n.mod(colors.size)]
        }
    }

    /** Change the color being displayed. This function should not be called in the periodic. */
    fun setColors(colors: ColorSets) {
        when (colors) {
            ColorSets.RAINBOW -> {
                for (n in 0 until ledStripBuffer.size / 4) {
                    val rainbow = rainbow().take(1).toList()[0]
                    ledStripBuffer[n * 4] = rainbow[0]
                    ledStripBuffer[n * 4 + 1] = rainbow[1]
                    ledStripBuffer[n * 4 + 2] = rainbow[2]
                }
            }
            ColorSets.TRANS -> {
                val stripes = 5
                val lightBlue = IntArray(4 * ledsPerFlagColor)
                for (n in 0 until ledsPerFlagColor) {
                    intArrayOf(0x55, 0xCD, 0xFC, 0xFF).copyInto(lightBlue, n * ledsPerFlagColor)
                }

                val lightPink = IntArray(4 * ledsPerFlagColor)
                for (n in 0 until ledsPerFlagColor) {
                    intArrayOf(0xF7, 0xA8, 0xB8, 0xFF).copyInto(lightPink, n * ledsPerFlagColor)
                }

                val white = IntArray(4 * ledsPerFlagColor)
                for (n in 0 until ledsPerFlagColor) {
                    intArrayOf(0xFF, 0xFF, 0xFF, 0xFF).copyInto(white, n * ledsPerFlagColor)
                }

                val finalFlag = IntArray(4 * ledsPerFlagColor * stripes)
                lightBlue.copyInto(finalFlag)
                lightPink.copyInto(finalFlag, 4 * ledsPerFlagColor * 1)
                white.copyInto(finalFlag, 4 * ledsPerFlagColor * 2)
                lightPink.copyInto(finalFlag, 4 * ledsPerFlagColor * 3)
                lightBlue.copyInto(finalFlag, 4 * ledsPerFlagColor * 4)

                setColors(finalFlag)
            }
            ColorSets.ENBY -> {
                val stripes = 4
                val yellow = IntArray(4 * ledsPerFlagColor)
                for (n in 0 until ledsPerFlagColor) {
                    intArrayOf(0xFF, 0xF4, 0x30, 0xFF).copyInto(yellow, n * ledsPerFlagColor)
                }

                val white = IntArray(4 * ledsPerFlagColor)
                for (n in 0 until ledsPerFlagColor) {
                    intArrayOf(0xFF, 0xFF, 0xFF, 0xFF).copyInto(white, n * ledsPerFlagColor)
                }

                val purple = IntArray(4 * ledsPerFlagColor)
                for (n in 0 until ledsPerFlagColor) {
                    intArrayOf(0x9C, 0x59, 0xD1, 0xFF).copyInto(purple, n * ledsPerFlagColor)
                }

                val black = IntArray(4 * ledsPerFlagColor)
                for (n in 0 until ledsPerFlagColor) {
                    intArrayOf(0x00, 0x00, 0x00, 0xFF).copyInto(black, n * ledsPerFlagColor)
                }

                val finalFlag = IntArray(4 * ledsPerFlagColor * stripes)
                yellow.copyInto(finalFlag)
                white.copyInto(finalFlag, 4 * ledsPerFlagColor * 1)
                purple.copyInto(finalFlag, 4 * ledsPerFlagColor * 2)
                black.copyInto(finalFlag, 4 * ledsPerFlagColor * 3)

                setColors(finalFlag)
            }
            ColorSets.ACE -> {
                val stripes = 4

                val black = IntArray(4 * ledsPerFlagColor)
                for (n in 0 until ledsPerFlagColor) {
                    intArrayOf(0x00, 0x00, 0x00, 0xFF).copyInto(black, n * ledsPerFlagColor)
                }

                val silver = IntArray(4 * ledsPerFlagColor)
                for (n in 0 until ledsPerFlagColor) {
                    intArrayOf(0xA4, 0xA4, 0xA4, 0xFF).copyInto(silver, n * ledsPerFlagColor)
                }

                val white = IntArray(4 * ledsPerFlagColor)
                for (n in 0 until ledsPerFlagColor) {
                    intArrayOf(0xFF, 0xFF, 0xFF, 0xFF).copyInto(white, n * ledsPerFlagColor)
                }

                val purple = IntArray(4 * ledsPerFlagColor)
                for (n in 0 until ledsPerFlagColor) {
                    intArrayOf(0x81, 0x00, 0x81, 0xFF).copyInto(purple, n * ledsPerFlagColor)
                }

                val finalFlag = IntArray(4 * ledsPerFlagColor * stripes)
                black.copyInto(finalFlag)
                silver.copyInto(finalFlag, 4 * ledsPerFlagColor * 1)
                white.copyInto(finalFlag, 4 * ledsPerFlagColor * 2)
                purple.copyInto(finalFlag, 4 * ledsPerFlagColor * 3)

                setColors(finalFlag)
            }
            ColorSets.PAN -> {
                val stripes = 3

                val pink = IntArray(4 * ledsPerFlagColor)
                for (n in 0 until ledsPerFlagColor) {
                    intArrayOf(0xFF, 0x1B, 0x8D, 0xFF).copyInto(pink, n * ledsPerFlagColor)
                }

                val yellow = IntArray(4 * ledsPerFlagColor)
                for (n in 0 until ledsPerFlagColor) {
                    intArrayOf(0xFF, 0xDA, 0x00, 0xFF).copyInto(yellow, n * ledsPerFlagColor)
                }

                val blue = IntArray(4 * ledsPerFlagColor)
                for (n in 0 until ledsPerFlagColor) {
                    intArrayOf(0x1B, 0xB3, 0xFF, 0xFF).copyInto(blue, n * ledsPerFlagColor)
                }

                val finalFlag = IntArray(4 * ledsPerFlagColor * stripes)
                pink.copyInto(finalFlag)
                yellow.copyInto(finalFlag, 4 * ledsPerFlagColor * 1)
                blue.copyInto(finalFlag, 4 * ledsPerFlagColor * 2)

                setColors(finalFlag)
            }
            ColorSets.RED -> { // 1510 red
                for (n in 0 until ledStripBuffer.size / 4) {
                    ledStripBuffer[n * 4] = 0xDF
                    ledStripBuffer[n * 4 + 1] = 0x00
                    ledStripBuffer[n * 4 + 2] = 0x0F
                }
            }
            ColorSets.BLUE -> { // 2898 blue
                for (n in 0 until ledStripBuffer.size / 4) {
                    ledStripBuffer[n * 4] = 0x1b
                    ledStripBuffer[n * 4 + 1] = 0x45
                    ledStripBuffer[n * 4 + 2] = 0x9b
                }
            }
            ColorSets.GREEN -> {
                for (n in 0 until ledStripBuffer.size / 4) {
                    ledStripBuffer[n * 4] = 0x00
                    ledStripBuffer[n * 4 + 1] = 0xFF
                    ledStripBuffer[n * 4 + 2] = 0x00
                }
            }
            ColorSets.ORANGE -> {
                for (n in 0 until ledStripBuffer.size / 4) {
                    ledStripBuffer[n * 4] = 0xFF
                    ledStripBuffer[n * 4 + 1] = 0xA5
                    ledStripBuffer[n * 4 + 2] = 0x00
                }
            }
            ColorSets.PINK -> {
                for (n in 0 until ledStripBuffer.size / 4) {
                    ledStripBuffer[n * 4] = 0xFF
                    ledStripBuffer[n * 4 + 1] = 0xC0
                    ledStripBuffer[n * 4 + 2] = 0xCB
                }
            }
        }
    }

    fun slideLeftEffect() {
        val temp = intArrayOf(ledStripBuffer[0], ledStripBuffer[1], ledStripBuffer[2], ledStripBuffer[3])
        for (n in 0 until ledStripBuffer.size / 4 - 1) {
            ledStripBuffer[n * 4] = ledStripBuffer[(n + 1) * 4]
            ledStripBuffer[n * 4 + 1] = ledStripBuffer[(n + 1) * 4 + 1]
            ledStripBuffer[n * 4 + 2] = ledStripBuffer[(n + 1) * 4 + 2]
            ledStripBuffer[n * 4 + 3] = ledStripBuffer[(n + 1) * 4 + 3]
        }
        ledStripBuffer[ledStripBuffer.size - 1] = temp[3]
        ledStripBuffer[ledStripBuffer.size - 2] = temp[2]
        ledStripBuffer[ledStripBuffer.size - 3] = temp[1]
        ledStripBuffer[ledStripBuffer.size - 4] = temp[0]
    }

    fun slideRightEffect() {
        val temp = IntArray(ledCount + 4)
        ledStripBuffer.copyInto(temp, 4)
        temp.copyInto(temp, 0, ledStripBuffer.size)
        temp.copyInto(ledStripBuffer, 0, 0, ledStripBuffer.size)
    }

    fun randomBlinkEffect() {
        for (n in 0 until ledStripBuffer.size / 4) {
            ledStripBuffer[n * 4 + 3] = Random.nextInt(0, 2) * 255
        }
    }

    fun randomBrightnessEffect() {
        for (n in 0 until ledStripBuffer.size / 4) {
            ledStripBuffer[n * 4 + 3] += Random.nextInt(-25, 25) // Epilepsy protection
            ledStripBuffer[n * 4 + 3] = ledStripBuffer[n * 4 + 3].coerceIn(0, 255)
        }
    }

    fun colorDriftEffect() {
        for (n in 0 until ledStripBuffer.size / 4) {
            val hsv = RGBtoHSV(ledStripBuffer[n * 4], ledStripBuffer[n * 4 + 1], ledStripBuffer[n * 4 + 2])
            hsv[0] = (hsv[0] + Random.nextInt(-5, 10)).mod(180)
            val rgb = HSVtoRGB(hsv[0], hsv[1], hsv[2])

            ledStripBuffer[n * 4] = rgb[0]
            ledStripBuffer[n * 4 + 1] = rgb[1]
            ledStripBuffer[n * 4 + 2] = rgb[2]
        }
    }

    fun turnOff() {
        toggle = false
    }

    fun turnOn() {
        toggle = true
    }

    fun setDataRGBA() {
        if (toggle) {
            for (n in 0..ledStripBuffer.size / 4) {
                val alpha = (ledStripBuffer[n * 4 + 3] + 1) / 255.0
                finalLEDStripBuffer.setRGB(
                    n,
                    (ledStripBuffer[n * 4] * alpha).toInt(),
                    (ledStripBuffer[n * 4 + 1] * alpha).toInt(),
                    (ledStripBuffer[n * 4 + 2] * alpha).toInt()
                )
            }
        } else {
            for (n in 0 until ledCount) {
                finalLEDStripBuffer.setRGB(n, 0, 0, 0)
            }
        }
    }

    override fun periodic() {
        ledStrip.setData(finalLEDStripBuffer)
    }
}