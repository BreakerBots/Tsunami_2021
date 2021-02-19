/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.frc2021.auto.actions;

import com.team5104.frc2021.subsystems.Hopper;
import com.team5104.lib.auto.AutoAction;
import com.team5104.lib.console;
import com.team5104.lib.setup.RobotState;

public class PickupBall extends AutoAction {
  private boolean intaked, simulationValue;
  private long startTime;
  private int timeoutMs;

  public PickupBall(int timeoutMs) {
    this(timeoutMs, false);
  }

  public PickupBall(int timeoutMs, boolean simulationValue) {
    this.timeoutMs = timeoutMs;
    this.simulationValue = simulationValue;
  }

  public void init() {
    console.log("Waiting for hopper to intake a ball");
    startTime = System.currentTimeMillis();
  }

  public void update() {
    if (RobotState.isSimulation() ? false : Hopper.isEntrySensorTrippedAvg()) intaked = true;
  }

  public boolean isFinished() {
    return intaked || (System.currentTimeMillis() >= startTime + timeoutMs);
  }

  public void end() {}

  public boolean getValue() {
    return RobotState.isSimulation() ? simulationValue : intaked;
  }
}
