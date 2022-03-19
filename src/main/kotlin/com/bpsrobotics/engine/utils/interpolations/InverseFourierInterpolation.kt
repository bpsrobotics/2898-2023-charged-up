package com.bpsrobotics.engine.utils.interpolations

import kotlin.math.sin

class InverseFourierInterpolation(val terms: List<InverseFourierTerm>): Interpolation {
    data class InverseFourierTerm(val coefficient: Double, val frequency: Double){
        fun calculate(input: Double): Double{
            return coefficient*sin(frequency * input)
        }
    }

    override fun interpolate(input: Double): Double {
        var sum = 0.0
        for(term in terms){
            sum += term.calculate(input)
        }
        return sum
    }
}