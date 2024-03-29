package com.team5104.frc2021.auto.unused;

import com.team5104.frc2021.auto.actions.DriveTrajectory;
import com.team5104.frc2021.auto.actions.ResetOdometry;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Position;

public class GalacticDefault extends AutoPath {
  final double MAX_VEL = 6;
  final double MAX_ACC = 4;

  public void start() {
    //Set position relative to field
    run(new ResetOdometry(0, 10));

    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
        new Position(0, 10, 0), // Start
        new Position(7.5, 10, -90), // B3
        new Position(7.5, 7.5, -90), // C3
        new Position(12.5, 5, 0), // D5
        new Position(15, 5, 0), // D6
        new Position(15, 2.5, -90) // E6
    ));
    // Second run
    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
        new Position(15, 2.5, -90), // E6
        new Position(10, 7.5, 90), // Swoop point
        new Position(15, 12.5, -45), // A6
        new Position(17.5, 10, 0), // B7,
        new Position(20, 10, 0), // B8
        new Position(22.5, 7.5, -45), // C9
        new Position(25, 5, -45), // D10
        new Position(30, 3, 0) // Finish
    ));
  }
}
