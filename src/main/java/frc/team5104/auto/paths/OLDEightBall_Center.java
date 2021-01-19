/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveStopAction;
import frc.team5104.auto.actions.DriveTrajectoryAction;
import frc.team5104.auto.actions.ShootAction;

public class OLDEightBall_Center extends AutoPath {
	public OLDEightBall_Center() {
		/*Robot Position - Center of the field, drives and picks up 
		the closest two balls from the trench, then drives back and
		fires, and finally grabs the three balls in our trench and shoots*/
		add(new DriveTrajectoryAction(true, false,
				new Position(0, 0, 0),
				new Position(8.9858, 6.67-4.21167, 0),
				new Position(9.51, 6.67-5.4867, 0),
				new Position(1,6.67,0)
			));
		add(new DriveTrajectoryAction(true, true,
				new Position(1,6.67,0),
				new Position(1,21.67,0)
			));
		add(new DriveTrajectoryAction(true, false,
				new Position(1,21.67,0),
				new Position(1,6.67,0)
			));
		add(new DriveStopAction());
		add(new ShootAction());
	}
}
