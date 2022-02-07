package com.team2898.robot.subsystems

import edu.wpi.first.wpilibj.AddressableLED
import edu.wpi.first.wpilibj.AddressableLEDBuffer
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.random.Random

object RGBLEDHandler : SubsystemBase() {
    private val ledCount = 200 // TODO: How many WS2812s do we have
    private val ledStrip = AddressableLED(8) // TODO: PWM Port Constant
    private var finalLEDStripBuffer = AddressableLEDBuffer(ledCount)
    private var toggle = true

    private val ledStripBuffer = IntArray(ledCount * 4) // RGBA
    private val currentColorSet = ArrayList<Int>(30)

    init {
        ledStrip.setData(finalLEDStripBuffer)
        ledStrip.start()
    }

    fun slideEffect(data: IntArray) {
        val temp = intArrayOf(data[0], data[1], data[2], data[3])
        for (n in 0 until data.size/4 - 1) {
            data[n * 4] = data[(n + 1) * 4]
            data[n * 4 + 1] = data[(n + 1) * 4 + 1]
            data[n * 4 + 2] = data[(n + 1) * 4 + 2]
            data[n * 4 + 3] = data[(n + 1) * 4 + 3]
        }
        data[data.size - 1] = temp[3]
        data[data.size - 2] = temp[2]
        data[data.size - 3] = temp[1]
        data[data.size - 4] = temp[0]
    }

    fun randomBlinkEffect(data: IntArray) {
        for (n in 0 until data.size/4) {
            data[n * 4 + 3] = Random.nextInt(0, 2) * 255
        }
    }

    fun randomBrightnessEffect(data: IntArray) {
        for (n in 0 until data.size/4) {
            data[n * 4 + 3] = Random.nextInt(0, 256)
        }
    }

    fun turnOff() {
        toggle = false
    }

    fun turnOn() {
        toggle = true
    }

    fun setDataRGBA(data: IntArray) {
        if (toggle) {
            for (n in 0..data.size / 4) {
                val alpha = data[n * 4 + 3] + 1
                finalLEDStripBuffer.setRGB(n, data[n * 4]/alpha, data[n * 4 + 1]/alpha, data[n * 4 + 2]/alpha)
            }
        }
        else {
            finalLEDStripBuffer = AddressableLEDBuffer(ledCount)
        }
    }

    override fun periodic() {

    }
}