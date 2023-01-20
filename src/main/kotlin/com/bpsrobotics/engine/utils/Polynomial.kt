package com.bpsrobotics.engine.utils
import kotlin.math.pow

class Polynomial(vararg val coefficients: Double) {
    /** Degree of the polynomial*/
    val degree = coefficients.size

    /**Returns the output of a polynomial given X*/
    operator fun get(x: Double): Double {
        var returnValue = 0.0;
        for ((i, k) in coefficients.withIndex()) {
            returnValue += k * x.pow(i)
        }
        return returnValue
    }


//    init {
//        for(item in coefficients){
//            this["a"] = item
//        }
//    }

}