package org.team100.frc2023.control;

import org.team100.frc2023.autonomous.DriveToWaypoint2;
import org.team100.frc2023.autonomous.MoveConeWidth;
import org.team100.frc2023.autonomous.Rotate;
import org.team100.frc2023.commands.AutoLevel;
import org.team100.frc2023.commands.Defense;
import org.team100.frc2023.commands.DriveMedium;
import org.team100.frc2023.commands.DriveSlow;
import org.team100.frc2023.commands.GoalOffset;
import org.team100.frc2023.commands.ResetPose;
import org.team100.frc2023.commands.ResetRotation;
import org.team100.frc2023.commands.RumbleOn;
import org.team100.frc2023.commands.Arm.ArmTrajectory;
import org.team100.frc2023.commands.Arm.Oscillate;
import org.team100.frc2023.commands.Arm.SetConeMode;
import org.team100.frc2023.commands.Arm.SetCubeMode;
import org.team100.frc2023.commands.Manipulator.Close;
import org.team100.frc2023.commands.Manipulator.CloseSlow;
import org.team100.frc2023.commands.Manipulator.Home;
import org.team100.frc2023.commands.Manipulator.Open;
import org.team100.frc2023.commands.Retro.DriveToRetroReflectiveTape;
import org.team100.frc2023.commands.Retro.LedOn;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.event.BooleanEvent;
import edu.wpi.first.wpilibj.event.EventLoop;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * The RC joystick thing joel made.
 * X, Y, and twist should work.
 * POV rotation should work.
 * Only one joystick is required.
 * Operator features are not implemented.
 * Command buttons are not implemented.
 */
public class Pilot implements Control, Sendable {
    private final CommandGenericHID m_controller;
    private Rotation2d previousRotation = new Rotation2d(0);

    public Pilot() {
        m_controller = new CommandGenericHID(0);
        System.out.printf("Controller0: %s\n", m_controller.getHID().getName());
        SmartDashboard.putData("Robot Container", this);
    }

    @Override
    public void driveToLeftGrid(DriveToWaypoint2 command) {
    }

    @Override
    public void autoLevel(AutoLevel command) {
    }

    @Override
    public void driveToCenterGrid(DriveToWaypoint2 command) {
    }

    @Override
    public void driveToRightGrid(DriveToWaypoint2 command) {
    }

    @Override
    public void driveToSubstation(DriveToWaypoint2 command) {
    }

    @Override
    public void resetRotation0(ResetRotation command) {
        JoystickButton startButton = new JoystickButton(m_controller.getHID(), 2);
        startButton.onTrue(command);
    }

    @Override
    public void resetRotation180(ResetRotation command) {
        JoystickButton startButton = new JoystickButton(m_controller.getHID(), 3);
        startButton.onTrue(command);
    }

    @Override
    public double rotSpeed() {
        // there is no rotational velocity control.
        return 0.0;
    }

    @Override
    public double ySpeed() {
        return -1.0 * m_controller.getHID().getRawAxis(0);
    }

    @Override
    public double xSpeed() {
        return -1.0 * m_controller.getHID().getRawAxis(1);
    }

    @Override
    public Trigger trigger() {
        EventLoop loop = CommandScheduler.getInstance().getDefaultButtonLoop();
        BooleanEvent event = new BooleanEvent(loop, () -> m_controller.getHID().getRawButton(1));
        return event.castTo(Trigger::new);
    }

    @Override
    public Trigger thumb() {
        EventLoop loop = CommandScheduler.getInstance().getDefaultButtonLoop();
        BooleanEvent event = new BooleanEvent(loop, () -> m_controller.getHID().getRawButton(2));
        return event.castTo(Trigger::new);
    }

    @Override
    public void driveSlow(DriveSlow command) {
    }

    @Override
    public void resetPose(ResetPose command) {
    }

    @Override
    public Rotation2d desiredRotation() {
        // the control goes from -1 to 1 in one turn
        double rotControl = m_controller.getHID().getRawAxis(5);
        previousRotation = Rotation2d.fromRotations(rotControl / 2);
        return previousRotation;
    }

    @Override
    public GoalOffset goalOffset() {
        return GoalOffset.center;
    }

    @Override
    public void defense(Defense defense) {
    }

    @Override
    public void rumbleOn() {
    }

    @Override
    public void rumbleTrigger(RumbleOn command) {
    }

    @Override
    public void rumbleOff() {
    }

    @Override
    public void rotate0(Rotate command) {
    }

    @Override
    public void driveMedium(DriveMedium command) {
    }

    @Override
    public void moveConeWidthLeft(MoveConeWidth command) {
    }

    @Override
    public void moveConeWidthRight(MoveConeWidth command) {
    }

    @Override
    public double openSpeed() {
        return 0;
    }

    @Override
    public double closeSpeed() {
        return 0;
    }

    @Override
    public double lowerSpeed() {
        return 0;
    }

    @Override
    public double upperSpeed() {
        return 0;
    }

    @Override
    public void armHigh(ArmTrajectory command) {
    }

    @Override
    public void armLow(ArmTrajectory command) {
    }

    @Override
    public void armSafe(ArmTrajectory command) {
    }

    @Override
    public void safeWaypoint(ArmTrajectory command) {
    }

    @Override
    public void armSafeSequential(ArmTrajectory command, ArmTrajectory command2) {
    }

    @Override
    public void armSafeBack(ArmTrajectory command) {
    }

    @Override
    public void closeSlow(CloseSlow command) {
    }

    @Override
    public void armSubstation(ArmTrajectory command) {
    }

    @Override
    public void armMid(ArmTrajectory command) {
    }

    @Override
    public void open(Open command) {
    }

    @Override
    public void home(Home command) {
    }

    @Override
    public void close(Close command) {
    }

    @Override
    public void cubeMode(SetCubeMode command) {
    }

    @Override
    public void coneMode(SetConeMode command) {
    }

    @Override
    public void armToSub(ArmTrajectory command) {
    }

    @Override
    public void ledOn(LedOn command) {
    }

    @Override
    public void oscillate(Oscillate command) {
    }

    @Override
    public void tapeDetect(DriveToRetroReflectiveTape command) {
    }

    @Override
    public void armSubSafe(ArmTrajectory command) {
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("joystick control");
        // builder.addDoubleProperty("right y", () -> controller0.getRightY(), null);
        // builder.addDoubleProperty("right x", () -> controller0.getRightX(), null);
        // builder.addDoubleProperty("left x", () -> controller0.getLeftX(), null);
        // builder.addDoubleProperty("x limited", () -> xLimited(), null);
        // builder.addDoubleProperty("y limtied", () -> yLimited(), null);
        // builder.addDoubleProperty("rot Limited", () -> rotLimited(), null);
    }

}