/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib.subsystem;

import com.team5104.lib.CrashHandler;
import com.team5104.lib.Looper;
import com.team5104.lib.console;
import com.team5104.lib.subsystem.Subsystem.SubsystemMode;

import java.util.ArrayList;
import java.util.List;

public class SubsystemManager {
  private static Subsystem[] attachedSubsystems;

  /** Specify the subsystems attached to the robot */
  public static void attach(Subsystem... subsystems) {
    //Save all subsystems
    attachedSubsystems = subsystems;

    //Initialize Subsystem's Interface & Print out target subsystems
    List<String> subsystemNames = new ArrayList<>();
    for (Subsystem subsystem : attachedSubsystems) {
      try {
        subsystem.mode = SubsystemMode.OPERATING;
        subsystem.identifyDevices();
        subsystem.reset();
        subsystemNames.add(subsystem.getClass().getSimpleName());
      } catch (Exception e) { CrashHandler.log(e); }
    }
    console.log("running subsystems: ", subsystemNames);

    //Fast Update
    Looper.attach(() -> {
      for (Subsystem subsystem : attachedSubsystems) {
        try {
          subsystem.fastUpdate();
        } catch (Exception e) { CrashHandler.log(e); }
      }
    }, "Fast");
  }

  /** Stop subsystems (feed thru) */
  public static void stop() {
    for (Subsystem subsystem : attachedSubsystems) {
      try {
        subsystem.stop();
      } catch (Exception e) { CrashHandler.log(e); }
    }
  }

  /** Reset subsystems (feed thru) */
  public static void reset() {
    for (Subsystem subsystem : attachedSubsystems) {
      try {
        subsystem.reset();
      } catch (Exception e) { CrashHandler.log(e); }
    }
  }

  /** Updates subsystems (feed thru) */
  public static void update() {
    for (Subsystem subsystem : attachedSubsystems) {
      try {
        //force stop if disabled
        if (subsystem.is(SubsystemMode.DISABLED))
          subsystem.stop();

        //otherwise update like normal
        else subsystem.update();
      } catch (Exception e) { CrashHandler.log(e); }
    }
  }

  /** Returns the attached subsystems */
  public static Subsystem[] getSubsystems() {
    return attachedSubsystems;
  }
}
