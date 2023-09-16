package org.team100.frc2023.control;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class VKBControl implements Control{

    private final CommandJoystick m_controller;
    private Rotation2d previousRotation = new Rotation2d(0);
    
    public VKBControl() {
        m_controller = new CommandJoystick(0);
        System.out.printf("Controller0: %s\n", m_controller.getHID().getName());
    }

    @Override
    public Twist2d twist() {
        double dx = m_controller.getX();
        double dy = m_controller.getY();
        double dtheta = m_controller.getTwist();
        return new Twist2d(dx, dy, dtheta);
    }

    @Override
    public Rotation2d desiredRotation() {
        double desiredAngleDegrees = m_controller.getHID().getPOV(1);
        if (desiredAngleDegrees < 0) {
            return null;
        }
        previousRotation = Rotation2d.fromDegrees(-1.0 * desiredAngleDegrees);
        return previousRotation;
    }

    @Override
    public void resetRotation0(Command command) {
        button(0).onTrue(command);
    }

    @Override
    public void resetRotation180(Command command) {
        button(0).onTrue(command);
    }

    @Override
    public void defense(Command command) {
        button(0).whileTrue(command);
    }

    private JoystickButton button(int button) {
        return new JoystickButton(m_controller.getHID(), button);
    }
}
