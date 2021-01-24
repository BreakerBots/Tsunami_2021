/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveTrajectoryAction;


public class CoordinateExample extends AutoPath {
	public CoordinateExample() {
		Position pos = new Position(7, 0);

		add(new DriveTrajectoryAction(pos, false,
				new Position(0, 0, 0),
				new Position(7.5, 2.5, -60)
			));
	}
}

