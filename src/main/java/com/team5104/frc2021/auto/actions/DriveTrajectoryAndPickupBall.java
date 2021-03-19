package com.team5104.frc2021.auto.actions;

import com.team5104.frc2021.subsystems.Hopper;
import com.team5104.lib.auto.AutoAction;
import com.team5104.lib.auto.Position;

public class DriveTrajectoryAndPickupBall extends AutoAction {
  private DriveTrajectory driveTrajectoryAction;
  private boolean pickedUp;

  public DriveTrajectoryAndPickupBall(boolean isReversed, double maxVel, double maxAcc, Position... waypoints) {
    this.driveTrajectoryAction = new DriveTrajectory(isReversed, maxVel, maxAcc, waypoints);
    this.pickedUp = false;
  }

  public void init() {
    driveTrajectoryAction.init();
  }

  public void update() {
    driveTrajectoryAction.update();
    if (Hopper.isEntrySensorTrippedAvg()) {
      pickedUp = true;
    }
  }

  public boolean isFinished() {
    return driveTrajectoryAction.isFinished();
  }

  public void end() {
    driveTrajectoryAction.end();
  }

  public boolean getValue() {
    return pickedUp;
  }
}

