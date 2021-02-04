/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.actions;

import frc.team5104.auto.AutoAction;
import frc.team5104.subsystems.Turret;
import frc.team5104.util.console;
import frc.team5104.util.console.c;

public class ZeroTurretAction extends AutoAction {
	
	private double angleToZeroTurretAt;
	
    public ZeroTurretAction(double angleToZeroTurretAt) {
    	this.angleToZeroTurretAt = angleToZeroTurretAt;
    }

    public void init() {
    	console.log(c.AUTO, "zeroing turret at " + angleToZeroTurretAt);
    	Turret.setFieldOrientedTarget(angleToZeroTurretAt);
    }

    public void update() {

    }

    public boolean isFinished() {
    	return true;
    }

    public void end() {
    	
    }
}
