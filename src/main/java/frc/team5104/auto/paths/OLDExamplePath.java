/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveStopAction;
import frc.team5104.auto.actions.DriveTrajectoryAction;

public class OLDExamplePath extends AutoPath {
	public OLDExamplePath() {
		add(new DriveTrajectoryAction(true, false,
				new Position(0, 0, 0),
				new Position(8, 3, 0)
			));
	/*	add(new DriveTrajectoryAction(true, false,
				new Position(0, 0, 0),
				new Position(12, 0, 0)
			));
		add(new DriveTrajectoryAction(true, true,
				new Position(12, 0, 0),
				new Position(0, 0, 0)
			)); */
		add(new DriveStopAction());
	}
}
