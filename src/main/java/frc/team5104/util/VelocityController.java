package frc.team5104.util;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;

public class VelocityController {
	private PIDController pid;
	private SimpleMotorFeedforward ff;
	private double lastPIDOutput, lastFFOutput, lastOutput;
	
	public VelocityController(double kP, double kI, double kD,
			double kS, double kV, double kA) {
		pid = new PIDController(kP, kI, kD);
		ff = new SimpleMotorFeedforward(kS, kV, kA);
	}
	
	public double calculate(double currentVelocity, double targetVelocity) {
		lastPIDOutput = pid.calculate(currentVelocity, targetVelocity);
		lastFFOutput = ff.calculate(
				targetVelocity,
				0
			);
		lastOutput = lastFFOutput + lastPIDOutput;
		
		return lastOutput;
	}
	
	public double getLastFFOutput() { return lastFFOutput; }
	public double getLastPIDOutput() { return lastPIDOutput; }
	public double getLastOutput() { return lastOutput; }
	
	public void setPID(double kP, double kI, double kD) {
		pid.setPID(kP, kI, kD);
	}
}
