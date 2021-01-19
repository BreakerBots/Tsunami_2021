/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util;

/** A simple class for sending/saving drive-train movement signals. */
public class DriveSignal {
	/**
	 * Unit that the left & right speeds can be in.
	 * feetPerSecond: uses PID directly on the talons to achieve the specified velocity
	 * percentOutput: directly set the percent output on the talons [from -1 (full speed reverse) to 1 (full speed forward)]
	 * voltage: similar to percentOutput but instead of -1 to 1 its -currentVoltage to currentVoltage (usually ~12V)
	 */
	public static enum DriveUnit {
		FEET_PER_SECOND,
		PERCENT_OUTPUT,
		VOLTAGE,
		STOP
	}
	
	// Robot Drive Signal Variables
	public double leftSpeed;
	public double rightSpeed;
	public double leftFeedForward;
	public double rightFeedForward;
	public boolean isHighGear;
	public DriveUnit unit;
	
	public DriveSignal() {
		this(0, 0, true, DriveUnit.STOP);
	}
	
	public DriveSignal(double leftSpeed, double rightSpeed, boolean isHighGear) {
		this(leftSpeed, rightSpeed, isHighGear, DriveUnit.PERCENT_OUTPUT);
	}
	
	public DriveSignal(double leftSpeed, double rightSpeed, DriveUnit unit) {
		this(leftSpeed, rightSpeed, true, DriveUnit.PERCENT_OUTPUT);
	}
	
	public DriveSignal(double leftSpeed, double rightSpeed, boolean isHighGear, DriveUnit unit) {
		this(leftSpeed, rightSpeed, isHighGear, unit, 0, 0);
	}
	
	public DriveSignal(double leftSpeed, double rightSpeed, boolean isHighGear, DriveUnit unit, double leftFeedForward, double rightFeedForward) {
		this.leftSpeed = leftSpeed;
		this.rightSpeed = rightSpeed;
		this.unit = unit;
		this.leftFeedForward = leftFeedForward;
		this.rightFeedForward = rightFeedForward;
		this.isHighGear = isHighGear;
	}
	
	public boolean hasFeedForward() {
		return leftFeedForward != 0 || rightFeedForward != 0;
	}
	
	public String toString() {
		return  "l: " + leftSpeed + ", " +
				"r: " + rightSpeed + 
				(hasFeedForward() ? 
					", lff: " + leftFeedForward + ", " +
					"rff: " + rightFeedForward
				: "");
	}
}