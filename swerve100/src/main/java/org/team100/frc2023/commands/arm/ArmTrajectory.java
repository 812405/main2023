package org.team100.frc2023.commands.arm;

import org.team100.frc2023.subsystems.arm.ArmPosition;
import org.team100.frc2023.subsystems.arm.ArmInterface;
import org.team100.lib.motion.arm.ArmAngles;

import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.Trajectory.State;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;

public class ArmTrajectory extends Command {
    public static class Config {
        public double oscillatorFrequencyHz = 2;
        /** amplitude (each way) of oscillation in encoder units */
        public double oscillatorScale = 0.1;
        /** start oscillating when this close to the target. */
        public double oscillatorZone = 0.1;
        public TrajectoryConfig safeTrajectory = new TrajectoryConfig(9, 3.5);
        public TrajectoryConfig normalTrajectory = new TrajectoryConfig(5, 2.5);
        public TrajectoryConfig subTrajectory = new TrajectoryConfig(6, 5.5);
        public TrajectoryConfig midTrajectory = new TrajectoryConfig(2, 0.5);

        public TrajectoryConfig oscillateTrajectory = new TrajectoryConfig(5, 2);
        public TrajectoryConfig autoTrajectory = new TrajectoryConfig(9, 1.5);

    }

    private static ArmPosition lastPosition = ArmPosition.AUTO;

    private final Config m_config = new Config();
    private final ArmInterface m_arm;
    private final ArmPosition m_position;
    private final boolean m_oscillate;
    private final Timer m_timer;

    private final DoublePublisher measurmentX;
    private final DoublePublisher measurmentY;
    private final DoublePublisher setpointUpper;
    private final DoublePublisher setpointLower;
    private final boolean goingDown = false;

    private Trajectory m_trajectory;

    /**
     * Go to the specified position and optionally oscillate when you get there.
     */
    public ArmTrajectory(ArmPosition position, ArmInterface arm, boolean oscillate) {
        m_arm = arm;
        m_position = position;
        m_oscillate = oscillate;
        m_timer = new Timer();
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        measurmentX = inst.getTable("Arm Trajec").getDoubleTopic("measurmentX").publish();
        measurmentY = inst.getTable("Arm Trajec").getDoubleTopic("measurmentY").publish();
        setpointUpper = inst.getTable("Arm Trajec").getDoubleTopic("Setpoint Upper").publish();
        setpointLower = inst.getTable("Arm Trajec").getDoubleTopic("Setpoint Lower").publish();
        // lastPosition = inst.getTable("Arm Trajec")
        addRequirements(m_arm.subsystem());
    }

   

    @Override
    public void initialize() {
        m_timer.restart();
        // m_arm.setReferenceUsed(true);
        
        // if(m_oscillate){
        //     trajectoryConfig = m_config.oscillateTrajectory;
        //     m_arm.setControlOscillate();
            
        // }else

        TrajectoryConfig trajectoryConfig;

        

        
        if (m_position == ArmPosition.SAFE) {
            trajectoryConfig = m_config.safeTrajectory;
            m_arm.setControlSafe();
        } else if(m_position == ArmPosition.AUTO){
            trajectoryConfig = m_config.autoTrajectory;
        } else if(m_position == ArmPosition.SUB){
            trajectoryConfig = m_config.subTrajectory;
        }else{
            trajectoryConfig = m_config.normalTrajectory;
            m_arm.setControlNormal();
        } 

        if(lastPosition == ArmPosition.MID){
            trajectoryConfig = m_config.midTrajectory;
        }
        m_trajectory = new ArmTrajectories(trajectoryConfig).makeTrajectory(
                m_arm.getMeasurement(),
                m_position,
                m_arm.getCubeMode());
        
        lastPosition = m_position;
    }

    public void execute() {
        if (m_trajectory == null) {
            return;
        }
        ArmAngles measurement = m_arm.getMeasurement();
        double currentUpper = measurement.th2;
        double currentLower = measurement.th1;

        double curTime = m_timer.get();
        State desiredState = m_trajectory.sample(curTime);

        double desiredUpper = desiredState.poseMeters.getX();
        double desiredLower = desiredState.poseMeters.getY();

        double upperError = desiredUpper - currentUpper;

        if (m_oscillate && upperError < m_config.oscillatorZone) {
            System.out.println("YESSSSSSSSSSs");
            m_arm.setControlOscillate();
            desiredUpper += oscillator(curTime);
        }

        // if(currentUpper < 1.29){
        //     m_arm.setReference(new ArmAngles(currentLower, 1.29));
        // } else if(currentUpper > 1.29 ){
        //     m_arm.setReference(new ArmAngles(currentLower, 1.29));

        // }

        ArmAngles reference = new ArmAngles(desiredLower, desiredUpper);


        m_arm.setReference(reference);

        measurmentX.set(currentUpper);
        measurmentY.set(currentLower);
        setpointUpper.set(desiredUpper);
        setpointLower.set(desiredLower);
    }

    @Override
    public void end(boolean interrupted) {
        m_arm.setControlNormal();
        // m_arm.setReferenceUsed(false);
        m_arm.setReference(m_arm.getMeasurement());
    }

    @Override
    public boolean isFinished() {
        if (m_position == ArmPosition.SAFEWAYPOINT) {
            return m_timer.hasElapsed(m_trajectory.getTotalTimeSeconds());
        }
        return false;
    }

    private double oscillator(double timeSec) {
        return m_config.oscillatorScale * Math.sin(2 * Math.PI * m_config.oscillatorFrequencyHz * timeSec);
    }

}
