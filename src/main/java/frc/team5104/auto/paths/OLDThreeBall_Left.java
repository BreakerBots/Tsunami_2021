/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveStopAction;
import frc.team5104.auto.actions.DriveTrajectoryAction;
import frc.team5104.auto.actions.IntakeAction;
import frc.team5104.auto.actions.ShootAction;

public class OLDThreeBall_Left extends AutoPath {
	
	
	public OLDThreeBall_Left() {
		// We changed the path!!  Robot lines up in front of power port,
		// shoots 3 balls, gets 3 balls from our trench
		// returns to shoot
		
		add(new DriveTrajectoryAction(true, false,
				new Position(0, 0, 0),
				new Position(2, 16, 0)
			));
		add(new ShootAction());
		add(new IntakeAction());
		add(new DriveTrajectoryAction(true, false,
				new Position(2, 16, 0),
				new Position(3, 21.5, 0),
				new Position(16.67, 21.5, 0)
				//new Position(-0.42, 6.75, 180)
			));
		
		add(new DriveTrajectoryAction(true, true,
				new Position (16.67, 21.5, 0),
				//new Position (0, 0, 0)
				new Position (0, 21, 90)
				));
		add(new DriveStopAction());
		add(new ShootAction());
	}
}
