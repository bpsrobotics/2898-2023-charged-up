package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.m
import com.team2898.robot.commands.climb.ElevatorMove
import com.team2898.robot.commands.climb.PistonMove
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

object ClimbCommand : SequentialCommandGroup(
    ParallelCommandGroup(
        PistonMove(DoubleSolenoid.Value.kForward), // Prep piston for latch
        ElevatorMove(0.m) // Med bar climb
    ),
    PistonMove(DoubleSolenoid.Value.kReverse), // Position piston above bar
    ElevatorMove(1.m), // Drop robot until piston arm latches
    PistonMove(DoubleSolenoid.Value.kForward), // Tilt robot towards high bar
    ElevatorMove(2.m), // Extend elevator past high bar
    PistonMove(DoubleSolenoid.Value.kReverse), // Position elevator above high bar
    // This section will probably require a lot of fine-tuning and testing
    ElevatorMove(1.m), // Latch high bar, get piston off
    PistonMove(DoubleSolenoid.Value.kForward), // Piston out of way of med bar, free swing
    ElevatorMove(0.m), // Finish high bar climb
    PistonMove(DoubleSolenoid.Value.kReverse) // Reset piston arm to default
)