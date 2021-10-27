package com.team5104.frc2021.auto.unused;

import com.team5104.frc2021.auto.actions.DriveTrajectory;
import com.team5104.frc2021.auto.actions.ResetOdometry;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Position;

public class AutoNavBarrel extends AutoPath {
  final double MAX_VEL = 8;
  final double MAX_ACC = 4;

  public void start() {
    //Set position relative to field
    run(new ResetOdometry(3, 7.5));

    // Evading Marker D5
    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
        new Position(3, 7.5, 0), // Start
        new Position(7.1, 7.5, 0),
        new Position(14.5, 5, -90),
        new Position(12.5, 2.5, -180),
        new Position(8, 5, 90),
    // Evading Marker B8
       // new Position(10.9, 5, 90),
        new Position(20, 7.5, 0),
        new Position(22, 10.0, 90),
        new Position(20, 13, 180),
        new Position(15.5, 10, 270),
    // Evading Marker D10
        new Position(21, 3.5, -45),
        new Position(26, 3.5, 45),
            new Position(26, 7.5, 135),
        new Position(20, 7.5, 180),

       // new Position(22, 7.35, 195),
        //new Position(15.4, 7.35, 165),
        new Position(-2.5, 7.5, 180) // Finish change x back to 2.5
    ));
  }
}
