/*BreakerBots Robotics Team 2019*/
package com.team5104.lib.setup;

import com.team5104.frc2021.Constants;
import com.team5104.frc2021.RobotSim;
import com.team5104.lib.Looper;
import com.team5104.lib.Looper.Crash;
import com.team5104.lib.Looper.Loop;
import com.team5104.lib.Looper.TimedLoop;
import com.team5104.lib.console;
import com.team5104.lib.console.Set;
import com.team5104.lib.dashboard.Dashboard;
import com.team5104.lib.setup.RobotState.RobotMode;
import edu.wpi.first.hal.FRCNetComm.tInstances;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.NotifierJNI;
import edu.wpi.first.wpilibj.RobotBase;

public class RobotController extends RobotBase {
  private final int notifier = NotifierJNI.initializeNotifier();
  private double expirationTime; //ms
  private Robot robot;

  //Init Robot
  public void startCompetition() {
    //Dashboard
    Dashboard.init();

    //Logging
    Set.create("RobotInit");
    if (Constants.robot.name.length() < 4)
      console.error("no robot.txt found! Use Filer.saveRobotFile() to specify the robot");
    console.log("initializing ", Constants.robot.name, " code...");

    //Set Child Class
    if (RobotState.isSimulation())
      robot = new RobotSim();
    else robot = new com.team5104.frc2021.Robot();

    //HAL
    HAL.report(tResourceType.kResourceType_Framework, tInstances.kFramework_Timed);
    HAL.observeUserProgramStarting();

    //Logging
    console.log("initialization took ", Set.getTime("RobotInit"), "seconds");

    //Fast Loop
    Looper.registerLoop(new TimedLoop("Fast", 9, 5));

    //Main Loop
    Looper.registerLoop(new Loop("Main", Thread.currentThread(), 5));
    expirationTime = RobotState.getFPGATimestamp() + RobotState.getLoopPeriod();
    NotifierJNI.setNotifierName(notifier, "Main");
    while (true) {
      NotifierJNI.updateNotifierAlarm(notifier, (long) (expirationTime * 1e3d));
      if (NotifierJNI.waitForNotifierAlarm(notifier) == 0)
        break;

      loop();

      expirationTime += RobotState.getLoopPeriod();
    }
  }
  public void endCompetition() {
    robot.mainShutdown();
    NotifierJNI.stopNotifier(notifier);
    NotifierJNI.cleanNotifier(notifier);
    Looper.killAll();
  }

  //Main Loop
  private void loop() {
    //Disabled
    if (isDisabled()) RobotState.setMode(RobotMode.DISABLED);

    //Enabled - Teleop/Test/Autonomous
    else if (isEnabled()) {
      //Test
      if (isTest()) RobotState.setMode(RobotMode.TEST);

      //Auto
      else if (isAutonomous()) RobotState.setMode(RobotMode.AUTONOMOUS);

      //Default to Teleop
      else RobotState.setMode(RobotMode.TELEOP);
    }

    //Handle Main Disabling
    try {
      if (RobotState.getLastMode() != RobotState.getMode()) {
        if (RobotState.getMode() == RobotMode.DISABLED) {
          robot.mainDisabled();
        }
        else if (RobotState.getLastMode() == RobotMode.DISABLED) {
          robot.mainEnabled();
        }
      }
    } catch (Exception e) { Looper.logCrash(new Crash(e)); }

    //Update Main Robot Loop
    try {
      robot.mainLoop();
    } catch (Exception e) { Looper.logCrash(new Crash(e)); }

    //Handle Modes
    switch(RobotState.getMode()) {
      case TELEOP: {
        try {
          //Teleop
          if (RobotState.getLastMode() != RobotState.getMode()) {
            console.log("Teleop Enabled");
            robot.teleopEnabled();
          }
          robot.teleopLoop();
          HAL.observeUserProgramTeleop();
        } catch (Exception e) { Looper.logCrash(new Crash(e)); }
        break;
      }
      case AUTONOMOUS: {
        try {
          //Auto
          if (RobotState.getLastMode() != RobotState.getMode()) {
            console.log("Auto Enabled");
            robot.autoEnabled();
          }
          robot.autoLoop();
          HAL.observeUserProgramTeleop();
        } catch (Exception e) { Looper.logCrash(new Crash(e)); }
        break;
      }
      case TEST: {
        try {
          //Test
          if (RobotState.getLastMode() != RobotState.getMode()) {
            console.log("Test Enabled");
            robot.testEnabled();
          }
          robot.testLoop();
          HAL.observeUserProgramTest();
        } catch (Exception e) { Looper.logCrash(new Crash(e)); }
        break;
      }
      case DISABLED: {
        try {
          //Disabled
          if (RobotState.getLastMode() != RobotState.getMode()) {
            switch (RobotState.getLastMode()) {
              case TELEOP: {
                robot.teleopDisabled();
                console.log("Teleop Disabled");
                break;
              }
              case AUTONOMOUS: {
                robot.autoDisabled();
                console.log("Autonomous Disabled");
                break;
              }
              case TEST: {
                robot.testDisabled();
                console.log("Test Disabled");
                break;
              }
              default: break;
            }
          }
          HAL.observeUserProgramDisabled();
        } catch (Exception e) { Looper.logCrash(new Crash(e)); }
        break;
      }
      default: break;
    }

    //LiveWindow.setEnabled(RobotState.isEnabled());
    //LiveWindow.updateValues();
    if (RobotState.isSimulation()) {
      HAL.simPeriodicBefore();
      HAL.simPeriodicAfter();
    }
    RobotState.setLastMode(RobotState.getMode());
  }

  //Child Class
  /** The main robot class
   * Override these methods to receive calls from the RobotController
   * Method Call Order:
   *  - Enabled:
   *   - mainEnabled() / mainDisabled()
   *   - mainLoop()
   *   - {mode}Enabled() / {mode}Disabled()
   *   - {mode}Loop()
   */
  public static abstract class Robot {
    /** Called at 50hz while the robot is enabled */
    public void mainLoop() { }
    /** Called once when the robot is enabled */
    public void mainEnabled() { }
    /** Called once when the robot is disabled */
    public void mainDisabled() { }
    /** Called when the robot code shuts down */
    public void mainShutdown() { }

    /** Called at 50hz while the robot is enabled in autonomous mode */
    public void autoLoop() { }
    /** Called once when the robot is enabled in autonomous mode */
    public void autoEnabled() { }
    /** Called once when the robot is disabled and was in autonomous mode */
    public void autoDisabled() { }

    /** Called at 50hz while the robot is enabled in teleop mode */
    public void teleopLoop() { }
    /** Called once when the robot is enabled in teleop mode */
    public void teleopEnabled() { }
    /** Called once when the robot is disabled and was in teleop mode */
    public void teleopDisabled() { }

    /** Called at 50hz while the robot is enabled in test mode */
    public void testLoop() { }
    /** Called once when the robot is enabled in test mode */
    public void testEnabled() { }
    /** Called once when the robot is disabled and was in test mode */
    public void testDisabled() { }

    /** TODO: Implement this plz */
    public void win() { }
  }
}
