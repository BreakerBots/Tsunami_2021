/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.frc2021.auto.paths;

import com.team5104.frc2021.auto.actions.DriveTrajectory;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Odometry;
import com.team5104.lib.auto.Position;

public class ExamplePath extends AutoPath {
  public void start() {
    //Set position relative to field
    Odometry.reset(new Position(0, 0));

    run(new DriveTrajectory(false,
                            new Position(0, 0, 0),
                            new Position(10, 10, 90)
    ));
  }
}

