/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.frc2021.auto.paths;

import com.team5104.frc2021.auto.actions.DriveTrajectory;
import com.team5104.frc2021.auto.actions.PickupBall;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Odometry;
import com.team5104.lib.auto.Position;

public class ExamplePath extends AutoPath {
  public void start() {
    // Set position relative to field
    Odometry.reset(new Position(0, 0));

    run(new DriveTrajectory(false, new Position(0, 0, 0), new Position(15, 15, 90)));

    if (run(new PickupBall(2000, false))) {
      run(new DriveTrajectory(false, new Position(15, 15, 90), new Position(0, 30, 90)));
    } else {
      run(new DriveTrajectory(false, new Position(15, 15, 90), new Position(30, 30, 90)));
    }
  }
}
