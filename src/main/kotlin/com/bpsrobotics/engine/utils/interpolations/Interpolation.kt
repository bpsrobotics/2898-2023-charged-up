package com.bpsrobotics.engine.utils.interpolations

interface Interpolation {
    fun interpolate(input: Double): Double
}