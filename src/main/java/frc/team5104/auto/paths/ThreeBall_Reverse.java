/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DelayAction;
import frc.team5104.auto.actions.DriveStopAction;
import frc.team5104.auto.actions.DriveTrajectoryAction;
import frc.team5104.auto.actions.ShootAction;
import frc.team5104.auto.actions.ZeroTurretAction;

/**
 * @startingPosition Front of Power Port
 * Shoots 3 balls
 * Drives forward 7 feet
 */
public class ThreeBall_Reverse extends AutoPath {
	public ThreeBall_Reverse() {
		add(new ZeroTurretAction(0));
		add(new DelayAction(500));
		add(new ShootAction());
		add(new DriveTrajectoryAction(false, false,
				new Position(0, 0, 0),
				new Position(2, 0, 0)
			));
		add(new DriveStopAction());
	}
}
