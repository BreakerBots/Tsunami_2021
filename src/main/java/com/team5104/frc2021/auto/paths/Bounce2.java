package com.team5104.frc2021.auto.paths;

import com.team5104.frc2021.auto.actions.DriveTrajectory;
import com.team5104.frc2021.auto.actions.ResetOdometry;
import com.team5104.lib.auto.AutoAction;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Position;

public class Bounce2 extends AutoPath {
  final double MAX_VEL = 10; //OG: 6
  final double MAX_ACC = 3; //OG: 4

  final AutoAction[] trajectories = {
          new DriveTrajectory(false, MAX_VEL, MAX_ACC,
                  new Position(1, 7.5, 0), // Start
                  // Starts Marker 1
                  new Position(8.7, 12.9, 90)
          ),
          // Hit Marker 1 (Reverse)
          new DriveTrajectory(true, MAX_VEL, MAX_ACC,
                  new Position(8.7, 12.9, 90), // Last point
                  //new Position(13.6, 2.5, 120),
                  //new Position(15.5, 3.8, 265),
                  new Position(9, 10, 120),
                  new Position(11.5, 5, 120),
                  //new Position(12.5, 2.5, 180),
                  new Position (15, 3, 220),
                  // Ends Marker 1, starts Marker 2
                  new Position(15.5, 15, 270)
          ),
          // Hit Marker 2 (Forward)
          new DriveTrajectory(false, MAX_VEL, MAX_ACC,
                  new Position(15.5, 15, 270), // Last point
                  new Position(16.5, 7, 275),
                  new Position(24.5, 5.4, 45),
//                  new Position(24.3, 9.6, 95),
                  // Ends Marker 2, starts Marker 3
                  new Position(24, 17, 100)
          ),
          // Hit Marker 3 and finish (Reverse)
          new DriveTrajectory(true, MAX_VEL, MAX_ACC,
                  new Position(24, 17, 100), // Last point
                  //new Position(22, 9.2, 150),
                  new Position(27.5, 13, 180) // Finish
          ),
  };

  public void start() {
    //Set position relative to field
    run(new ResetOdometry(5, 7.5));

    run(trajectories[0]);
    run(trajectories[1]);
    run(trajectories[2]);
    run(trajectories[3]);

//    // Starting actions
//    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
//        new Position(1, 7.5, 0), // Start
//        // Starts Marker 1
//        new Position(8.7, 12.9, 90)
//    ));
//
//    // Hit Marker 1 (Reverse)
//    run(new DriveTrajectory(true, MAX_VEL, MAX_ACC,
//        new Position(8.7, 12.9, 90), // Last point
//        //new Position(13.6, 2.5, 120),
//        //new Position(15.5, 3.8, 265),
//        new Position(9, 10, 120),
//        new Position(11.5, 5, 120),
//        new Position(12.5, 2.5, 180),
//        // Ends Marker 1, starts Marker 2
//        new Position(15.5, 12.5, 270)
//    ));
//    // Hit Marker 2 (Forward)
//    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
//        new Position(15.5, 12.5, 270), // Last point
//        new Position(16.5, 3.9, 275),
//        new Position(21.4, 2.4, 10),
//        new Position(23.3, 6.6, 95),
//        // Ends Marker 2, starts Marker 3
//        new Position(23, 13, 90)
//    ));
//    // Hit Marker 3 + Finish(Reverse)
//    run(new DriveTrajectory(true, MAX_VEL, MAX_ACC,
//        new Position(23, 13, 90), // Last point
//        //new Position(22, 9.2, 150),
//        new Position(25.7, 8, 180) // Finish
//    ));
  }
}
