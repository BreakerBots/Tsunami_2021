/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util.managers;

/** A twix rapper for all the requirements in a teleop controller */ 
public abstract class TeleopController {
	
	/** Called periodically from the robot loop */
	protected abstract void update();
	
	/** Called once the robot becomes enabled */
	protected void enabled() {}
	
	/** Called once the robot becomes disabled */
	protected void disabled() {}
}