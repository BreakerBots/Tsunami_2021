/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.actions;

import frc.team5104.Superstructure;
import frc.team5104.Superstructure.FlywheelState;
import frc.team5104.auto.AutoAction;
import frc.team5104.util.console;

public class ChargeFlywheelAction extends AutoAction {
    public ChargeFlywheelAction() {
    	
    }

    public void init() {
    	console.log("charging flywheel");
    	Superstructure.set(FlywheelState.SPINNING);
    }

    public void update() {

    }

    public boolean isFinished() { return true; }

    public void end() {
    	
    }
}
