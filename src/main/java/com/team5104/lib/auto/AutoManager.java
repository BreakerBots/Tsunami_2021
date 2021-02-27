/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib.auto;

import com.team5104.lib.Compressor;
import com.team5104.lib.Looper;
import com.team5104.lib.Looper.Crash;
import com.team5104.lib.Looper.Loop;
import com.team5104.lib.console;
import com.team5104.lib.dashboard.DashboardTrajectory;
import com.team5104.lib.setup.RobotState;
import com.team5104.lib.subsystem.Characterizer;

/** manages the running of an autonomous path and characterizing */
public class AutoManager {
  private static AutoPath targetPath;
  private static Thread pathThread;

  //Enabled/Disabled
  public static void enabled() {
    if (Characterizer.isRunning()) {
      Characterizer.enabled();
    }
    else if (targetPath != null) {
      /*Spawn path thread -- a thread that waits through each action.
         Calls action init(), isFinished(), end(), and getValue() but not update() <-- called below */
      pathThread = new Thread(() -> {
        try {
          console.log("Running " + targetPath.getClass().getSimpleName());
          DashboardTrajectory.alertStartingPath();
          targetPath.start();
          console.log(targetPath.getClass().getSimpleName() + " finished");
        } catch (Exception e) { Looper.logCrash(new Crash(e)); }
      });
      pathThread.start();
      Looper.registerLoop(new Loop("Auto", pathThread, 8));
    }
  }
  public static void disabled() {
    if (pathThread != null) {
      pathThread.interrupt();
      pathThread = null;
    }
    if (Characterizer.isRunning())
      Characterizer.disabled();
  }

  //Init
  public static void setTargetPath(AutoPath path) {
    setTargetPath(path, false);
  }
  public static void setTargetPath(AutoPath path, boolean dontSend) {
    console.log("target path is now " + path.getClass().getSimpleName());
    targetPath = path;
    if (!dontSend) {
      DashboardTrajectory.sendTargetPath();
    }
  }
  public static AutoPath getTargetPath() {
    return targetPath;
  }

  //Update
  public static void update() {
    //update the path
    if (targetPath != null)
      targetPath.update();

    //update characterization
    if (Characterizer.isRunning())
      Characterizer.update();

    //stop compressor
    if (!RobotState.isSimulation())
      Compressor.stop();

    //update odometry
    Odometry.update();
  }
}
