package com.team2898.robot.commands

//The commands for both the driver and operator
import com.bpsrobotics.engine.utils.`M/s`
import com.bpsrobotics.engine.utils.Rectangle
import com.team2898.robot.OI
import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj.drive.DifferentialDrive.curvatureDriveIK
import com.team2898.robot.commands.AutoBalance
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.DriverStation.Alliance
import edu.wpi.first.wpilibj.drive.DifferentialDrive.arcadeDriveIK
import kotlin.math.atan2
import kotlin.math.min

class TeleOp : CommandBase() {

    init {
        addRequirements(Drivetrain)
    }

    // Called when the command is started.
    override fun initialize() {
        Drivetrain.mode = Drivetrain.Mode.OPEN_LOOP
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


        val communityCoords = if (DriverStation.getAlliance() == DriverStation.Alliance.Blue) {
            // TODO: Replace placeholder coordinates with real coordinates
            Rectangle(0.0, 0.0, 0.0, 0.0)
        } else {
//        do red community rectangle
            Rectangle(0.0, 0.0, 0.0, 0.0)
        }
//      Make the alliance community zone a rectangle
        val pose = Odometry.pose

        val xdistToCommunity = pose.x - communityCoords.x1
//        var ydistToCommunity = pose.y - allianceYCommunitypos
        if ((pose in communityCoords && (pose.rotation.degrees in 170.0..190.0)) || (pose in communityCoords && (pose.rotation.degrees in -170.0..-190.0))) {
            // TODO: what do if pose loops around i.e 360 into 540 or -540
            val maxAllowedSpeed = 5.0 - xdistToCommunity

            val cappedLeft = min(speeds.left, maxAllowedSpeed)
            val cappedRight = min(speeds.right, maxAllowedSpeed)
//
            Drivetrain.stupidDrive(`M/s`(cappedLeft * 5.0), `M/s`(cappedRight * 5.0))
        } else {
            Drivetrain.stupidDrive(`M/s`(speeds.left * 5.0), `M/s`(speeds.right * 5.0))
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
