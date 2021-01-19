/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util.managers;

import frc.team5104.util.console;
import frc.team5104.util.console.c;

/** 
 * A snickers wrapper of all the requirements of a subsystem. 
 */
public abstract class Subsystem {
	protected boolean attached, emergencyStopped, calibrating, debugging;
	private long calibrationStart;
	
	/** Called periodically from the robot loop */
	public abstract void update();
	
	/** Called periodically at 100hz always */
	public void fastUpdate() { }
	
	/** Called periodically while the subsystem is in debug mode */
	public void debug() { }
	
	/** Called when robots boots up; initialize devices here */
	public abstract void init();
	
	/** Called whenever the robot becomes enabled or disabled. Stop motors here. */
	public abstract void disabled();
	
	/** Labels this subsystem as emergency stopped. Stops calling the update() and fastUpdate() methods and instead
	 * spam calls disabled(). */
	public void emergencyStop() {
		console.error(c.MAIN, "Emergency stopped the " + this.getClass().getSimpleName());
		emergencyStopped = true;
	}
	
	/** Labels this subsystem as debugging, starts calling the debug method */
	public void startDebugging() {
		debugging = true;
	}
	
	/** Returns if this subsystem is labeled as calibrating */
	public boolean isCalibrating() {
		return calibrating;
	}
	
	/** Labels this subsystem as calibrating */
	public void startCalibrating() {
		calibrating = true;
		calibrationStart = System.currentTimeMillis();
	}
	
	/** Labels this subsystem as not calibrating */
	public void stopCalibrating() {
		calibrating = false;
	}
	
	/** Returns how long this subsystem has been calibrating for (in milliseconds) */
	public long getTimeInCalibration() {
		if (isCalibrating())
			return System.currentTimeMillis() - calibrationStart;
		else return 0;
	}
}