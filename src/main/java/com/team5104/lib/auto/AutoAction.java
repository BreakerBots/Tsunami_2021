/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib.auto;

/**
 * A framework for all actions inside frc.team5104.auto.actions
 */
public abstract class AutoAction {
  /** Called when action is started to be run */
  public abstract void init();
  
  /** Called periodically when the action is being run */
  public abstract void update();

  /** @return if the action is finished */
  public abstract boolean isFinished();

  /** Called when the action is finished being run */
  public abstract void end();

    /** Passes through a value */
  public boolean getValue() { return false; }
}
