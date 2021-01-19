/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveStopAction;
import frc.team5104.auto.actions.DriveTrajectoryAction;

public class OLDRight2BallPickup extends AutoPath {
	//Robot Position - Almost at right (Power Port) side edge
	public OLDRight2BallPickup() {
		add(new DriveTrajectoryAction(true, false,
				new Position(0, 0, 0),
		//		new Position(5.67, 0, 0),
				new Position(11.67, 0, 0)
			));
		//Reversing
		add(new DriveTrajectoryAction(true,true,
				//new Position(0, 0, 0),
				new Position(11.67, 0, 0),
				new Position(0, -5, 0)
			));
		//Shooting Code
		add(new DriveStopAction());
		//Code checked 1/25/2020 - Seems to work
	}
}
