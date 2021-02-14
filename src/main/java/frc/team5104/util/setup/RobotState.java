package frc.team5104.util.setup;

import edu.wpi.first.hal.HALUtil;

public class RobotState {
	//State Machine
	public enum RobotMode {
		DISABLED, TELEOP, AUTONOMOUS, TEST
	}; 
	private static RobotMode currentMode = RobotMode.DISABLED;
	private static RobotMode lastMode = RobotMode.DISABLED;
	protected static double loopPeriod = 20; //<-- loop constant for main thread

	//Protected
	protected static void setMode(RobotMode mode) {
		currentMode = mode;
	}
	protected static void setLastMode(RobotMode mode) {
		lastMode = mode;
	}

	//External Functions
	public static boolean isEnabled() { return currentMode != RobotMode.DISABLED; }
	public static boolean isDisabled() { return !isEnabled(); }
	public static boolean is(RobotMode mode) { return mode == currentMode; }
	/** @return the current mode */
	public static RobotMode getMode() { return currentMode; }
	/** @return the mode from the last loop */
	public static RobotMode getLastMode() { return lastMode; }
	/** @return the hertz of the loop (def 50hz) - aka 1/loopPeriod*/
	public static int getLoopHz() { return (int) (1000.0 / getLoopPeriod()); }
	/** @return the loop period (def 20ms) in ms - aka 1/loopHz*/
	public static int getLoopPeriod() { return (int) loopPeriod; }
	/** sets the loop period in ms */
	public static void setLoopPeriod(double period) { loopPeriod = period; }
	/** returns if the robot is currently being simulated */
	public static boolean isSimulation() { return HALUtil.getHALRuntimeType() != 0; }
	/** returns the FPGA timestamp in ms */
	public static double getFPGATimestamp() {
		return edu.wpi.first.wpilibj.RobotController.getFPGATime() / 1e9d;
	}
}