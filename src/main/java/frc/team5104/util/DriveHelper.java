/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util;

import frc.team5104.util.DriveSignal.DriveUnit;

/** A class for better handling of the robot's drive train (PLAZZZ MAKE BETTEER) */
public class DriveHelper {
	
	//Constants
	private static final double RIGHT_ACCOUNT_FORWARD = 1.000;
	private static final double RIGHT_ACCOUNT_REVERSE = 1.000;
	private static final double LEFT_ACCOUNT_FORWARD  = 1.000;
	private static final double LEFT_ACCOUNT_REVERSE  = 1.000;
	
	private static final double MIN_SPEED_FORWARD = 0.055; //percent
	private static final double MIN_SPEED_TURN = 0.055;
	
	private static final double KICKSTAND_SCALAR = 0.2;
	
	private static final double TURN_SPEED_ADJ = 0.2;
	
	//Methods
	/** Calculates and left and right speed (in volts) for the robot depending on input variables */
	public static DriveSignal get(double turn, double forward, boolean kickstand) {
		DriveSignal signal = new DriveSignal(
			(forward + turn) * 12,
			(forward - turn) * 12,
			true, DriveUnit.VOLTAGE
		);
		if (kickstand)
			signal = applyKickstand(signal);
		signal = applyDriveStraight(signal);
		signal = applyMotorMinSpeed(signal);
		return signal;
	}
	
	public static double getTurnAdjust(double forward) {
		return (1 - Math.abs(forward)) * (1 - TURN_SPEED_ADJ) + TURN_SPEED_ADJ;
	}
	
	private static DriveSignal applyKickstand(DriveSignal signal) {
		signal.leftSpeed *= KICKSTAND_SCALAR;
		signal.rightSpeed *= KICKSTAND_SCALAR;
		return signal;
	}
	
	private static DriveSignal applyDriveStraight(DriveSignal signal) {
		double leftMult = (signal.leftSpeed > 0 ? 
				LEFT_ACCOUNT_REVERSE : 
				LEFT_ACCOUNT_FORWARD
			);
		double rightMult = (signal.rightSpeed > 0 ? 
				RIGHT_ACCOUNT_REVERSE : 
				RIGHT_ACCOUNT_FORWARD
			);
		signal.leftSpeed = signal.leftSpeed * leftMult;
		signal.rightSpeed = signal.rightSpeed * rightMult;
		return signal;
	}

	private static DriveSignal applyMotorMinSpeed(DriveSignal signal) {
		double turn = Math.abs(signal.leftSpeed - signal.rightSpeed) / 2;
		double biggerMax = (Math.abs(signal.leftSpeed) > Math.abs(signal.rightSpeed) ? Math.abs(signal.leftSpeed) : Math.abs(signal.rightSpeed));
		if (biggerMax != 0)
			turn = Math.abs(turn / biggerMax);
		double forward = 1 - turn;
		
		double minSpeed;
		minSpeed = (forward * (MIN_SPEED_FORWARD/12.0)) + (turn * (MIN_SPEED_TURN/12.0));
		
		if (signal.leftSpeed != 0)
			signal.leftSpeed = signal.leftSpeed * (1 - minSpeed) + (signal.leftSpeed > 0 ? minSpeed : -minSpeed);
		if (signal.rightSpeed != 0)
			signal.rightSpeed = signal.rightSpeed * (1 - minSpeed) + (signal.rightSpeed > 0 ? minSpeed : -minSpeed);

		return signal;
	}
}