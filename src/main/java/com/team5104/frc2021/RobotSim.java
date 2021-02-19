package com.team5104.frc2021;

import com.team5104.frc2021.auto.paths.AutoNavSlalom;
import com.team5104.frc2021.subsystems.Drive;
import com.team5104.lib.auto.AutoManager;
import com.team5104.lib.auto.Odometry;
import com.team5104.lib.dashboard.Dashboard;
import com.team5104.lib.managers.SubsystemManager;
import com.team5104.lib.setup.RobotController;
import com.team5104.lib.webapp.Plotter;
import com.team5104.lib.webapp.Webapp;

public class RobotSim extends RobotController.BreakerRobot {
  public RobotSim() {
    // Win
    this.win();

    // Managers
    SubsystemManager.useSubsystems(new Drive());

    // Other Initialization
    // Dashboard.run();
    Webapp.run();
    Plotter.reset();
    Odometry.init();
    AutoManager.setTargetPath(new AutoNavSlalom());
    // AutoManager.runTrajectoryTester();
    AutoManager.enabledPlotting();
    AutoManager.characterize(Drive.class);
  }

  // Autonomous
  public void autoStart() {
    AutoManager.enabled();
  }

  public void autoStop() {
    AutoManager.disabled();
  }

  public void autoLoop() {
    AutoManager.update();
  }

  // Main
  public void mainStart() {
    SubsystemManager.reset();
  }

  public void mainStop() {
    SubsystemManager.reset();
  }

  public void mainLoop() {
    SubsystemManager.update();
  }

  public void mainShutdown() {
    Dashboard.close();
  }
}
