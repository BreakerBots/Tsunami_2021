/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util;

/** A simple class for sending/saving drive-train movement signals. */
public class DriveSignal {
	public static enum DriveUnit {
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