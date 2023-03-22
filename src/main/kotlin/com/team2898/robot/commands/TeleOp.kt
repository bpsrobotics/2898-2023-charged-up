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
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.PneumaticHub
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj.drive.DifferentialDrive.curvatureDriveIK
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.absoluteValue

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

    private val leftLimiter = SlewRateLimiter(100.0)
    private val rightLimiter = SlewRateLimiter(100.0)

    // Called every time the scheduler runs while the command is scheduled.
    override fun execute() {

        if (Drivetrain.mode == Drivetrain.Mode.CLOSED_LOOP) {
            // Auto Allign is being pressed: if D-PAD inactive, stop following path
            if (OI.alignmentPad == OI.Direction.INACTIVE) {
                Drivetrain.mode = Drivetrain.Mode.OPEN_LOOP
                Drivetrain.fullStop()
            }
        } else if (OI.alignmentPad != OI.Direction.INACTIVE) { // D-PAD is being pressed
            if (Field.map.facing_community(Odometry.pose)) alignGrid()  //If robot is facing community, align to the grid.
        } else {
            val turn = OI.turn * 0.5
            Drivetrain.coastMode()
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
            val left = speeds.left
            val right = speeds.right
            SmartDashboard.putNumber("l output", left)
            SmartDashboard.putNumber("r output", right)
            SmartDashboard.putNumber("turn", turn)
            SmartDashboard.putNumber("throttle", OI.throttle)
            Drivetrain.stupidDrive(`M/s`(left), `M/s`(right))
        }

        when (OI.highHat) {
            in intArrayOf(315, 0, 45) -> Intake.runOuttake(0.5)
            in intArrayOf(135, 180, 225) -> Intake.runIntake(0.75)
            else -> if (OI.operatorTrigger) Intake.runIntake(0.3)
                    else Intake.stopIntake()
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
                OI.moving -> MOVING
                else -> null
            }
            if (goalPosition != null) {
                Arm.setGoal(goalPosition.position)
            }
        }


    }
    fun generatePath(dst: Pose2d): Trajectory {
        return Drivetrain.trajectoryMaker.builder()
                .start(Odometry.pose)
                .end(dst)
                .build()
    }
    fun alignGrid() {
        val grid = Field.map.scoring.getFacedGrid(Odometry.pose, Field.map.rotation)
        var dir = OI.alignmentPad
        if (Field.teamColor != DriverStation.Alliance.Blue) dir = dir.mirrored()
        val scoreSpot = when(dir) {
            OI.Direction.RIGHT -> grid.Cone1
            OI.Direction.LEFT  -> grid.Cone2
            else               -> grid.Cube
        }
        val targetPose = scoreSpot.RobotPosition.toPose2d(Field.map.rotation)
        val path = generatePath(targetPose)
        Drivetrain.follow(path)
    }

    // Called once the command ends or is interrupted.
    override fun end(interrupted: Boolean) {
    }


    // Returns true when the command should end.
    override fun isFinished(): Boolean {
        return false
    }
}
