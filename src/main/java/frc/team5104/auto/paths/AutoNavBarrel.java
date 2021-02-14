package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Odometry;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveTrajectoryAction;

public class AutoNavBarrel extends AutoPath {
        public void start() {
            //Set position relative to field
            Odometry.reset(new Position(5, 7.5));
            
            // Evading Marker D5
            run(new DriveTrajectoryAction(false,
                    new Position(5, 7.5, 0), // Start
                    new Position(9.1, 7.5, 0),
                    new Position(15, 5, -90),
                    new Position(10.9, 2.9, -220),
                    new Position(9.2, 8.2, 70)
            ));
            // Evading Marker B8
            run(new DriveTrajectoryAction(false, 
                    new Position(9.2, 8.2, 70), // Last point
                    new Position(21.9, 7.6, 0),
                    new Position(23.6, 11.0, 110),
                    new Position(18.4, 13.2, 180),
                    new Position(17, 8.5, 290)
            ));
            // Evading Marker D10
            run(new DriveTrajectoryAction(false, 
                    new Position(17, 8.5, 290), // Last point
                    new Position(20.3, 3.9, 325),
                    new Position(26.5, 2.6, 0),
                    new Position(28, 5.7, 120),
                    new Position(22, 7.35, 195),
                    new Position(15.4, 8, 165),
                    new Position(0, 7.5, 180) // Finish
            ));
        }
    }