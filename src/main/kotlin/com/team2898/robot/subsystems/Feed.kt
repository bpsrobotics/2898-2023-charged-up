// package com.team2898.robot.subsystems

// import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
// import com.cuforge.libcu.Lasershark
// import edu.wpi.first.wpilibj2.command.SubsystemBase

// object Feed : SubsystemBase() {

//    private val ballDetector = Lasershark(z)
//    // x and y are the CAN IDs of the sparks
//    private val topMotor = WPI_TalonSRX(x)
//    private val bottomMotor = WPI_TalonSRX(y)

//    enum class Mode {
//        IDLE, FEED
//    }

//    private var state = Mode.IDLE

//    private fun idleMotors() {
//        topMotor.set(0.0)
//        bottomMotor.set(0.0)
//    }

//    // we can determine the top motor's speed and the bottom motor's speed with some testing
//    private fun feedBall() {
//        if (ballDetector.getDistanceCentimeters()>2.0){
//            topMotor.set(TOP_SPEED)
//            bottomMotor.set(BOTTOM_SPEED)
//        }
//        else {
//            topMotor.set(0.0)
//            bottomMotor.set(0.0)
//            state = Mode.IDLE
//        }
//    }

//    fun changeState(newState: Mode) {
//        if (newState != state) {
//            state = newState
//        }
//    }

//    override fun periodic() {
//        when (state) {
//            Mode.IDLE -> {
//                idleMotors()
//            }
//            Mode.FEED -> {
//                feedBall()
//            }
//        }
//    }
// }