package com.bpsrobotics.engine.utils

class LambdaInterpolation(val funct: (Double) -> Double): Interpolation {
    override fun interpolate(input: Double): Double {
        return funct(input)
    }
}