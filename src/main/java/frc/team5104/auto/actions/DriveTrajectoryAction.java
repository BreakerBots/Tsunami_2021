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
import frc.team5104.auto.AutoAction;
import frc.team5104.auto.AutoManager;
import frc.team5104.auto.Odometry;
import frc.team5104.auto.Position;
import frc.team5104.subsystems.Drive;
import frc.team5104.util.DriveSignal;
import frc.team5104.util.DriveSignal.DriveUnit;
import frc.team5104.util.Plotter;
import frc.team5104.util.Units;
import frc.team5104.util.console;
import frc.team5104.util.console.c;

/**
 * Follow a trajectory using the Breaker Trajectory Follower (Ramses Follower)
 */
public class DriveTrajectoryAction extends AutoAction {

	public static final double CORRECTION_FACTOR = 1/*1*/; //>0
	public static final double DAMPENING_FACTOR = 0.3/*0.5*/; //0-1

	private final Timer timer = new Timer();
	private final Trajectory trajectory;
	private final RamseteController follower;
	private final SimpleMotorFeedforward feedforward;
	private final DifferentialDriveKinematics kinematics;
	private final PIDController leftController, rightController;
	private DifferentialDriveWheelSpeeds lastSpeeds;
	private double lastTime;

	public DriveTrajectoryAction(boolean isReversed, Position... waypoints) {
		feedforward = new SimpleMotorFeedforward(
				Constants.drive.kLS,
				Constants.drive.kLV,
				Constants.drive.kLA
			);

		kinematics = new DifferentialDriveKinematics(
				Units.feetToMeters(Constants.drive.trackWidth)
			);

		// Create a voltage constraint to ensure we don't accelerate too fast
		DifferentialDriveVoltageConstraint autoVoltageConstraint = 
				new DifferentialDriveVoltageConstraint(
						feedforward,
						kinematics,
					10
				);

		// Create config for trajectory
		TrajectoryConfig config = new TrajectoryConfig(
				Units.feetToMeters(Constants.drive.maxVelocity),
				Units.feetToMeters(Constants.drive.maxAccel)
			).setKinematics(kinematics)
			 .addConstraint(autoVoltageConstraint)
			 .setReversed(isReversed);

		// An example trajectory to follow. All units in meters.
		trajectory = TrajectoryGenerator.generateTrajectory(
				Position.toPose2dMeters(waypoints),
				config
			);
		
		follower = new RamseteController(
				CORRECTION_FACTOR,
				DAMPENING_FACTOR
			);
		leftController = new PIDController(Constants.drive.kP, 0, Constants.drive.kD);
		rightController = new PIDController(Constants.drive.kP, 0, Constants.drive.kD);

		if (AutoManager.plottingEnabled())
			Plotter.plotAll(Position.fromStates(trajectory.getStates()), Plotter.Color.RED);
	}

	public void plot() {
		Plotter.plotAll(Position.fromStates(trajectory.getStates()), Plotter.Color.RED);
	}

	public void init() {
		console.sets.create("RunTrajectoryTime");
		console.log(c.AUTO, "Running Trajectory");
		lastTime = 0;
		Trajectory.State initialState = trajectory.sample(0);
		lastSpeeds = kinematics.toWheelSpeeds(
			new ChassisSpeeds(
				initialState.velocityMetersPerSecond, 
				0,
				initialState.curvatureRadPerMeter * initialState.velocityMetersPerSecond
			)
		);
		timer.reset();
		timer.start();
		leftController.reset();
		rightController.reset();
	}

	public void update() {
		double curTime = timer.get();
		double dt = curTime - lastTime;

		DifferentialDriveWheelSpeeds targetWheelSpeeds = kinematics.toWheelSpeeds(
				follower.calculate(
					Odometry.getPose2dMeters(),
					trajectory.sample(curTime)
			)
		);

		double leftFeedforward = feedforward.calculate(
				targetWheelSpeeds.leftMetersPerSecond,
				(targetWheelSpeeds.leftMetersPerSecond - lastSpeeds.leftMetersPerSecond) / dt
			);

		double rightFeedforward = feedforward.calculate(
				targetWheelSpeeds.rightMetersPerSecond,
				(targetWheelSpeeds.rightMetersPerSecond - lastSpeeds.rightMetersPerSecond) / dt
			);

		double leftFeedback = leftController.calculate(
				Odometry.getWheelSpeeds().leftMetersPerSecond, 
				targetWheelSpeeds.leftMetersPerSecond
			);

		double rightFeedback = rightController.calculate(
				Odometry.getWheelSpeeds().rightMetersPerSecond,
				targetWheelSpeeds.rightMetersPerSecond
			);

		lastTime = curTime;
		lastSpeeds = targetWheelSpeeds;

		Drive.set(new DriveSignal(
				leftFeedforward + leftFeedback,
				rightFeedforward + rightFeedback,
				DriveUnit.VOLTAGE
		));

		if (AutoManager.plottingEnabled()) {
			Plotter.plot(Odometry.getPositionFeet().getXFeet(),
			             Odometry.getPositionFeet().getYFeet(),
			             Plotter.Color.ORANGE);
		}
	}

	public boolean isFinished() {
		return timer.hasPeriodPassed(trajectory.getTotalTimeSeconds());
	}

	public void end() {
		timer.stop();
		Drive.stop();
		console.log(c.AUTO, 
				"Trajectory Finished in " + 
				console.sets.getTime("RunTrajectoryTime") + "s" +
				", at: " + Odometry.getPositionFeet()
		);
	}
}
