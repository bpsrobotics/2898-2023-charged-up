package com.bpsrobotics.engine.utils.interpolations

class PolynomialInterpolation(val coefficients: List<PolynomialCoefficient>): Interpolation {
    data class PolynomialCoefficient(val coefficient: Double, val exponent: Double){
        fun Calculate(value: Double): Double{
            return coefficient*Math.pow(value, exponent)
        }
    }
    override fun interpolate(input: Double): Double{
        var sum = 0.0
        for (term in coefficients){
            sum += term.Calculate(input)
        }
        return sum
    }
}