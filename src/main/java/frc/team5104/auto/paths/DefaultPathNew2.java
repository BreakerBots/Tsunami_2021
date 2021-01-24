/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveTrajectoryAction;


public class DefaultPathNew2 extends AutoPath {
	public DefaultPathNew2() {//if it needs to reverse change all to true
		add(new DriveTrajectoryAction(false,
				new Position(0, 0, 0),
				new Position(7.5, 2.5, -60)
			));
		add(new DriveTrajectoryAction(true,
				new Position(7.5, 2.5, -60),
				new Position(15, 5, 30)
			));
		add(new DriveTrajectoryAction(false,
				new Position(15, 5, 30),
				new Position(17.5, 2.5, 0)
			));
		add(new DriveTrajectoryAction(false,
				new Position(17.5, 2.5, 0),
				new Position(20, 2.5, 0)
			));
		add(new DriveTrajectoryAction(false,
				new Position(20, 2.5, 0),
				new Position(22.5, 0, 90)
			));
		add(new DriveTrajectoryAction(false,
				new Position(22.5, 0, 90),
				new Position(25, -2.5, 90)
			));
		add(new DriveTrajectoryAction(false,//doesn't pick up a ball, its a weird angle. (also in hindsight horrible place to add a comment)
				new Position(25, -2.5, 90),
				new Position(20, -7.5, 180)
			));
	
		add(new DriveTrajectoryAction(false,
				new Position(15, -7.5, 180),
				new Position(15, -5, 270)
			));
		add(new DriveTrajectoryAction(false,//this could work but might not
				new Position(15, -5, 270),
				new Position(15, -2.5, 225)
			));
		add(new DriveTrajectoryAction(false,
				new Position(15, -2.5, 225),
				new Position(12.5, -2.5, 180)
			));
		add(new DriveTrajectoryAction(false,
				new Position(12.5, -2.5, 180),
				new Position(7.5, 0, 180)
			));
		add(new DriveTrajectoryAction(false,//back to 0,0! idk if needed.
				new Position(7.5, 0, 180),
				new Position(0, 0, 180)
			));
	}
}
