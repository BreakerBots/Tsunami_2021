/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveStopAction;
import frc.team5104.auto.actions.DriveTrajectoryAction;
import frc.team5104.auto.actions.IntakeAction;
import frc.team5104.auto.actions.ShootAction;

public class OLDCenter2BallPickup extends AutoPath {
	//Robot Position - Middle of Initiation Line
	public OLDCenter2BallPickup() {
		add(new IntakeAction());
		add(new DriveTrajectoryAction(true, false,
				new Position(0, 0, 0),
				new Position(11, 10.83, 0)
			));
		
		add(new DriveTrajectoryAction(true, true,
				new Position(11, 10.83, 0),
				new Position(0, 5.25, 90)
			));
		add(new DriveStopAction());
		add(new ShootAction());
	}
}
