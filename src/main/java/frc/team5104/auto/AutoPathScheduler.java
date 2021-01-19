/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto;

/** Handles the Execution of Actions inside the assigned Path */
public class AutoPathScheduler {
	
	private AutoPath targetPath;
	private int index;
	
	public AutoPathScheduler(AutoPath targetPath) {
		this.targetPath = targetPath;
		this.index = 0;
		initCurrentAction();
	}
	
	public void update() {
		//If not finished update the current action
		if (!isFinished()) {
			if (targetPath.pathActions[index].update()) {
				//If the current action is finished call the "end" function, then go to the next action
				targetPath.pathActions[index].end();
				index++;
				initCurrentAction();
			}
		}
	}
	
	public boolean isFinished() {
		return index >= targetPath.pathActionsLength;
	}
	
	private void initCurrentAction() {
		if (!isFinished())
			targetPath.pathActions[index].init();
	}
}
