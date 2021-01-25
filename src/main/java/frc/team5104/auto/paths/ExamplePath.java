/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveTrajectoryAction;
import frc.team5104.auto.actions.HopperWaitForPickup;

public class ExamplePath extends AutoPath {
	public void start() {
		Position start = new Position(0, 0);

		run(new DriveTrajectoryAction(start, false,
				new Position(0, 0, 0),
				new Position(15, 15, 90)
		));

		if (run(new HopperWaitForPickup(2000, false))) {
			run(new DriveTrajectoryAction(start, false,
					new Position(15, 15, 90),
					new Position(0, 30, 90)
			));
		}
		else {
			run(new DriveTrajectoryAction(start, false,
					new Position(15, 15, 90),
					new Position(30, 30, 90)
			));
		}
	}
}

