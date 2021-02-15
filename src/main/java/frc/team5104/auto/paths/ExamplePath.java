/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Odometry;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveTrajectory;
import frc.team5104.auto.actions.PickupBall;

public class ExamplePath extends AutoPath {
	public void start() {
		//Set position relative to field
		Odometry.reset(new Position(0, 0));

		run(new DriveTrajectory(false,
			new Position(0, 0, 0),
			new Position(15, 15, 90)
		));

		if (run(new PickupBall(2000, false))) {
			run(new DriveTrajectory(false,
				new Position(15, 15, 90),
				new Position(0, 30, 90)
			));
		}
		else {
			run(new DriveTrajectory(false,
                new Position(15, 15, 90),
                new Position(30, 30, 90)
			));
		}
	}
}

