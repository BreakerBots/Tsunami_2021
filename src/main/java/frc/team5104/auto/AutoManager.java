/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto;

import frc.team5104.auto.actions.DriveTrajectory;
import frc.team5104.teleop.CompressorController;
import frc.team5104.util.*;
import frc.team5104.util.Looper.Crash;
import frc.team5104.util.Looper.Loop;
import frc.team5104.util.Plotter.InputMode;
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
		else if (targetPath != null) {
			/*Spawn path thread -- a thread that waits through each action.
		     Calls action init(), isFinished(), end(), and getValue() but not update() <-- called below */
			pathThread = new Thread(() -> {
				try {
					console.log("Running auto path: " + targetPath.getClass().getSimpleName());
					targetPath.start();
					console.log(targetPath.getClass().getSimpleName() + " finished");
				} catch (Exception e) { Looper.logCrash(new Crash(e)); }
			});
			pathThread.start();
			Looper.registerLoop(new Loop("Auto", pathThread, 8));
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
		console.log("Setting target auto path to: " + path.getClass().getSimpleName());
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

	//Trajectory Tester
	public static void runTrajectoryTester() {
		Plotter.setInputMode(InputMode.TRAJECTORY);
		Plotter.setInputListener((data) -> {
			boolean reversed = Webapp.getJSONValue(data, "reversed") == "\"true\"";
			String[] pointStrings = data
					.substring(data.indexOf("points\":")+9, data.indexOf("}")-1)
					.replaceAll("\"", "")
					.split("]");

			Position[] positions = new Position[pointStrings.length];
			for (int i = 0; i < positions.length; i++) {
				String str = pointStrings[i].substring(pointStrings[i].charAt(0)==','?2:1);
				double x = Double.valueOf(str.substring(0, str.indexOf(",")));
				double y = Double.valueOf(str.substring(str.indexOf(",")+1, str.lastIndexOf(",")));
				double angle = Double.valueOf(str.substring(str.lastIndexOf(",")+1));
				positions[i] = new Position(x, y, angle);
			}

			try {
				new DriveTrajectory(reversed, positions);
			} catch (Exception e) { }
		});
	}
}