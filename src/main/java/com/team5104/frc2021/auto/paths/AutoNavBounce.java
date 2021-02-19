package com.team5104.frc2021.auto.paths;

import com.team5104.frc2021.auto.actions.DriveTrajectory;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Odometry;
import com.team5104.lib.auto.Position;

public class AutoNavBounce extends AutoPath {
  public void start() {
    // Set position relative to field
    Odometry.reset(new Position(5, 7.5));

    // Starting actions
    run(
        new DriveTrajectory(
            false,
            new Position(5, 7.5, 0), // Start
            // Starts Marker 1
            new Position(7.7, 12.9, 90)));
    // Hit Marker 1 (Reverse)
    run(
        new DriveTrajectory(
            true,
            new Position(7.7, 12.9, 90), // Last point
            new Position(7.9, 10, 95),
            new Position(10.6, 4.9, 95),
            new Position(11.6, 2.5, 160),
            new Position(15.5, 3.8, 265),
            // Ends Marker 1, starts Marker 2
            new Position(15, 12.5, 270)));
    // Hit Marker 2 (Forward)
    run(
        new DriveTrajectory(
            false,
            new Position(15, 12.5, 270), // Last point
            new Position(15.5, 3.9, 275),
            new Position(20.4, 2.4, 10),
            new Position(22.3, 6.6, 95),
            // Ends Marker 2, starts Marker 3
            new Position(22.5, 12.6, 90)));
    // Hit Marker 3 + Finish(Reverse)
    run(
        new DriveTrajectory(
            true,
            new Position(22.5, 12.6, 90), // Last point
            // new Position(22, 9.2, 150),
            new Position(25.7, 7.5, 180) // Finish
            ));
  }
}
