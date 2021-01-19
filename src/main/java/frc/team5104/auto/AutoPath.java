/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto;

/**
 * A Collection of BreakerCommands (The Entire Path)
 * Ran through the BreakerCommandScheduler
 */
public abstract class AutoPath {
	
	/** The Actions for the path */
	public AutoPathAction[] pathActions = new AutoPathAction[30];
	
	/** The number of Actions in the path */
	public int pathActionsLength = 0;
	
	/** Add an action to the Path */
	public void add(AutoPathAction action) {
		pathActions[pathActionsLength] = action;
		pathActionsLength++;
	}
}
