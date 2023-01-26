package com.team2898.robot.subsystems

import com.team2898.robot.RobotMap
import com.team2898.robot.RobotMap.INTAKE_BREAK_BACKWARD
import com.team2898.robot.RobotMap.INTAKE_BREAK_FORWARD
import com.team2898.robot.RobotMap.PNEUMATICS_MODULE_TYPE
import com.team2898.robot.RobotMap.PNUEMATICS_MODULE
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Intake: SubsystemBase() {
    private val
    init {
    }
    override fun {

    }

    val intakePneumatic = DoubleSolenoid(PNUEMATICS_MODULE, PNEUMATICS_MODULE_TYPE, INTAKE_BREAK_FORWARD, INTAKE_BREAK_BACKWARD)

}