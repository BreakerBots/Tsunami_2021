package com.team5104.frc2021.auto.paths;

import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Odometry;
import com.team5104.lib.auto.Position;
import com.team5104.frc2021.auto.actions.DriveTrajectory;

public class AutoNavBounce extends AutoPath {
  final double MAX_VEL = 6;
  final double MAX_ACC = 4;

  public void start() {
    //Set position relative to field
    Odometry.reset(new Position(5, 7.5));

    // Starting actions
    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
        new Position(1, 7.5, 0), // Start
        // Starts Marker 1
        new Position(8.7, 12.9, 90)
    ));
    // Hit Marker 1 (Reverse)
    run(new DriveTrajectory(true, MAX_VEL, MAX_ACC,
        new Position(8.7, 12.9, 90), // Last point
        //new Position(13.6, 2.5, 120),
        //new Position(15.5, 3.8, 265),
        new Position(9, 10, 120),
        new Position(11.5, 5, 120),
        new Position(12.5, 2.5, 180),
        // Ends Marker 1, starts Marker 2
        new Position(15.5, 12.5, 270)
    ));
    // Hit Marker 2 (Forward)
    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
        new Position(15.5, 12.5, 270), // Last point
        new Position(16.5, 3.9, 275),
        new Position(21.4, 2.4, 10),
        new Position(23.3, 6.6, 95),
        // Ends Marker 2, starts Marker 3
        new Position(23, 13, 90)
    ));
    // Hit Marker 3 + Finish(Reverse)
    run(new DriveTrajectory(true, MAX_VEL, MAX_ACC,
        new Position(23, 13, 90), // Last point
        //new Position(22, 9.2, 150),
        new Position(25.7, 8, 180) // Finish
    ));
  }
}
