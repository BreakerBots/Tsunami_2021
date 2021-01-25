/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.actions;

import frc.team5104.auto.AutoAction;
import frc.team5104.util.console;
import frc.team5104.util.console.c;

public class DelayAction extends AutoAction {
	long startTime;
	int delay;

    public DelayAction(int milliseconds) {
        delay = milliseconds;
    }

    public void init() {
    	console.log(c.AUTO, "Delaying " + delay + "ms");
    	startTime = System.currentTimeMillis();
    }

    public boolean update() {
    	return (System.currentTimeMillis() >= startTime + delay);
    }

    public void end() {
    	
    }
}
