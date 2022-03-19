package com.bpsrobotics.engine.utils

import kotlin.math.exp

/**
 * An interpolation of the form Ae^(Bx). An exponential of form A*B^(Cx) is equivalent to A*e^(ln(B)Cx)
 */
class ExponentialInterpolation(val A: Double, val B: Double): Interpolation {
    override fun interpolate(input: Double): Double {
        return A*exp(B*input)
    }
}