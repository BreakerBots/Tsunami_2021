package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Odometry;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveTrajectoryAction;

public class AutoNavBounce extends AutoPath {
        public void start() {
            //Set position relative to field
            Odometry.reset(new Position(7.5, 5));
            
            // Starting actions
            run(new DriveTrajectoryAction(false,
                    new Position(7.5, 5, 0), // Start
                    new Position(7.2, 10, 85),
                    // Starts Marker 1 
                    new Position(7.7, 12.9, 90)
            ));
            // Hit Marker 1 (Reverse)
            run(new DriveTrajectoryAction(true, 
                    new Position(7.7, 12.9, 90), // Last point
                    new Position(7.9, 10, 95),
                    new Position(9.6, 6.9, 95),
                    new Position(10.6, 2.5, 180),
                    new Position(14.5, 3.8, 265),
                    // Ends Marker 1, starts Marker 2
                    new Position(15, 9.3, 270)
            ));
            // Hit Marker 2 (Forward)
            run(new DriveTrajectoryAction(false, 
                    new Position(15, 9.3, 270), // Last point
                    new Position(15.5, 3.9, 275),
                    new Position(20.4, 2.4, 0),
                    new Position(22.3, 6.6, 85),
                    // Ends Marker 2, starts Marker 3
                    new Position(22.5, 12.6, 90)
            ));
            // Hit Marker 3 + Finish(Reverse)
            run(new DriveTrajectoryAction(true, 
                    new Position(22.5, 12.6, 90), // Last point
                    new Position(22, 9.2, 150),
                    new Position(25.7, 7.5, 180) // Finish
            ));
        }
    }