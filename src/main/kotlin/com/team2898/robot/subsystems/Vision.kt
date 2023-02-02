package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.Polynomial
import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Confidence {
    //TODO: Place in actual values and find degree of polynomial
    private const val a = 0.0
    private const val b = 0.0
    private const val c = 0.0
    private const val d = 0.0

    //TODO: Properly set up the appropriate coefficients for each polynomial
    private val yDistPolynomial = Polynomial(a,b,c,d)
    private val xDistPolynomial = Polynomial(a,b,c,d)
    private val speedPolynomial = Polynomial(a,b,c,d)

    operator fun get(x: Double) : Double {
        return 0.0
    }

    /** Returns the standard deviation. The higher the value, the less confident we are*/
    fun getConfindence(x_dist: Double, y_dist: Double, speed: Double = 0.0): Double {
//        val xDistOutput = xDistPolynomial[x_dist]
//        val yDistOutput = yDistPolynomial[y_dist]
//        val speedOutput = speedPolynomial[speed]
//        return xDistOutput + yDistOutput + speedOutput
        TODO()
    }
}


object Vision : SubsystemBase() {
    var currentRobotPose = Pose3d()
}
