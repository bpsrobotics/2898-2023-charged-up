package com.team2898.robot.commands

//The commands for both the driver and operator
import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.Constants.ArmHeights.*
import com.team2898.robot.Field
import com.team2898.robot.OI
import com.team2898.robot.subsystems.Arm
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Intake
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.math.filter.SlewRateLimiter
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.wpilibj.PneumaticHub
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj.drive.DifferentialDrive.curvatureDriveIK
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.absoluteValue
import kotlin.math.min

class TeleOp : CommandBase() {

    init {
        addRequirements(Drivetrain)
    }

    // Called when the command is started.
    override fun initialize() {
        Drivetrain.mode = Drivetrain.Mode.OPEN_LOOP
        Intake
        PneumaticHub(42).enableCompressorDigital()
    }

    private val leftLimiter = SlewRateLimiter(10.0)
    private val rightLimiter = SlewRateLimiter(10.0)

    // Called every time the scheduler runs while the command is scheduled.
    override fun execute() {

        val turn = OI.turn * 0.5
        val speeds = when {
            //lessen speed when facing the community and getting close to the community


            // Quickturn buttons means turn in place
            OI.quickTurnRight - OI.quickTurnLeft != 0.0 -> DifferentialDrive.arcadeDriveIK(
                0.0,
                OI.quickTurnRight - OI.quickTurnLeft,
                false
            )

            // Otherwise, drive and turn normally
            else -> curvatureDriveIK(OI.throttle, turn, true)
        }

        val pitch = Odometry.NavxHolder.navx.pitch
        val kP = 0.003
        val ff = if (pitch.absoluteValue > 2.0) kP * pitch else 0.0
        val left = leftLimiter.calculate(speeds.left * 5.0)
        val right = rightLimiter.calculate(speeds.right * 5.0)
        Drivetrain.stupidDrive(`M/s`(left + ff), `M/s`(right + ff))

//      /** Make the alliance community zone a rectangle */
//        val pose = Odometry.pose
//        val distanceToCommunity = Field.map.gridWall.distance(pose) ?: 100.0
//        if (distanceToCommunity < 5.0) {
//            val maxAllowedSpeed = distanceToCommunity + 0.5
//
//            val cappedLeft = min(speeds.left, maxAllowedSpeed)
//            val cappedRight = min(speeds.right, maxAllowedSpeed)
//
//            Drivetrain.stupidDrive(`M/s`(cappedLeft * 5.0), `M/s`(cappedRight * 5.0))
//        } else {
//            Drivetrain.stupidDrive(`M/s`(speeds.left * 5.0), `M/s`(speeds.right * 5.0))
//        }

        if (OI.slowOuttake) {
            Intake.runOuttake(0.25)
        }
//        println(OI.highHat)
        when (OI.highHat) {
            in intArrayOf(315, 0, 45) -> {
//                println("out")
                Intake.runOuttake(0.75)
            }

            in intArrayOf(135, 180, 225) -> {
//                println("in")
                Intake.runIntake(0.75)
            }

            else -> {
//                println("stop")
                Intake.stopIntake()
            }
        }

        if (OI.brakeRelease) {
            Arm.brakeRelease()
        } else {
            val goalPosition = when {
                OI.floorIntake -> PICKUP
                OI.lowGoal -> LOWGOAL
                OI.midArmCube -> MIDDLEBOXGOAL
                OI.midArmCone -> MIDDLECONEGOAL
                OI.highArmCube -> HIGHCUBELAUNCH
                else -> null
            }
            if (goalPosition != null) {
                Arm.setGoal(goalPosition.position)
            }
        }

        when (OI.alignButton) {
            in intArrayOf(270) -> {}
            in intArrayOf(0) -> {}
            in intArrayOf(90) -> {}
        }
    }

    // Called once the command ends or is interrupted.
    override fun end(interrupted: Boolean) {
    }


    // Returns true when the command should end.
    override fun isFinished(): Boolean {
        return false
    }

    fun generatePath(dst: Pose2d): Trajectory {
        return Drivetrain.trajectoryMaker.builder()
            .start(Odometry.pose)
            .end(dst)
            .build()
    }
}
