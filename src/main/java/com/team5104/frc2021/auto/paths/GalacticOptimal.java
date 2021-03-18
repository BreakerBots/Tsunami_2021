package com.team5104.frc2021.auto.paths;

import com.team5104.frc2021.Superstructure;
import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.frc2021.auto.actions.DriveTrajectory;
import com.team5104.frc2021.auto.actions.PickupBall;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Odometry;
import com.team5104.lib.auto.Position;

public class GalacticOptimal extends AutoPath {
  final double MAX_VEL = 6;
  final double MAX_ACC = 4;
  final int WAIT_TIME = 2000;
  int pathChoice = 0;

  //Start
  public void start() {
    //Set position relative to field
    Odometry.reset(new Position(2.5, 10));

    //?
    if (pathChoice == 0) {
      //intake
      Superstructure.set(Mode.INTAKING);

      //?
      run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
          new Position(2.5, 10, 0), // Start
          new Position(7.5, 10, -90) // B3
      ));

      //?
      if (run(new PickupBall(WAIT_TIME))) {
        runBRed();
        /* Ran B-Red (Green cells)
           Next path is A-Red (Red cells) */
        pathChoice = 1;
      }

      //?
      else {
        //?
        run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
            new Position(7.5, 10, -90), // B3, last position
            new Position(7.5, 7.5, -90) // C3
        ));

        if (run(new PickupBall(WAIT_TIME))) {
          runARed();
          /* Ran A-Red (Red cells)
             Next path is B-Red (Green cells) */
          pathChoice = 2;
        }
        else {
          //?
          run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
              new Position(7.5, 7.5, -90), // C3, last position
              new Position(15, 5, 0) // D6
          ));

          if (run(new PickupBall(WAIT_TIME))) {
            runBBlue();
            /* Ran B-Blue (Blue cells)
               Next path is A-Blue (Yellow cells) */
            pathChoice = 4;
          }
          else {
            Superstructure.set(Mode.IDLE);
            runABlue();
            /* Ran A-Blue (Yellow cells)
               Next path is B-Blue (Blue cells) */
            pathChoice = 3;
          }
        }
      }
    }

    // A-Red or Red path
    else if (pathChoice == 1) {
      runARed();
    }

    // B-Red or Green path
    else if (pathChoice == 2) {
      runBRed();
    }

    // B-Blue or Blue path
    else if (pathChoice == 3) {
      runBBlue();
    }

    // A-Blue or Yellow path
    else if (pathChoice == 4) {
      runABlueSolo();
    }
  }

  //A-Red
  public void runARed() {
    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
        new Position(7.5, 7.5, -90), // C3
        new Position(12.5, 5, 0), // D5
        new Position(15, 12.5, 0), // A6
        new Position(30, 12.5, 0) // Finish
    ));
  }

  //B-Red
  public void runBRed() {
    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
        new Position(7.5, 10, -90), // B3, last position
        new Position(12.5, 5, 0), // D5
        new Position(17.5, 10, 0), // B7
        new Position(30, 12, 0) // Finish
    ));
  }

  //B-Blue
  public void runBBlue() {
    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
        new Position(15, 5, 0), // D6, last position
        new Position(20, 10, 0), // B8
        new Position(25, 5, -45), // D10
        new Position(30, 3, 0) // Finish
    ));
  }

  //A-Blue
  public void runABlue() {
    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
        new Position(15, 5, 0), // D6, last position
        new Position(15, 2.5, -90), // E6
        new Position(10, 7.5, 90), // Swoop point
        new Position(17.5, 10, 0), // B7
        new Position(22.5, 7.5, -45), // C9
        new Position(30, 5, 0) // Finish
    ));
  }

  //A-Blue Solo
  public void runABlueSolo() {
    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
        new Position(15, 2.5, 0), // E6
        new Position(17.5, 10, 0), // B7
        new Position(22.5, 7.5, -45), // C9
        new Position(30, 5, 0) // Finish
    ));
  }
}
