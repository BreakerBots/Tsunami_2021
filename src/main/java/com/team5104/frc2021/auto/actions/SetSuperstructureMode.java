package com.team5104.frc2021.auto.actions;

import com.team5104.frc2021.Superstructure;
import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.lib.auto.AutoAction;

public class SetSuperstructureMode extends AutoAction {
  private Mode mode;

  public SetSuperstructureMode(Mode mode) {
    this.mode = mode;
  }

  public void init() {
    Superstructure.set(mode);
  }

  public void update() { }

  public boolean isFinished() {
    return true;
  }

  public void end() { }
}

