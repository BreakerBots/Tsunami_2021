/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.actions;

import frc.team5104.Superstructure;
import frc.team5104.Superstructure.Mode;
import frc.team5104.auto.AutoAction;
import frc.team5104.util.console;

public class IdleAction extends AutoAction {
    public IdleAction() {
    	
    }

    public void init() {
    	console.log("idling");
    	Superstructure.set(Mode.IDLE);
    }

    public void update() {

    }

    public boolean isFinished() {
    	return true;
    }

    public void end() {
    	
    }
}
