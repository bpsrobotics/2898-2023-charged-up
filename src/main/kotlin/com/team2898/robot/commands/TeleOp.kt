package com.team2898.robot.commands

//The commands for both the driver and operator
import com.bpsrobotics.engine.utils.`M/s`
import com.bpsrobotics.engine.utils.Sugar.angleDifference
import com.bpsrobotics.engine.utils.Sugar.degreesToRadians
import com.bpsrobotics.engine.utils.geometry.Rectangle
import com.team2898.robot.Field
import com.team2898.robot.OI
import com.team2898.robot.OI.operatorTrigger
import com.team2898.robot.subsystems.Arm
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Intake
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj.drive.DifferentialDrive.curvatureDriveIK
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.PneumaticHub
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Commands
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

            OI.parallelButton -> return

            // Otherwise, drive and turn normally
            else -> curvatureDriveIK(OI.throttle, turn, true)
        }

        val pitch = Odometry.NavxHolder.navx.pitch
        val kP = 0.003
        val ff = if (pitch.absoluteValue > 2.0) kP * pitch else 0.0
        Drivetrain.stupidDrive(`M/s`(speeds.left * 5.0 + ff), `M/s`(speeds.right * 5.0 + ff))


/**      Make the alliance community zone a rectangle */
        val pose = Odometry.pose
        val distanceToCommunity = Field.map.gridWall.distance(pose) ?: 100.0
        if (distanceToCommunity < 5.0) {
            val maxAllowedSpeed = distanceToCommunity + 0.5

            val cappedLeft = min(speeds.left, maxAllowedSpeed)
            val cappedRight = min(speeds.right, maxAllowedSpeed)

            Drivetrain.stupidDrive(`M/s`(cappedLeft * 5.0), `M/s`(cappedRight * 5.0))
        } else {
            Drivetrain.stupidDrive(`M/s`(speeds.left * 5.0), `M/s`(speeds.right * 5.0))
        }


        if (OI.highHat > 270 || OI.highHat < 90)  {
            Intake.runOuttake()
        }
        else if (OI.highHat in 91 until  270) {
            Intake.runIntake()
        }
    }

    // Called once the command ends or is interrupted.
    override fun end(interrupted: Boolean) {
    }


    // Returns true when the command should end.
    override fun isFinished(): Boolean {
        return false
    }


}
