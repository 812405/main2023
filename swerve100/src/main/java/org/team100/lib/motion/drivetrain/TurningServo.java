package org.team100.lib.motion.drivetrain;

import org.team100.lib.encoder.turning.TurningEncoder;
import org.team100.lib.motor.turning.TurningMotor;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Feedforward and feedback control of a single turning motor. */
public class TurningServo implements Sendable {
    public static class Config {
        public double kSteeringDeadband = 0.03;
    }

    private final Config m_config = new Config();
    private final TurningMotor m_turningMotor;
    private final TurningEncoder m_turningEncoder;
    private final ProfiledPIDController m_turningController;
    private final SimpleMotorFeedforward m_turningFeedforward;

    public double turningFeedForwardOutput;
    public double turningMotorControllerOutput;

    public TurningServo(
            String name,
            TurningMotor turningMotor,
            TurningEncoder turningEncoder,
            ProfiledPIDController turningController,
            SimpleMotorFeedforward turningFeedforward) {
        m_turningMotor = turningMotor;
        m_turningEncoder = turningEncoder;
        m_turningController = turningController;
        m_turningFeedforward = turningFeedforward;
        SmartDashboard.putData(String.format("Swerve TurningServo %s", name), this);
    }

    void setTurning(SwerveModuleState state) {
        turningMotorControllerOutput = m_turningController.calculate(getTurningAngleRad(), state.angle.getRadians());
        turningFeedForwardOutput = m_turningFeedforward.calculate(getTurnSetpointVelocityRadS(), 0);
        double turnOutput = turningMotorControllerOutput + turningFeedForwardOutput;
        // output deadband to prevent shivering.
        m_turningMotor.set(MathUtil.applyDeadband(turnOutput, m_config.kSteeringDeadband));
    }

    void set(double output) {
        m_turningMotor.set(output);
    }

    double getTurnSetpointVelocityRadS() {
        return m_turningController.getSetpoint().velocity;
    }

    double getTurningAngleRad() {
        return m_turningEncoder.getAngle();
    }

    Rotation2d getTurningRotation() {
        return new Rotation2d(getTurningAngleRad());
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.addDoubleProperty("Turning Angle (rad)", () -> getTurningAngleRad(), null);
        builder.addDoubleProperty("Turning Angle (deg)", () -> Units.radiansToDegrees(getTurningAngleRad()), null);

        builder.addDoubleProperty("Turning Goal (rad)", () -> m_turningController.getGoal().position, null);
        builder.addDoubleProperty("Turning Setpoint (rad)", () -> m_turningController.getSetpoint().position, null);
        builder.addDoubleProperty("Turning Setpoint Velocity (rad/s)", this::getTurnSetpointVelocityRadS, null);
        builder.addDoubleProperty("Turning Position Error (rad)", () -> m_turningController.getPositionError(), null);
        builder.addDoubleProperty("Turning Velocity Error (rad/s)", () -> m_turningController.getVelocityError(), null);

        builder.addDoubleProperty("Controller Output", () -> turningMotorControllerOutput, null);
        builder.addDoubleProperty("Feed Forward Output", () -> turningFeedForwardOutput, null);

        builder.addDoubleProperty("Turning Motor Output [-1, 1]", () -> m_turningMotor.get(), null);

    }

}