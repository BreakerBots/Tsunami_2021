/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib.teleop;

import com.team5104.lib.Looper;
import com.team5104.lib.Looper.Crash;
import com.team5104.lib.console;

import java.util.ArrayList;
import java.util.List;

/** Manages all the calls for all Teleop Controllers given */
public class TeleopManager {
  private static TeleopController[] targetTeleopControllers;

  /** Tell the State Machine Manager what State Machines to use */
  public static void use(TeleopController... teleopControllers) {
    targetTeleopControllers = teleopControllers;

    //Print out
    List<String> names = new ArrayList<>();
    for (TeleopController teleopController : teleopControllers) {
      names.add(teleopController.getClass().getSimpleName());
    }
    console.log("Running Teleop Controllers: ", names);
  }

  /** Call periodically when the robot is enabled (and wants teleoperation) */
  public static void update() {
    for (TeleopController teleopController : targetTeleopControllers) {
      try {
        teleopController.update();
      } catch (Exception e) { Looper.logCrash(new Crash(e)); }
    }
  }

  /** Call once the robot becomes enabled */
  public static void enabled() {
    for (TeleopController teleopController : targetTeleopControllers) {
      try {
        teleopController.enabled();
      } catch (Exception e) { Looper.logCrash(new Crash(e)); }
    }
  }

  /** Call once the robot becomes disabled */
  public static void disabled() {
    for (TeleopController teleopController : targetTeleopControllers) {
      try {
        teleopController.disabled();
      } catch (Exception e) { Looper.logCrash(new Crash(e)); }
    }
  }
}
