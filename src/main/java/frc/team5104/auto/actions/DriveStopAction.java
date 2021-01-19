/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.actions;

import frc.team5104.auto.AutoPathAction;
import frc.team5104.subsystems.Drive;
import frc.team5104.util.console;
import frc.team5104.util.console.c;

public class DriveStopAction extends AutoPathAction {
	public void init() { console.log(c.DRIVE, "Stopping Drive"); }
	public boolean update() {
		Drive.stop();
		return true;
	}
	public void end() { Drive.stop(); }
}
