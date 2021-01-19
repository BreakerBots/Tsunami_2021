/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.actions;

import frc.team5104.Superstructure;
import frc.team5104.Superstructure.FlywheelState;
import frc.team5104.auto.AutoPathAction;
import frc.team5104.util.console;
import frc.team5104.util.console.c;

public class ChargeFlywheelAction extends AutoPathAction {
    public ChargeFlywheelAction() {
    	
    }

    public void init() {
    	console.log(c.AUTO, "charging flywheel");
    	Superstructure.setFlywheelState(FlywheelState.SPINNING);
    }

    public boolean update() {
    	return true;
    }

    public void end() {
    	
    }
}
