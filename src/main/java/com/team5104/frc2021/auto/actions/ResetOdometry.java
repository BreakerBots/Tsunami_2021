package com.team5104.frc2021.auto.actions;

import com.team5104.lib.auto.AutoAction;
import com.team5104.lib.auto.Odometry;
import com.team5104.lib.auto.Position;

public class ResetOdometry extends AutoAction {
  private Position position;

  public ResetOdometry(double xFeet, double yFeet) {
    this.position = new Position(xFeet, yFeet);
  }

  public void init() {
    Odometry.reset(position);
  }

  public void update() { }

  public boolean isFinished() {
    return true;
  }

  public void end() { }
}

