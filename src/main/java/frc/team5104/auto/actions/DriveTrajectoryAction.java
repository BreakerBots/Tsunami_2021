/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.actions;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import frc.team5104.Constants;
import frc.team5104.auto.AutoPathAction;
import frc.team5104.auto.Odometry;
import frc.team5104.auto.Position;
import frc.team5104.subsystems.Drive;
import frc.team5104.util.Units;
import frc.team5104.util.console;
import frc.team5104.util.DriveSignal;
import frc.team5104.util.DriveSignal.DriveUnit;
import frc.team5104.util.Plotter;
import frc.team5104.util.Plotter.Color;
import frc.team5104.util.console.c;

/**
 * Follow a trajectory using the Breaker Trajectory Follower (Ramses Follower)
 */
public class DriveTrajectoryAction extends AutoPathAction {

	private final Timer m_timer = new Timer();
	private final Trajectory m_trajectory;
	private final RamseteController m_follower;
	private final SimpleMotorFeedforward m_feedforward;
	private final DifferentialDriveKinematics m_kinematics;
	private final PIDController m_leftController;
	private final PIDController m_rightController;
	private DifferentialDriveWheelSpeeds m_prevSpeeds;
	private double m_prevTime;

	public DriveTrajectoryAction(boolean plotTrajectory, boolean isReversed, Position... waypoints) {
		m_feedforward = new SimpleMotorFeedforward(
				Constants.DRIVE_KS,
				Constants.DRIVE_KV,
				Constants.DRIVE_KA
			);

		m_kinematics = new DifferentialDriveKinematics(
				Units.feetToMeters(Constants.DRIVE_WHEEL_BASE_WIDTH)
			);

		// Create a voltage constraint to ensure we don't accelerate too fast
		DifferentialDriveVoltageConstraint autoVoltageConstraint = 
				new DifferentialDriveVoltageConstraint(
					m_feedforward,
					m_kinematics,
					10
				);

		// Create config for trajectory
		TrajectoryConfig config = new TrajectoryConfig(
				Units.feetToMeters(Constants.AUTO_MAX_VELOCITY), 
				Units.feetToMeters(Constants.AUTO_MAX_ACCEL)
			).setKinematics(m_kinematics)
			 .addConstraint(autoVoltageConstraint)
			 .setReversed(isReversed);

		// An example trajectory to follow. All units in meters.
		m_trajectory = TrajectoryGenerator.generateTrajectory(
				Position.toPose2dMeters(waypoints),
				config
			);
		
		//plot trajectory on plotter
		if (plotTrajectory) {
			Plotter.plotAll(Position.fromStates(m_trajectory.getStates()), Color.RED);
		}

		m_follower = new RamseteController(
				Constants.AUTO_CORRECTION_FACTOR, 
				Constants.AUTO_DAMPENING_FACTOR
			);
		m_leftController = new PIDController(Constants.DRIVE_KP, 0, Constants.DRIVE_KD);
		m_rightController = new PIDController(Constants.DRIVE_KP, 0, Constants.DRIVE_KD);
	}

	public void init() {
		console.sets.create("RunTrajectoryTime");
		console.log(c.AUTO, "Running Trajectory");
		m_prevTime = 0;
		Trajectory.State initialState = m_trajectory.sample(0);
		m_prevSpeeds = m_kinematics.toWheelSpeeds(
			new ChassisSpeeds(
				initialState.velocityMetersPerSecond, 
				0,
				initialState.curvatureRadPerMeter * initialState.velocityMetersPerSecond
			)
		);
		m_timer.reset();
		m_timer.start();
		m_leftController.reset();
		m_rightController.reset();
	}

	public boolean update() {
		double curTime = m_timer.get();
		double dt = curTime - m_prevTime;

		DifferentialDriveWheelSpeeds targetWheelSpeeds = m_kinematics.toWheelSpeeds(
			m_follower.calculate(
				Odometry.getPose2dMeters(),
				m_trajectory.sample(curTime)
			)
		);

		double leftFeedforward = m_feedforward.calculate(
				targetWheelSpeeds.leftMetersPerSecond,
				(targetWheelSpeeds.leftMetersPerSecond - m_prevSpeeds.leftMetersPerSecond) / dt
			);

		double rightFeedforward = m_feedforward.calculate(
				targetWheelSpeeds.rightMetersPerSecond,
				(targetWheelSpeeds.rightMetersPerSecond - m_prevSpeeds.rightMetersPerSecond) / dt
			);

		double leftFeedback = m_leftController.calculate(
				Odometry.getWheelSpeeds().leftMetersPerSecond, 
				targetWheelSpeeds.leftMetersPerSecond
			);

		double rightFeedback = m_rightController.calculate(
				Odometry.getWheelSpeeds().rightMetersPerSecond,
				targetWheelSpeeds.rightMetersPerSecond
			);

		m_prevTime = curTime;
		m_prevSpeeds = targetWheelSpeeds;

		Drive.set(
			new DriveSignal(
					leftFeedforward + leftFeedback, 
					rightFeedforward + rightFeedback, 
					true, DriveUnit.VOLTAGE
			)
		);

		return m_timer.hasPeriodPassed(m_trajectory.getTotalTimeSeconds());
	}

	public void end() {
		m_timer.stop();
		Drive.stop();
		console.log(c.AUTO, 
				"Trajectory Finished in " + 
				console.sets.getTime("RunTrajectoryTime") + "s" +
				", at: " + Odometry.getPositionFeet()
		);
	}
}
