/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib.auto;

import com.team5104.lib.setup.RobotState;

/** a framework for all paths inside frc.team5104.auto.paths */
public abstract class AutoPath {
  private AutoAction currentAction;
  private boolean isRunning;

  /** Run the path, holding the thread until it is finished.
   * @warning DO NOT CALL IN MAIN THREAD */
  public abstract void start();

  /** Set whether this path is running */
  public final void setRunning(boolean isRunning) {
    this.isRunning = isRunning;
  }

  /** Runs an action, holding the thread until it is finished.
   * @warning DO NOT CALL IN MAIN THREAD
   * @return passthroughs value from action */
  public final boolean run(AutoAction action) {
    if (action == null || AutoManager.pathThreadInterrupted)
      return false;

    currentAction = action;
    currentAction.init();
    while (currentAction != null && !currentAction.isFinished() && !AutoManager.pathThreadInterrupted) {
      try { Thread.sleep(RobotState.getLoopPeriod()); }
      //no code in the loop (just waiting until currentAction is finished)
      catch (InterruptedException e) { }
    }
    boolean value = false;
    if (currentAction != null && !AutoManager.pathThreadInterrupted) {
      currentAction.end();
      value = currentAction.getValue();
      currentAction = null;
    }
    return value;
  }

  /** Updates the current action */
  public final void update() {
    if (currentAction != null && isRunning) {
      currentAction.update();
    }
  }
}
