package com.bpsrobotics.engine.utils.interpolations


/**
 * A complex interpolation consisting of the sum of multiple interpolations
 */

class SumInterpolation(val interpolations: List<Interpolation>): Interpolation {
    override fun interpolate(input: Double): Double {
        var sum = 0.0
        for(term in interpolations){
            sum += term.interpolate(input)
        }
        return sum
    }
}