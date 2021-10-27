/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.frc2021.auto.unused;

import com.team5104.frc2021.auto.actions.DriveTrajectory;
import com.team5104.frc2021.auto.actions.ResetOdometry;
import com.team5104.lib.auto.AutoAction;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Position;

public class ExamplePath2 extends AutoPath {
  final double MAX_VEL = 6;
  final double MAX_ACC = 4;

  final AutoAction[] trajectories = {
      new DriveTrajectory(
false, MAX_VEL, MAX_ACC,
          new Position(0, 0, 0),
          new Position(10, 10, 90)
      )
  };

  public void start() {
    //Set position relative to field
    run(new ResetOdometry(0, 0));

    run(trajectories[0]);
  }
}
