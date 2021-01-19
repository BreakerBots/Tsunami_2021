package frc.team5104.util;

import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile.Constraints;
import frc.team5104.util.setup.RobotState;

public class PositionController {
	private ProfiledPIDController pid;
	private SimpleMotorFeedforward ff;
	private double lastVelocity, lastPIDOutput, lastFFOutput, lastOutput, lastPosition, lastTarget;
	
	public PositionController(double kP, double kI, double kD, double maxVel, 
			double maxAccel, double kS, double kV, double kA) {
		pid = new ProfiledPIDController(kP, kI, kD, new Constraints(maxVel, maxAccel));
		ff = new SimpleMotorFeedforward(kS, kV, kA);
	}
	
	public double calculate(double currentPosition, double targetPosition) {
		lastPIDOutput = pid.calculate(currentPosition, targetPosition);
		lastFFOutput = ff.calculate(
				pid.getSetpoint().velocity,
				(pid.getSetpoint().velocity - lastVelocity) / RobotState.getDeltaTime()
			);
		lastOutput = lastFFOutput + lastPIDOutput;
		
		lastVelocity = pid.getSetpoint().velocity;
		
		return lastOutput;
	}
	
	public double getLastFFOutput() { return lastFFOutput; }
	public double getLastPIDOutput() { return lastPIDOutput; }
	public double getLastOutput() { return lastOutput; }
	public double getLastError() { return lastTarget - lastPosition; }
	
	public void setPID(double kP, double kI, double kD) {
		pid.setPID(kP, kI, kD);
	}
	
	public void setProfiling(double maxVel, double maxAccel) {
		pid.setConstraints(new Constraints(maxVel, maxAccel));
	}
}