package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveTrajectoryAction;
import frc.team5104.auto.actions.HopperWaitForPickup;

public class ExamplePath extends AutoPath {
    final int WAIT_TIME = 2000;
    int pathChoice = 0;
	public void start() {
		Position start = new Position(0, 10);

        if (pathChoice == 0) {
            run(new DriveTrajectoryAction(start, false,
                    new Position(0, 10, 0), // Start
                    new Position(7.5, 10, -90) // B3
            ));
            if (new HopperWaitForPickup(WAIT_TIME)) {
                runB_Red();
                    // Ran B-Red (Green cells)
                    // Next path is A-Red (Red cells)
                pathChoice = 1;
            }

            else {
                run(new DriveTrajectoryAction(start, false,
                    new Position(7.5, 10, -90), // B3, last position
                    new Position(7.5, 7.5, -90) // C3
                ));
                if (new HopperWaitForPickup(WAIT_TIME)) {
                    runA_Red();
                        // Ran A-Red (Red cells)
                        // Next path is B-Red (Green cells)
                        // Write to text file!!!
                    pathChoice = 2;
                }
                else {
                    run(new DriveTrajectoryAction(start, false,
                        new Position(7.5, 7.5, -90), // C3, last position
                        new Position(15, 5, 0) // D6
                    ));
                    if (new HopperWaitForPickup(WAIT_TIME)) {
                        runB_Blue();
                        // Ran B-Blue (Blue cells)
                        // Next path is A-Blue (Yellow cells)
                        // Write to text file!!!
                        pathChoice = 4;  
                    }
                    else {
                        runA_Blue();
                            // Ran A-Blue (Yellow cells)
                            // Next path is B-Blue (Blue cells)
                            // Write to text file!!!
                        pathChoice = 3;
                    }
                }
            }
        }
        else {
            if (pathChoice == 1) { // Red path or A-Red
               runA_Red();
            }
            if (pathChoice == 2) { // Green path or B-Red
                runB_Red();
            }
            if (pathChoice == 3) { // Blue path or B-Blue
                runB_Blue();
            }
            if (pathChoice == 4) { // Yellow path or A-Blue
                runA_Blue();
            }
        }
    }
    public void runA_Red() {
        run(new DriveTrajectoryAction(start, false,
            new Position(7.5, 7.5, -90), // C3
            new Position(12.5, 5, 0), // D5
            new Position(15, 12.5, 0), // A6
            new Position(30, 12.5, 0) // Finish
        ));
    }
    public void runB_Red() {
        run(new DriveTrajectoryAction(start, false,
            new Position(7.5, 10, -90), // B3, last position
            new Position(12.5, 5, 0), // D5
            new Position(17.5, 10, 0), // B7
            new Position(30, 12, 0) // Finish
        ));
    }
    public void runB_Blue() {
        run(new DriveTrajectoryAction(start, false,
            new Position(15, 5, 0), // D6, last position
            new Position(20, 10, 0), // B8
            new Position(25, 5, -45), // D10
            new Position(30, 3, 0) // Finish
        ));
    }
    public void runA_Blue() {
        run(new DriveTrajectoryAction(start, false,
            new Position(15, 5, 0), // D6, last position
            new Position(15, 2.5, -90), // E6
            new Position(10, 7.5, 90), // Swoop point
            new Position(17.5, 10, 0), // B7
            new Position(22.5, 7.5, -45), // C9
            new Position(30, 5, 0) // Finish
        ));
    } 
}

