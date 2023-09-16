package org.team100.frc2023.control;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.wpilibj.event.EventLoop;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class VKBControl implements Control {

    private final CommandJoystick m_controller;
    private Rotation2d previousRotation = new Rotation2d(0);
    private double ticks = 0;
    private final double kTicksToDegrees = 0;
    private final double kTicksToRadians = 0;

    public static class Config {
        public static int kTriggerSoftChannel = 0;
        public static int kTriggerHardChannel = 1;
        public static int kBigRedButtonChannel = 2;
        public static int kHighGreyButtonChannel = 3;
        public static int kMidGreyButtonChannel = 4;
        public static int kLowGreyButtonChannel = 5;
        public static int kF1ButtonChannel = 6;
        public static int kF2ButtonChannel = 7;
        public static int kF3ButtonChannel = 8;
        public static int kSw1UpChannel = 9;
        public static int kSw1DownChannel = 10;
        public static int kEn1IncChannel = 11;
        public static int kEn1DecChannel = 12;
    }

    public VKBControl() {
        m_controller = new CommandJoystick(0);
        System.out.printf("Controller0: %s\n", m_controller.getHID().getName());

        EventLoop loop = CommandScheduler.getInstance().getActiveButtonLoop();
        loop.bind(updateTicks());
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

    public double getEn1Raw() {
        return ticks;
    }

    public double getEn1Radians() {
        return MathUtil.angleModulus(ticks * kTicksToRadians);
    }

    public double getEn1Degrees() {
        return ticks * kTicksToDegrees - 360 * Math.floor(ticks * kTicksToDegrees / 360);
    }

    @Override
    public void resetRotation0(Command command) {
        button(Config.kSw1UpChannel).onTrue(command);
    }

    @Override
    public void resetRotation180(Command command) {
        button(Config.kSw1DownChannel).onTrue(command);
    }

    @Override
    public void defense(Command command) {
        button(Config.kBigRedButtonChannel).whileTrue(command);
    }

    private Runnable updateTicks() {
        return new Runnable() {
            private boolean m_incLast = button(Config.kEn1IncChannel).getAsBoolean();
            private boolean m_decLast = button(Config.kEn1DecChannel).getAsBoolean();

            @Override
            public void run() {
                boolean inc = button(Config.kEn1IncChannel).getAsBoolean();
                boolean dec = button(Config.kEn1DecChannel).getAsBoolean();
                if (!m_incLast && inc) {
                    ticks++;
                }
                if (!m_decLast && dec) {
                    ticks--;
                }
                m_incLast = inc;
                m_decLast = dec;
            }
        };
    }

    private JoystickButton button(int button) {
        return new JoystickButton(m_controller.getHID(), button);
    }
}
