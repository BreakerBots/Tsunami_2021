/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.frc2021.teleop;

import edu.wpi.first.wpilibj.Compressor;
import frc.team5104.frc2021.Controls;
import frc.team5104.lib.managers.TeleopController;
import frc.team5104.lib.setup.RobotState;
import frc.team5104.lib.setup.RobotState.RobotMode;

public class CompressorController extends TeleopController {
	private static Compressor compressor = new Compressor();
	
	protected void update() {
		if (RobotState.getMode() == RobotMode.TELEOP) {
			if (Controls.COMPRESSOR_TOGGLE.get()) {
				if (compressor.enabled()) {
					stop();
				}
				else {
					start();
				}
			}
		}
	}
	
	public void enabled() {
		stop();
	}
	
	public static void stop() {
		if (compressor != null)
			compressor.stop();
	}
	
	public static void start() {
		if (compressor != null)
			compressor.start();
	}

	public static boolean isRunning() {
		return (compressor != null) ? compressor.enabled() : false;
	}
}
