/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveStopAction;
import frc.team5104.auto.actions.DriveTrajectoryAction;
import frc.team5104.auto.actions.ShootAction;

public class OLDSimpleLeft extends AutoPath {
	public OLDSimpleLeft() {
		//Robot Position - Farthest position on left (Ball Collection) side
		add(new DriveTrajectoryAction(true, false,
				new Position(0, 0, 0),
				new Position(2, 2, 90),
				new Position(2, 18.33, 90)
			));
		add(new DriveStopAction());
		add(new ShootAction());
	}
}
