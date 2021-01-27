/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto;

import frc.team5104.util.setup.RobotState;

/** a framework for all paths inside frc.team5104.auto.paths */
public abstract class AutoPath {

	/** Run the path, holding the thread until it is finished.
	 * @warning DO NOT CALL IN MAIN THREAD */
	public abstract void start();

	/** Runs an action, holding the thread until it is finished.
	 * @warning DO NOT CALL IN MAIN THREAD
	 * @return passthroughs value from action */
	public boolean run(AutoAction action) {
		action.init();
		while (!action.update()) {
			try { Thread.sleep(RobotState.getLoopPeriod()); }
			catch (InterruptedException e) { e.printStackTrace(); }
		}
		action.end();
		return action.getValue();
	}
}
