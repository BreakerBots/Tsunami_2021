/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.teleop;

import frc.team5104.Controls;
import frc.team5104.subsystems.Drive;
import frc.team5104.util.DriveSignal;
import frc.team5104.util.managers.TeleopController;

public class DriveController extends TeleopController {
	//Constants
	private static final double MIN_SPEED_FORWARD = 0.055;
	private static final double MIN_SPEED_TURN = 0.055;

	private static final double KICKSTAND_SCALAR = 0.2; //percent aka slow-mode

	private static final double TURN_SPEED_ADJ = 0.2; //percent reduces turning bases on forward/back velocity

	//Variables
	boolean kickstand = false;

	//Loop
	protected void update() {
		if (Controls.DRIVE_KICKSTAND.get())
			kickstand = !kickstand;
		
		double forward = Controls.DRIVE_FORWARD.get() - Controls.DRIVE_REVERSE.get();
		Controls.DRIVE_TURN.changeCurveX1((1 - Math.abs(forward)) * (1 - TURN_SPEED_ADJ) + TURN_SPEED_ADJ);
		double turn = Controls.DRIVE_TURN.get();
		Drive.set(get(turn, forward, kickstand));
	}

	//Methods
	/** Forward Kinematics with added control features
	 * @param turn percent
	 * @param forward percent
	 * @param kickstand
	 * @return DriveSignal in volts */
	public static DriveSignal get(double turn, double forward, boolean kickstand) {
		DriveSignal signal = new DriveSignal(
				(forward + turn) * 12,
				(forward - turn) * 12,
				DriveSignal.DriveUnit.VOLTAGE
		);
		if (kickstand) {
			signal.leftSpeed *= KICKSTAND_SCALAR;
			signal.rightSpeed *= KICKSTAND_SCALAR;
		}
		signal = applyMinSpeed(signal);
		return signal;
	}
	private static DriveSignal applyMinSpeed(DriveSignal signal) {
		//reverse kinematics
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
