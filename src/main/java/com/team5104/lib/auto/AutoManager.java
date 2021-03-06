/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib.auto;

import com.team5104.lib.Compressor;
import com.team5104.lib.CrashHandler;
import com.team5104.lib.Looper;
import com.team5104.lib.Looper.Loop;
import com.team5104.lib.console;
import com.team5104.lib.dashboard.DashboardTrajectory;
import com.team5104.lib.subsystem.Characterizer;

/** manages the running of an autonomous path and characterizing */
public class AutoManager {
  private static AutoPath targetPath;
  private static Thread pathThread;
  public static boolean pathThreadInterrupted;

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
          console.log("running ", targetPath.getClass().getSimpleName());
          DashboardTrajectory.alertStartingPath();
          targetPath.setRunning(true);
          targetPath.start();
          targetPath.setRunning(false);

          if (pathThreadInterrupted)
            console.log(targetPath.getClass().getSimpleName(), " exited early");
          else console.log(targetPath.getClass().getSimpleName(), " finished");
        } catch (Exception e) { CrashHandler.log(e); }
      });
      pathThreadInterrupted = false;
      pathThread.start();
      Looper.registerLoop(new Loop("Auto", pathThread, 8));
    }
  }
  public static void disabled() {
    if (pathThread != null) {
      pathThread.interrupt();
      pathThread = null;
      pathThreadInterrupted = true;
    }
    if (Characterizer.isRunning())
      Characterizer.disabled();
  }

  //Init
  public static void setTargetPath(AutoPath path) {
    setTargetPath(path, false);
  }
  public static void setTargetPath(AutoPath path, boolean dontSend) {
    console.log("target path is now ", path.getClass().getSimpleName());
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
    //update characterization
    if (Characterizer.isRunning()) {
      Characterizer.update();
    }

    //update auto
    else {
      if (targetPath != null) {
        //update path
        targetPath.update();
      }

      //update odometry
      Odometry.update();
    }

    //stop compressor
    Compressor.stop();
  }
}
