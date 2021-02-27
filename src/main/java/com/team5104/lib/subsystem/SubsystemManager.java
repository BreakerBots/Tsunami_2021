/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib.subsystem;

import com.team5104.lib.Looper;
import com.team5104.lib.Looper.Crash;
import com.team5104.lib.console;
import com.team5104.lib.subsystem.Subsystem.SubsystemMode;

public class SubsystemManager {
  private static Subsystem[] attachedSubsystems;

  /** Specify the subsystems attached to the robot */
  public static void attach(Subsystem... subsystems) {
    //Save all subsystems
    attachedSubsystems = subsystems;

    //Initialize Subsystem's Interface & Print out target subsystems
    StringBuilder message = new StringBuilder();
    message.append("running subsystems: ");
    String prefix = "";
    for (Subsystem subsystem : attachedSubsystems) {
      try {
        subsystem.mode = SubsystemMode.OPERATING;
        subsystem.identifyDevices();
        subsystem.reset();
        message.append(prefix);
        prefix = ", ";
        message.append(subsystem.getClass().getSimpleName());
      } catch (Exception e) { Looper.logCrash(new Crash(e)); }
    }
    console.log(message);

    //Fast Update
    Looper.attach(() -> {
      for (Subsystem subsystem : attachedSubsystems) {
        try {
          subsystem.fastUpdate();
        } catch (Exception e) { Looper.logCrash(new Crash(e)); }
      }
    }, "Fast");
  }

  /** Stop subsystems (feed thru) */
  public static void stop() {
    for (Subsystem subsystem : attachedSubsystems) {
      try {
        subsystem.stop();
      } catch (Exception e) { Looper.logCrash(new Crash(e)); }
    }
  }

  /** Reset subsystems (feed thru) */
  public static void reset() {
    for (Subsystem subsystem : attachedSubsystems) {
      try {
        subsystem.reset();
      } catch (Exception e) { Looper.logCrash(new Crash(e)); }
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
      } catch (Exception e) { Looper.logCrash(new Crash(e)); }
    }
  }

  /** Returns the attached subsystems */
  public static Subsystem[] getSubsystems() {
    return attachedSubsystems;
  }
}
