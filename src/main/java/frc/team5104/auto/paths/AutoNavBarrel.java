package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveTrajectoryAction;

public class AutoNavBarrel extends AutoPath {
        public void start() {
            Position start = new Position(5, 7.5);
            // Evading Marker D5
            run(new DriveTrajectoryAction(start, false,
                    new Position(5, 7.5, 0), // Start
                    new Position(9.1, 9.1, 0), 
                    new Position(15, 3.4, -90),
                    new Position(10.9, 2.9, -180),
                    new Position(9.2, 6.6, 90)    
            ));
            // Evading Marker B8
            run(new DriveTrajectoryAction(start, false, 
                    new Position(9.2, 6.6, 90), // Last point
                    new Position(23.9, 8.7, 30),
                    new Position(18.4, 13.2, -180),
                    new Position(17, 8.5, 90)
            ));
            // Evading Marker D10
            run(new DriveTrajectoryAction(start, false, 
                    new Position(17, 8.5, 90), // Last point
                    new Position(20.3, 3.9, 45),
                    new Position(26.5, 2.6, 0),
                    new Position(28, 6.9, 90),
                    new Position(22, 7.35, 195),
                    new Position(15.4, 8, 165),
                    new Position(7.5, 0, 180) // Finish
            ));
        }
    }