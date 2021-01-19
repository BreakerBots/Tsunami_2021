/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveStopAction;
import frc.team5104.auto.actions.DriveTrajectoryAction;

public class OLDRight5BallPickup extends AutoPath {
	public OLDRight5BallPickup() {
		add(new DriveTrajectoryAction(true, false,
				new Position(0, 0, 0),
				new Position(0.5, 0, 0)
			));
		//Shooting Code
		add(new DriveTrajectoryAction(true,true,
				new Position(18, 0, 0),
				new Position(20.5, 1.25, 0),
				new Position(20.92, 0, 90),
				new Position(20.5, -1.25, 180),
				new Position(-0.42, -1.25, 180)
			));
		//Shooting Code
		add(new DriveStopAction());
	}
}
