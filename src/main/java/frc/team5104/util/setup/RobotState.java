package frc.team5104.util.setup;

import edu.wpi.first.wpilibj.Timer;

public class RobotState {
	
	//State Machine
	public static enum RobotMode {
		DISABLED, TELEOP, AUTONOMOUS, TEST
	}; 
	private static RobotMode currentMode = RobotMode.DISABLED;
	private static RobotMode lastMode = RobotMode.DISABLED;
	private static Timer timer = new Timer();
	private static double deltaTime = 0;
	
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
	public static RobotMode getMode() { return currentMode; }
	public static RobotMode getLastMode() { return lastMode; }
	public static double getDeltaTime() { return deltaTime; }
	public static double getTimeSinceEnabled() { return timer.get(); }
}