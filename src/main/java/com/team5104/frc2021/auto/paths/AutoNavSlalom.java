package com.team5104.frc2021.auto.paths;

import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Odometry;
import com.team5104.lib.auto.Position;
import com.team5104.frc2021.auto.actions.DriveTrajectory;

public class AutoNavSlalom extends AutoPath {
  public void start() {
    //Set position relative to field
    Odometry.reset(new Position(5, 2.5));
    
    // Start
    run(new DriveTrajectory(false,
      new Position(5, 2.5, 0),  //5, 2.5, 0
      new Position(9, 5, 90),
      new Position(10, 8.5, 0), //7.8, 6.5, 60
      new Position(11.5, 8.5, 0),  //11.5, 7.5 , 0
      new Position(22, 8.5, -10),   //18, 7,-10   TRY 21!!!
      new Position(23, 5, -90), // 23.4, 3.5, -70
      new Position(26.5, 2.5, 30),
      new Position(29, 5.5, 90),
      new Position(24.3, 8.8, -180),
      new Position(23, 5, -90),
      new Position(19.8, 2.3, -180),
      new Position(17.7, 2.3, -180),
      new Position(10.5, 2.3, -180),
      new Position(8.5, 5, 90),
      new Position(2.5, 7.6, 180)
    ));
  }
}