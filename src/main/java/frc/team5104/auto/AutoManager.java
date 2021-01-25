/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto;

import frc.team5104.teleop.CompressorController;
import frc.team5104.util.CrashLogger;
import frc.team5104.util.Plotter;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.setup.RobotState;

/** Basically just handles a separate thread for autonomous. The path is updated in "AutoPathScheduler" */
public class AutoManager {
	private static boolean plottingEnabled;
	private static AutoPath targetPath;
	private static Thread pathThread;

	//Enabled/Disabled
	public static void enabled() {
		Odometry.resetHold();
		
		//spawn auto thread
		pathThread = new Thread(() -> {
			try {
				console.log(c.AUTO, "Running auto path: " + targetPath.getClass().getSimpleName());
				targetPath.start();
				console.log(c.AUTO, targetPath.getClass().getSimpleName() + " finished");
			} catch (Exception e) { CrashLogger.logCrash(new CrashLogger.Crash("auto-manager", e)); }
		});
		pathThread.start();
	}
	public static void disabled() {
		if (pathThread != null) {
			pathThread.interrupt();
			pathThread = null;
		}
	}
	
	//Init
	public static void setTargetPath(AutoPath path) {
		console.log(c.AUTO, "Setting target auto path to: " + path.getClass().getSimpleName());
		targetPath = path;
	}
	public static AutoPath getTargetPath() {
		return targetPath;
	}
	
	//Update
	public static void update() {
		//stop compressor
		if (!RobotState.isSimulation())
			CompressorController.stop();

		//update odometry
		Odometry.update();
		if (plottingEnabled()) {
			Plotter.plot(
					Odometry.getPositionFeet().getXFeet(),
					Odometry.getPositionFeet().getYFeet(),
					Plotter.Color.ORANGE
				);
		}
	}

	//Plotting
	public static void enabledPlotting() {
		plottingEnabled = true;
	}
	public static boolean plottingEnabled() {
		return plottingEnabled;
	}
}