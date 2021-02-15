package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Odometry;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveTrajectory;

public class AutoNavSlalom extends AutoPath {
	public void start() {
		//Set position relative to field
		Odometry.reset(new Position(5, 2.5));

		// Start
		run(new DriveTrajectory(false,
            new Position(5, 2.5, 0), // Start
            new Position(7.8, 6.5, 80),
            new Position(11.5, 7.6, 0),
            new Position(22.7, 6.3, -60),
            new Position(23.4, 3.5, -80),
            new Position(26.5, 2.5, 0),
            // Returning
            new Position(28.2, 6.5, -90),
            new Position(24.3, 8.8, -180),
            new Position(21.9, 3.4, 100),
            new Position(18.7, 2, 180),
            new Position(8.5, 3.1, 170),
            new Position(7.1, 6.3, 100),
            new Position(0.9, 7.6, 180) // Finish
		));
	}
}