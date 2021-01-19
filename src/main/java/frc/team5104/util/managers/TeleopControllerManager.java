/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util.managers;

import frc.team5104.util.CrashLogger;
import frc.team5104.util.console;
import frc.team5104.util.CrashLogger.Crash;
import frc.team5104.util.console.c;
import frc.team5104.util.console.t;

/** Manages all the calls for all Teleop Controllers given */
public class TeleopControllerManager {
	private static TeleopController[] targetTeleopControllers;
	
	/** Tell the State Machine Manager what State Machines to use */
	public static void useTeleopControllers(TeleopController... teleopControllers) {
		targetTeleopControllers = teleopControllers;
		
		//Print out
		String message = "Running Teleop Controllers: ";
		for (TeleopController teleopController : teleopControllers) {
			message += teleopController.getClass().getSimpleName() + ", ";
		}
		console.log(c.MAIN, t.INFO, message.substring(0, message.length()-2));
	}
	
	/** Call periodically when the robot is enabled (and wants teleoperation) */
	public static void update() {
		for (TeleopController teleopController : targetTeleopControllers) {
			try {
				teleopController.update();
			} catch (Exception e) { CrashLogger.logCrash(new Crash("main", e)); }
		}
	}

	/** Call once the robot becomes enabled */
	public static void enabled() {
		for (TeleopController teleopController : targetTeleopControllers) {
			try {
				teleopController.enabled();
			} catch (Exception e) { CrashLogger.logCrash(new Crash("main", e)); }
		}
	}

	/** Call once the robot becomes disabled */
	public static void disabled() {
		for (TeleopController teleopController : targetTeleopControllers) {
			try {
				teleopController.disabled();
			} catch (Exception e) { CrashLogger.logCrash(new Crash("main", e)); }
		}
	}
}
