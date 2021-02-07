package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Odometry;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveTrajectoryAction;
import frc.team5104.auto.actions.HopperWaitForPickup;

public class GalacticOptimal extends AutoPath {
    final int WAIT_TIME = 2000;
	public void start() {
        //Set position relative to field
        Odometry.reset(new Position(0, 10));

		run(new DriveTrajectoryAction( false,
				new Position(0, 10, 0), // Start
				new Position(7.5, 10, -90) // B3
        ));
        if (run(new HopperWaitForPickup(WAIT_TIME))) {
            run(new DriveTrajectoryAction(false,
                new Position(7.5, 10, -90), // B3, last position
                new Position(12.5, 5, 0), // D5
                new Position(17.5, 10, 0), // B7
                new Position(30, 12, 0) // Finish
                // Ran B-Red (Green cells)
                // Next path is A-Red (Red cells)
                // Write to text file!!!
            ));
        }

        else {
            run(new DriveTrajectoryAction(false,
                new Position(7.5, 10, -90), // B3, last position
                new Position(7.5, 7.5, -90) // C3
            ));
            if (run(new HopperWaitForPickup(WAIT_TIME))) {
                run(new DriveTrajectoryAction(false,
                    new Position(7.5, 7.5, -90), // C3
                    new Position(12.5, 5, 0), // D5
                    new Position(15, 12.5, 0), // A6
                    new Position(30, 12.5, 0) // Finish
                    // Ran A-Red (Red cells)
                    // Next path is B-Red (Green cells)
                    // Write to text file!!!
                ));
            }
            else {
                run(new DriveTrajectoryAction(false,
                    new Position(7.5, 7.5, -90), // C3, last position
                    new Position(15, 5, 0) // D6
                ));
                if (run(new HopperWaitForPickup(WAIT_TIME))) {
                    run(new DriveTrajectoryAction(false,
                    new Position(15, 5, 0), // D6, last position
                    new Position(20, 10, 0), // B8
                    new Position(25, 5, -45), // D10
                    new Position(30, 3, 0) // Finish
                    // Ran B-Blue (Blue cells)
                    // Next path is A-Blue (Yellow cells)
                    // Write to text file!!!
                    ));
                }
                else {
                    run(new DriveTrajectoryAction(false,
                        new Position(15, 5, 0), // D6, last position
                        new Position(15, 2.5, -90), // E6
                        new Position(10, 7.5, 90), // Swoop point
                        new Position(17.5, 10, 0), // B7
                        new Position(22.5, 7.5, -45), // C9
                        new Position(30, 5, 0) // Finish
                        // Ran A-Blue (Yellow cells)
                        // Next path is B-Blue (Blue cells)
                        // Write to text file!!!
                    ));
                }
            }
        }
    }
}