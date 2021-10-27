package com.team5104.frc2021.auto.unused;

import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.frc2021.auto.actions.DriveTrajectory;
import com.team5104.frc2021.auto.actions.ResetOdometry;
import com.team5104.frc2021.auto.actions.SetSuperstructureMode;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Position;

public class GalacticDefault2 extends AutoPath {
  final double MAX_VEL = 2; // Originally 6 and 4
  final double MAX_ACC = 2;

  public void start() {
    //Set position relative to field
    run(new ResetOdometry(2.5, 10));

    System.out.println("We are supposed to be intaking now idk tho");
    run(new SetSuperstructureMode(Mode.INTAKING));

    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
        new Position(2.5, 10, 0), // Start
        new Position(7.5, 10, -90), // B3
        new Position(7.5, 7.5, -90), // C3
        new Position(12.5, 5, 0), // D5
        new Position(15, 5, 0), // D6
        //new Position(20, 5, -45), // Turn 1
        //new Position(20, 2.5, -135), // Turn 2
        new Position(15, 2.5, -90) // E6
    ));
    // Second run
    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
        new Position(15, 2.5, -90), // E6
        new Position(10, 0, 180),
        new Position(7.5, 7.5, 90),
        new Position(15, 12.5, 0), // A6
        new Position(17.5, 10, 0), // B7,
        new Position(20, 10, 0), // B8
        new Position(22.5, 7.5, -45), // C9
        new Position(25, 5, -45), // D10
        new Position(30, 3, 0) // Finish
    ));
  }
}
