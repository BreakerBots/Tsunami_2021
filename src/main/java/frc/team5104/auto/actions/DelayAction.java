/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.actions;

import frc.team5104.auto.AutoAction;
import frc.team5104.util.console;

public class DelayAction extends AutoAction {
	long startTime;
	int delay;

    public DelayAction(int milliseconds) {
        delay = milliseconds;
    }

    public void init() {
    	console.log("Delaying " + delay + "ms");
    	startTime = System.currentTimeMillis();
    }

    public void update() {
    }

    public boolean isFinished() { return (System.currentTimeMillis() >= startTime + delay); }

    public void end() {
    	
    }
}
