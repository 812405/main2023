package team100.control;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.autonomous.DriveToWaypoint2;
import frc.robot.commands.DriveRotation;
import frc.robot.commands.GoalOffset;
import frc.robot.commands.ResetPose;
import frc.robot.commands.ResetRotation;
import frc.robot.commands.Arm.ArmTrajectory;
import frc.robot.commands.Arm.DriveToSetpoint;
import frc.robot.commands.Manipulator.Close;
import frc.robot.commands.Manipulator.Home;
import frc.robot.commands.Manipulator.Open;

/**
 * see
 * https://docs.google.com/document/d/1M89x_IiguQdY0VhQlOjqADMa6SYVp202TTuXZ1Ps280/edit#
 */
public class DualXboxControl implements Sendable {
    private static final double kDtSeconds = 0.02;
    private static final double kMaxRotationRateRadiansPerSecond = Math.PI;
    private static final double kTriggerThreshold = .5;

    private final CommandXboxController controller0;
    private final CommandXboxController controller1;
    Rotation2d previousRotation = new Rotation2d(0);

    public DualXboxControl() {
        controller0 = new CommandXboxController(0);
        System.out.printf("Controller0: %s\n", controller0.getHID().getName());
        controller1 = new CommandXboxController(1);
        System.out.printf("Controller1: %s\n", controller1.getHID().getName());
        SmartDashboard.putData("Robot Container", this);
    }

    ///////////////////////////////
    //
    // DRIVER: manual driving and auto navigation controls

    public void driveToLeftGrid(DriveToWaypoint2 command) {
        controller0.x().whileTrue(command);
    };

    public void driveToCenterGrid(DriveToWaypoint2 command) {
        controller0.a().whileTrue(command);
    };

    public void driveToRightGrid(DriveToWaypoint2 command) {
        controller0.b().whileTrue(command);
    };

    public void driveToSubstation(DriveToWaypoint2 command) {
        controller0.y().whileTrue(command);
    };

    public void resetRotation(ResetRotation command) {
        controller0.rightBumper().onTrue(command);
    }

    public void autoLevel(frc.robot.commands.autoLevel command) {
        // controller0.y().whileTrue(command);
    }

    public void driveRotation(DriveRotation command) {
        controller0.rightBumper().whileTrue(command);
    }

    /** @return [-1,1] */
    public double xSpeed() {
        return -1.0 * controller0.getRightY();
    }

    /** @return [-1,1] */
    public double ySpeed() {
        return -1.0 * controller0.getRightX();
    }

    /** @return [-1,1] */
    public double rotSpeed() {
        return -1.0 * controller0.getLeftX();
    }

    public void resetPose(ResetPose command) {
        controller0.leftBumper().onTrue(command);
    }

    public Rotation2d desiredRotation() {
        double desiredAngleDegrees = controller0.getHID().getPOV();
        if (desiredAngleDegrees < 0) { // no POV input
            double stickInput = MathUtil.applyDeadband(controller0.getLeftX(), 0.05);
            double desiredRateRadiansPerSecond = stickInput * kMaxRotationRateRadiansPerSecond;
            Rotation2d dRotation = new Rotation2d(desiredRateRadiansPerSecond * kDtSeconds);
            previousRotation = previousRotation.minus(dRotation);
            return previousRotation;
        }
        previousRotation = Rotation2d.fromDegrees(-1.0 * desiredAngleDegrees);
        return previousRotation;
    }

    public GoalOffset goalOffset() {
        double left = controller0.getLeftTriggerAxis();
        double right = controller0.getRightTriggerAxis();
        if (left > kTriggerThreshold) {
            if (right > kTriggerThreshold) {
                return GoalOffset.center;
            }
            return GoalOffset.left;
        }
        if (right > kTriggerThreshold) {
            return GoalOffset.right;
        }
        return GoalOffset.center;
    }

    ///////////////////////////////
    //
    // OPERATOR: arm and manipulator controls

    /** @return [-1,1] */
    public double openSpeed() {
        return controller1.getRightTriggerAxis();
    }

    /** @return [-1,1] */
    public double closeSpeed() {
        return controller1.getLeftTriggerAxis();
    }

    /** @return [-1,1] */
    public double lowerSpeed() {
        return controller1.getRightX();
    }

    /** @return [-1,1] */
    public double upperSpeed() {
        return controller1.getLeftY();
    }

    public void driveToHigh(DriveToSetpoint command) {
        controller1.y().whileTrue(command);
    }

    public void driveToSafe(SequentialCommandGroup command) {
        controller1.rightBumper().whileTrue(command);
    }

    // TODO: remove this
    public XboxController getController() {
        return controller1.getHID();
    }

    public void armHigh(ArmTrajectory command) {
        controller1.povUp().whileTrue(command);
    }

    public void armSafe(ArmTrajectory command) {
        controller1.povDown().whileTrue(command);
    }

    public void open(Open command) {
        controller1.a().whileTrue(command);
    }

    public void home(Home command) {
        controller1.b().whileTrue(command);
    }

    public void close(Close command) {
        controller1.x().whileTrue(command);
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("xbox control");
        builder.addDoubleProperty("right y", () -> controller0.getRightY(), null);
        builder.addDoubleProperty("right x", () -> controller0.getRightX(), null);
        builder.addDoubleProperty("left x", () -> controller0.getLeftX(), null);
    }
}