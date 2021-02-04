package frc.team5104.util.setup;

import edu.wpi.first.hal.HALUtil;
import edu.wpi.first.wpilibj.Timer;

public class RobotState {
	//State Machine
	public enum RobotMode {
		DISABLED, TELEOP, AUTONOMOUS, TEST
	}; 
	private static RobotMode currentMode = RobotMode.DISABLED;
	private static RobotMode lastMode = RobotMode.DISABLED;
	private static Timer timer = new Timer();
	private static double deltaTime = 0;
	protected static double loopPeriod = 20; //<-- loop constant for main thread

	//Protected
	protected static void setMode(RobotMode mode) {
		currentMode = mode;
	}
	protected static void setLastMode(RobotMode mode) {
		lastMode = mode;
	}
	protected static void setDeltaTime(double dt) {
		deltaTime = dt;
	}
	protected static void resetTimer() {
		timer.reset();
	}
	protected static void startTimer() {
		timer.start();
	}
	
	//External Functions
	public static boolean isEnabled() { return currentMode != RobotMode.DISABLED; }
	public static boolean isDisabled() { return !isEnabled(); }
	public static boolean is(RobotMode mode) { return mode == currentMode; }
	public static RobotMode getMode() { return currentMode; }
	public static RobotMode getLastMode() { return lastMode; }
	public static int getLoopHz() { return (int) (1000.0 / getLoopPeriod()); }
	public static int getLoopPeriod() { return (int) loopPeriod; }
	public static void setLoopPeriod(double period) { loopPeriod = period; }
	public static double getDeltaTime() { return deltaTime; }
	public static double getTimeSinceEnabled() { return timer.get(); }
	public static boolean isSimulation() { return HALUtil.getHALRuntimeType() != 0; }
}