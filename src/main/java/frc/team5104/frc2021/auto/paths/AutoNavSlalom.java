package frc.team5104.frc2021.auto.paths;

import frc.team5104.lib.auto.AutoPath;
import frc.team5104.lib.auto.Odometry;
import frc.team5104.lib.auto.Position;
import frc.team5104.frc2021.auto.actions.DriveTrajectory;

public class AutoNavSlalom extends AutoPath {
	public void start() {
		//Set position relative to field
		Odometry.reset(new Position(5, 2.5));
		
		// Start
		run(new DriveTrajectory(false,
			new Position(5, 2.5, 0),
			new Position(7.8, 6.5, 60),
			new Position(11.5, 7.5, 0),
			new Position(18, 7, -10),
			new Position(23.4, 3.5, -70),
			new Position(26.5, 2.5, 30),
			new Position(28.2, 5.5, 90),
			new Position(24.3, 8.8, -160),
			new Position(21.9, 3.4, -130),
			new Position(18.7, 2.3, -170),
			new Position(8.5, 3.1, 160),
			new Position(6.1, 5.9, 130),
			new Position(0.9, 7.6, 180)
		));
	}
}