/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveStopAction;
import frc.team5104.auto.actions.DriveTrajectoryAction;
import frc.team5104.auto.actions.IdleAction;
import frc.team5104.auto.actions.IntakeAction;
import frc.team5104.auto.actions.ShootAction;
import frc.team5104.auto.actions.ZeroTurretAction;

/**
 * @startingPosition Left of the Field
 * Intakes 2 from enemy Trench
 * Goes to Power Port to shoot 5
 * Intakes 3 from our Trench
 * Returns to Power Port to shoot 3
 */
public class EightBall_Left extends AutoPath {
	public EightBall_Left() {
		add(new ZeroTurretAction(0));
		add(new IntakeAction());
		add(new DriveTrajectoryAction(true, false,
				new Position(0, 0, 0),
				new Position(7.5, 0, -60)
			));
		add(new DriveTrajectoryAction(true, true,
				new Position(7.5, 0, -60),
				new Position(4, 16, -90)
			));
		add(new DriveStopAction());
		add(new ShootAction());
		add(new DriveTrajectoryAction(true, true,
				new Position(4, 16, -90),
				new Position(0, 22, 0)
			));
		add(new IntakeAction());
		add(new DriveTrajectoryAction(true, false,
				new Position(0, 22, 0),
				new Position(14, 22, 0)
			));
		add(new IdleAction());
		add(new DriveTrajectoryAction(true, true,
				new Position(14, 22, 0),
				new Position(4, 19, 45)
			));
		add(new ShootAction());
	}
}