/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveStopAction;
import frc.team5104.auto.actions.DriveTrajectoryAction;
import frc.team5104.auto.actions.ShootAction;

public class OLDSimpleRight extends AutoPath {
	public OLDSimpleRight() {
		//Robot Position - In front of the Power Port
		add(new DriveTrajectoryAction(true, false,
				new Position(0, 0, 0),
				new Position(2, 0, 0)
			));
		add(new DriveStopAction());
		add(new ShootAction());
		//Changed x from 0.5 to 2 to get the robot to move forward
		// about 1 foot.  With the 0.5 value, the robot tried to move,
		// but didn't actually move forward
	}

	
	}
