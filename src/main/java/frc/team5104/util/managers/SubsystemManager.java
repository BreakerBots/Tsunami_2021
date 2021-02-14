/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util.managers;

import edu.wpi.first.wpilibj.Notifier;
import frc.team5104.util.Looper;
import frc.team5104.util.Looper.Crash;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.console.t;
import frc.team5104.util.setup.RobotState;

/** Manages the calls for all Subsystems given */
public class SubsystemManager {
	private static Subsystem[] targetSubsystems;
	private static Notifier thread;
	
	/** Tell the Subsystem Manager what Subsystems to manage */
	public static void useSubsystems(Subsystem... availableSubsystems) {
		//Save all subsystems
		targetSubsystems = availableSubsystems;

		//Initialize Subsystem's Interface & Print out target subsystems
		String message = "Running Subsystems: ";
		for (Subsystem subsystem : targetSubsystems) {
			try {
				subsystem.attached = true;
				subsystem.emergencyStopped = false;
				if (RobotState.isSimulation())
					subsystem.initSim();
				else subsystem.init();
				message += subsystem.getClass().getSimpleName() + ", ";
			} catch (Exception e) { Looper.logCrash(new Crash(e)); }
		}
		console.log(c.MAIN, t.INFO, message.substring(0, message.length()-2));
		
		//Fast Update
		Looper.attach(() -> {
			for (Subsystem subsystem : targetSubsystems) {
				try {
					if (!subsystem.emergencyStopped)
						subsystem.fastUpdate();
				} catch (Exception e) { Looper.logCrash(new Crash(e)); }
			}
		}, "Fast");
	}
	
	/** Plug in a the classes of subsystem you want to debug. */
	@SafeVarargs
	public static void debug(Class<? extends Subsystem>... subsystemsToDebug) {
		for (Class<? extends Subsystem> subsystemToDebug : subsystemsToDebug) {
			for (Subsystem subsystem : targetSubsystems) {
				try {
					if (subsystem.getClass() == subsystemToDebug)
						subsystem.startDebugging();
				} catch (Exception e) { Looper.logCrash(new Crash(e)); }
			}
		}
	}

	/** Returns a subsystem from a given class. Will return null if the subsystem is not attached.
	 * @warn this method gives dangerous levels of access--avoid using this method */
	public static Subsystem getSubsystem(Class<? extends Subsystem> targetSubsystem) {
		for (Subsystem subsystem : targetSubsystems) {
			try {
				if (subsystem.getClass() == targetSubsystem)
					return subsystem;
			} catch (Exception e) { Looper.logCrash(new Crash(e)); }
		}
		return null;
	}
	
	/** Call when the robot becomes enabled or disabled */
	public static void reset() {
		for (Subsystem subsystem : targetSubsystems) {
			try {
				subsystem.disabled();
			} catch (Exception e) { Looper.logCrash(new Crash(e)); }
		}
	}
	
	/** Call periodically when the robot is enabled */
	public static void update() {
		for (Subsystem subsystem : targetSubsystems) {
			try {
				if (subsystem.emergencyStopped)
					subsystem.disabled();
				else {
					subsystem.update();
					if (RobotState.isSimulation())
						subsystem.updateSim();
					if (subsystem.debugging)
						subsystem.debug();
				}
			} catch (Exception e) { Looper.logCrash(new Crash(e)); }
		}
	}
}
