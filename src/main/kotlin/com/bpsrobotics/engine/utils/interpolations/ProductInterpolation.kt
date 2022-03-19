package com.bpsrobotics.engine.utils.interpolations

/** A Complex Interpolation that takes the product of multiple interpolations
 *
 */
class ProductInterpolation(val interpolations: List<Interpolation>): Interpolation {
    override fun interpolate(input: Double): Double {
        var prod = 1.0
        for(term in interpolations){
            prod *= term.interpolate(input)
        }
        return prod
    }
}