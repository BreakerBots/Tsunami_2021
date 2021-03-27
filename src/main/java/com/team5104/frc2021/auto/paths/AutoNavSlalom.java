package com.team5104.frc2021.auto.paths;

import com.team5104.frc2021.auto.actions.DriveTrajectory;
import com.team5104.frc2021.auto.actions.ResetOdometry;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Position;

public class AutoNavSlalom extends AutoPath {
  final double MAX_VEL = 10; //OG: 6 Final: 8
  final double MAX_ACC = 3; //OG: 4 Final: 4

  public void start() {
    //Set position relative to field
    run(new ResetOdometry(2.5, 2.5));

    // Start
    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
      new Position(2.5, 2.5, 0),
      new Position(6.5, 5, 60),
      new Position(10, 8, 20),
      new Position(17, 8, 0),
      new Position(19.5, 5, -60),
      new Position(24.5, 3, 45),
      new Position(21.5, 10, -180),
      new Position(19, 6, -135),
      new Position(15, 3.5, -170),
      new Position(7.5, 3.5, 180),
      new Position(5, 8.5, 110),
      new Position(-5, 9.5, 180)
    ));

//    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
//            new Position(2.5, 2.5, 0),
//            new Position(7, 5, 90),
//            new Position(10, 8, 20),
//            new Position(20, 8, -20),
//            new Position(21, 5, -90),
//            new Position(25.5, 3, 45),
//            new Position(23, 8.5, -180),
//            new Position(20.5, 5, -110),
//            new Position(18, 2.5, -180),
//            new Position(10, 2.5, 160),
//            new Position(5.5, 7.5, 110),
//            new Position(0, 9, 180)
//    ));


  }
}
