/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto;

import frc.team5104.teleop.CompressorController;
import frc.team5104.util.*;
import frc.team5104.util.console.c;
import frc.team5104.util.managers.Subsystem;
import frc.team5104.util.setup.RobotState;

/** manages the running of an autonomous path and characterizing */
public class AutoManager {
	private static boolean plottingEnabled;
	private static AutoPath targetPath;
	private static Thread pathThread;

	//Enabled/Disabled
	public static void enabled() {
		if (Characterizer.isRunning()) {
			Characterizer.enabled();
		}
		else {
			/*Spawn path thread -- a thread that waits through each action.
		     Calls action init(), isFinished(), end(), and getValue() but not update() <-- called below */
			pathThread = new Thread(() -> {
				try {
					console.log(c.AUTO, "Running auto path: " + targetPath.getClass().getSimpleName());
					targetPath.start();
					console.log(c.AUTO, targetPath.getClass().getSimpleName() + " finished");
				} catch (Exception e) { CrashLogger.logCrash(new CrashLogger.Crash("auto-manager", e)); }
			});
			pathThread.start();
		}
	}
	public static void disabled() {
		if (pathThread != null) {
			pathThread.interrupt();
			pathThread = null;
		}
		if (Characterizer.isRunning())
			Characterizer.disabled();
	}
	
	//Init
	public static void setTargetPath(AutoPath path) {
		console.log(c.AUTO, "Setting target auto path to: " + path.getClass().getSimpleName());
		targetPath = path;
	}
	public static AutoPath getTargetPath() {
		return targetPath;
	}
	public static void characterize(Class<? extends Subsystem> targetSubsystem) {
		Characterizer.init(targetSubsystem);
	}

	//Update
	public static void update() {
		//update the path
		if (targetPath != null)
			targetPath.update();

		//update characterization
		if (Characterizer.isRunning())
			Characterizer.update();

		//stop compressor
		if (!RobotState.isSimulation())
			CompressorController.stop();

		//update odometry
		Odometry.update();
	}

	//Plotting
	public static void enabledPlotting() {
		plottingEnabled = true;
	}
	public static boolean plottingEnabled() {
		return plottingEnabled;
	}
}