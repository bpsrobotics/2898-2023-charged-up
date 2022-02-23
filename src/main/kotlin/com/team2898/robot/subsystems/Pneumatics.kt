package com.team2898.robot.subsystems

import edu.wpi.first.wpilibj.Compressor
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.PneumaticsModuleType.REVPH

object Pneumatics {
    init {
        Compressor(REVPH)
    }

    val testSolenoid = DoubleSolenoid(REVPH, 1, 2)
}
