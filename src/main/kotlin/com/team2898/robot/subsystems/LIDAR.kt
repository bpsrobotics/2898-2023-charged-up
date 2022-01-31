package com.team2898.robot.subsystems

import com.cuforge.libcu.Lasershark
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase

object LIDAR : SubsystemBase() {
    private val shark = Lasershark(0)

    override fun periodic() {
        SmartDashboard.putNumber("shark distance (cm)", shark.distanceCentimeters)
    }
}