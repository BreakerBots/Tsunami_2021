/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib.auto;

import com.team5104.lib.setup.RobotState;

/** a framework for all paths inside frc.team5104.auto.paths */
public abstract class AutoPath {
  private AutoAction currentAction;

  /** Run the path, holding the thread until it is finished.
   * @warning DO NOT CALL IN MAIN THREAD */
  public abstract void start();

  /** Runs an action, holding the thread until it is finished.
   * @warning DO NOT CALL IN MAIN THREAD
   * @return passthroughs value from action */
  public boolean run(AutoAction action) {
    if (action == null)
      return false;

    currentAction = action;
    currentAction.init();
    while (currentAction != null && !currentAction.isFinished()) {
      try { Thread.sleep(RobotState.getLoopPeriod()); }
      //no code in the loop (just waiting until currentAction is finished)
      catch (InterruptedException e) { }
    }
    boolean value = false;
    if (currentAction != null) {
      currentAction.end();
      value = currentAction.getValue();
      currentAction = null;
    }
    return value;
  }

  /** Updates the current action */
  public void update() {
    if (currentAction != null)
      currentAction.update();
  }
}
