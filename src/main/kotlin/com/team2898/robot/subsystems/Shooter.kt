package com.team2898.robot.subsystems

import com.bpsrobotics.engine.controls.Controller
import com.bpsrobotics.engine.utils.*
import com.bpsrobotics.engine.utils.MovingAverage.Companion.movingAverage
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMax.IdleMode.kCoast
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless
import com.team2898.robot.Constants.DUMP_SPEED
import com.team2898.robot.RobotMap.SHOOTER_FLYWHEEL
import com.team2898.robot.RobotMap.SHOOTER_SPINNER
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.min

object Shooter : SubsystemBase() {
    private val flywheelController = CANSparkMax(SHOOTER_FLYWHEEL, kBrushless)
    private val spinnerController = CANSparkMax(SHOOTER_SPINNER, kBrushless)
    private var spunUpTime = 0.seconds

    val ready get() = (rawFlywheelSpeed - target.flywheel).value.absoluteValue < 10 &&
                (rawSpinnerSpeed - target.spinner).value.absoluteValue < 10 &&
                min(abs(rawFlywheelSpeed.value), abs(rawSpinnerSpeed.value)) > 10

    var target = ShooterSpeeds(0.RPM, 0.RPM)
    var shooterPower = ShooterPowers(0.0, 0.0)

    data class ShooterSpeeds(val flywheel: RPM, val spinner: RPM)
    data class ShooterPowers(val flywheel: Double, val spinner: Double)

    private var lastShooterPos = 0.0
    private var lastSpinnerPos = 0.0
    private val timer = Timer()
    init {
        timer.start()
    }

    private val shooterPID = Controller.PID(TODO(), TODO())
    private val shooterFF = SimpleMotorFeedforward(TODO(), TODO(), TODO())
    private val spinnerPID = Controller.PID(TODO(), TODO())
    private val spinnerFF = SimpleMotorFeedforward(TODO(), TODO(), TODO())

//    init {
//        spinnerEncoder.distancePerPulse = 1.0 / 256
//    }

    private val rawFlywheelSpeed get() = ((flywheelController.encoder.position - lastShooterPos) / timer.get() * 60).RPM
    private val rawSpinnerSpeed get() = ((spinnerController.encoder.position - lastSpinnerPos) / timer.get() * 60).RPM
    private val filteredShooterSpeed = MovingAverage(3)
    private val filteredSpinnerSpeed = MovingAverage(3)

    init {
        listOf(flywheelController, spinnerController).forEach {
            it.restoreFactoryDefaults()
            it.setSmartCurrentLimit(20)
            it.idleMode = kCoast
            it.inverted = true
        }

        flywheelController.pidController.ff = 0.0
        flywheelController.pidController.p  = 0.0
        flywheelController.pidController.i  = 0.0
        flywheelController.pidController.d  = 0.0
    }

    fun spinUp(speeds: ShooterPowers = TargetAlignUtils.getPowers()) {
        spunUpTime = Timer.getFPGATimestamp().seconds + 5.seconds
        shooterPower = speeds
        notMaxSpeed()
    }

    fun dumpSpinUp() {
        spinUp(DUMP_SPEED)
    }

    fun stopShooter() {
        spunUpTime = Timer.getFPGATimestamp().seconds
        notMaxSpeed()
    }

    private var maxSpeed = false

    fun maxSpeed() {
        listOf(flywheelController, spinnerController).forEach {
            it.setSmartCurrentLimit(40)
        }
        maxSpeed = true
    }

    fun notMaxSpeed() {
        if (!maxSpeed) return
        listOf(flywheelController, spinnerController).forEach {
            it.setSmartCurrentLimit(20)
        }
        maxSpeed = false
    }

    override fun periodic() {
        if (maxSpeed) {
            flywheelController.set(1.0)
            spinnerController.set(1.0)
        } else if (Timer.getFPGATimestamp() < spunUpTime.value) {
            var pid = spinnerPID.calculate(filteredSpinnerSpeed.average, target.spinner.value)
            var ff = spinnerFF.calculate(target.spinner.value)
            spinnerController.setVoltage((pid + ff) * 12)

            pid = shooterPID.calculate(filteredShooterSpeed.average, target.flywheel.value)
            ff = shooterFF.calculate(target.flywheel.value)
            flywheelController.setVoltage((pid + ff) * 12)
        } else {
            flywheelController.set(0.0)
            spinnerController.set(0.0)
        }
        filteredShooterSpeed.add(rawFlywheelSpeed.value)
        filteredSpinnerSpeed.add(rawSpinnerSpeed.value)

        lastShooterPos = flywheelController.encoder.position
        lastSpinnerPos = spinnerController.encoder.position
        timer.reset()
    }

    override fun initSendable(builder: SendableBuilder) {
        builder.setSmartDashboardType("Subsystem")
        builder.addDoubleProperty("shooter RPM", { rawFlywheelSpeed.value }) {}
        builder.addDoubleProperty("spinner RPM", { rawSpinnerSpeed.value }) {}
        builder.addDoubleProperty("shooter target", { target.flywheel.value }) {}
        builder.addDoubleProperty("spinner target", { target.spinner.value }) {}
    }
}
