package frc.team5104.frc2021.auto.paths;

import frc.team5104.lib.auto.AutoPath;
import frc.team5104.lib.auto.Odometry;
import frc.team5104.lib.auto.Position;
import frc.team5104.frc2021.auto.actions.DriveTrajectory;

public class GalacticDefault2 extends AutoPath {
	public void start() {
		//Set position relative to field
		Odometry.reset(new Position(0, 10));

		run(new DriveTrajectory(false,
            new Position(0, 10, 0), // Start
            new Position(7.5, 10, -90), // B3
            new Position(7.5, 7.5, -90), // C3
            new Position(12.5, 4.5, 0), // D5
            new Position(15, 4.5, 0), // D6
            new Position(20, 5, -45), // Turn 1
            new Position(20, 2.5, -135), // Turn 2
			new Position(15, 2, -180) // E6
		));
		// Second run
		run(new DriveTrajectory(false,
            new Position(15, 2, -180), // E6
            new Position(10, 7.5, 90), // Swoop point
            new Position(15, 12.5, -45), // A6
            new Position(17.5, 10, 0), // B7,
            new Position(20, 10, 0), // B8
            new Position(22.5, 7.5, -45), // C9
            new Position(25, 5, -45), // D10
            new Position(30, 3, 0) // Finish
		));
	}
}