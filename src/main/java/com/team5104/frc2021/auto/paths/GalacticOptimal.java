package com.team5104.frc2021.auto.paths;

import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.frc2021.auto.actions.*;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Position;

public class GalacticOptimal extends AutoPath {
  final double MAX_VEL = 2; //OG : 6 and 4
  final double MAX_ACC = 2;
  final int WAIT_TIME = 2000;
  int pathChoice = 0;

  //Start
  public void start() {
    System.out.println("running yay pls good things pls!");
    //Set position relative to field
    run(new ResetOdometry(2.5, 10));
    // try 2.5, 15, -45

    System.out.println(pathChoice);
    //?
    if (pathChoice == 0) {
      //intake
      run(new SetSuperstructureMode(Mode.INTAKING));

      //?
      System.out.println("Check B3!!");
      boolean intaked = run(new DriveTrajectoryAndPickupBall(
          false, MAX_VEL, MAX_ACC,
          new Position(2.5, 10, 0), // Start
          new Position(7.5, 10, -90) // B3
      ));
      System.out.println(intaked);

      //?
      if (intaked || run(new PickupBall(WAIT_TIME))) {
        System.out.println("Running path B-Red");
        runBRed();
        /* Ran B-Red (Green cells)
           Next path is A-Red (Red cells) */
        pathChoice = 1;
      }

      //?
      else {
        //?
        System.out.println("Check C3!!");
        intaked = run(new DriveTrajectoryAndPickupBall(false, MAX_VEL, MAX_ACC,
            new Position(7.5, 10, -90), // B3, last position
            new Position(7.5, 7.5, -90) // C3
        ));
        System.out.println(intaked);

        if (intaked || run(new PickupBall(WAIT_TIME))) {
          System.out.println("Running A-Red");
          runARed();
          /* Ran A-Red (Red cells)
             Next path is B-Red (Green cells) */
          pathChoice = 2;
        }
        else {
          //?
          System.out.println("Check D6!!");
          intaked = run(new DriveTrajectoryAndPickupBall(false, MAX_VEL, MAX_ACC,
              new Position(7.5, 7.5, -90), // C3, last position
              new Position(15, 5, 0) // D6
          ));
          System.out.println(intaked);

          if (intaked || run(new PickupBall(WAIT_TIME))) {
            System.out.println("Running B-Blue");
            runBBlue();
            /* Ran B-Blue (Blue cells)
               Next path is A-Blue (Yellow cells) */
            pathChoice = 4;
          }
          else {
            System.out.println("Checked all, moving on!!");
//            Superstructure.set(Mode.IDLE);
            System.out.println("Running A-Blue");
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
      System.out.println("We are running second path: A-Red");
      runARed();
    }

    // B-Red or Green path
    else if (pathChoice == 2) {
      System.out.println("We are running second path: B-red");
      runBRed();
    }

    // B-Blue or Blue path
    else if (pathChoice == 3) {
      System.out.println("We are running second path: B-Blue");
      runBBlue();
    }

    // A-Blue or Yellow path
    else if (pathChoice == 4) {
      System.out.println("We are running second path: A-Blue");
      runABlueSolo();
    }
  }

  //A-Red
  public void runARed() {
    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
            new Position(2.5, 10, 0), // Start
        new Position(7.5, 7.5, -90), // C3
        new Position(12.5, 4.5, 0), // D5
        new Position(14.5, 12.5, 90), // A6
        new Position(30, 10, 0) // Finish
    ));
  }

  //B-Red
  public void runBRed() {
    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
            new Position(2.5, 10, 0), // Start
        new Position(7.5, 10, -90), // B3, last position
        new Position(12.5, 5, 0), // D5
        new Position(17.5, 10.5, 0), // B7
        new Position(30, 12, 0) // Finish
    ));
  }

  //B-Blue
  public void runBBlue() {
    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
            new Position(2.5, 10, 0), // Start
        new Position(15, 5, 0), // D6, last position
        new Position(20, 10.5, 0), // B8
        new Position(25, 5, -45), // D10
        new Position(30, 3, 0) // Finish
    ));
  }

  //A-Blue
  public void runABlue() {
    run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
            new Position(2.5, 10, 0), // Start
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
        new Position(15, 2.5, 0), // E6 -- note to smol: back up first, no loop
        new Position(17.5, 10, 0), // B7
        new Position(22.5, 7.5, -45), // C9
        new Position(30, 5, 0) // Finish
    ));
  }
}
