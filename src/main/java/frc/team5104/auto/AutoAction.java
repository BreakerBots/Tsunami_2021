/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto;

/**
 * A Single BreakerCommand (ind. peices of a path)
 */
public abstract class AutoAction {
	/** Called when action is started to be run */
	public abstract void init();
	
	/** Called periodically when the action is being run
	 * @return if action is finished */
	public abstract boolean update();

	/** Called when the action is finished being run */
	public abstract void end();

	/** Plot stuff on the Webapp Plotter */
    public void plot() { }

    /** Passes through a value */
	public boolean getValue() { return false; }
}
