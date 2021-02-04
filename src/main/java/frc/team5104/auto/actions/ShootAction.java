/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.actions;

import frc.team5104.Superstructure;
import frc.team5104.Superstructure.Mode;
import frc.team5104.auto.AutoAction;
import frc.team5104.util.console;
import frc.team5104.util.console.c;

public class ShootAction extends AutoAction {
    public ShootAction() {
    	
    }

    public void init() {
    	console.log(c.AUTO, "firing!");
    	Superstructure.set(Mode.AIMING);
    }

    public void update() {

    }

    public boolean isFinished() {
	    //exit when hopper is empty
	    return Superstructure.is(Mode.IDLE);
    }

    public void end() {
    	console.log(c.AUTO, "finished firing");
    }
}
