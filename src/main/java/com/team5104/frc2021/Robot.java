/*BreakerBots Robotics Team 2020*/
package com.team5104.frc2021;

import com.team5104.frc2021.auto.paths.*;
import com.team5104.frc2021.subsystems.*;
import com.team5104.frc2021.teleop.DriveController;
import com.team5104.frc2021.teleop.SuperstructureController;
import com.team5104.lib.Compressor;
import com.team5104.lib.auto.AutoManager;
import com.team5104.lib.auto.Odometry;
import com.team5104.lib.controller.XboxController;
import com.team5104.lib.dashboard.Dashboard;
import com.team5104.lib.devices.Limelight;
import com.team5104.lib.setup.RobotController;
import com.team5104.lib.setup.RobotState;
import com.team5104.lib.subsystem.SubsystemManager;
import com.team5104.lib.teleop.TeleopManager;

public class Robot extends RobotController.Robot {
  public Robot() {
    //Win
    win();

    //Subsystems
    SubsystemManager.attach(
      new Drive(),
      new Intake(),
      new Turret(),
      new Flywheel(),
      new Hopper(),
      new Hood(),
      new Climber()
//      new Paneler()
    );

    //Teleop Controllers
    TeleopManager.use(
      new DriveController(),
      new SuperstructureController()
    );

    //Superstructure
    Superstructure.reset();

    //Other
    Limelight.init(false);
    Odometry.init();
    AutoManager.setTargetPath(new ThreeBall_Right());
    Compressor.stop();
  }

  //Teleop
  public void teleopEnabled() {
    TeleopManager.enabled();
  }
  public void teleopDisabled() {
    TeleopManager.disabled();
  }
  public void teleopLoop() {
    TeleopManager.update();
  }

  //Autonomous
  public void autoEnabled() {
    AutoManager.enabled();
  }
  public void autoDisabled() {
    AutoManager.disabled();
  }
  public void autoLoop() {
    AutoManager.update();
  }

  //Test
  public void testLoop() {
    SubsystemManager.stop();
    Compressor.start();
  }

  //Main
  public void mainEnabled() {
    Compressor.stop();
    Superstructure.reset();
    SubsystemManager.reset();
  }
  public void mainDisabled() {
    Compressor.stop();
    Superstructure.reset();
    SubsystemManager.reset();
  }
  public void mainLoop() {
    Superstructure.update();
    SubsystemManager.update();
    XboxController.update();

    if (RobotState.isDisabled()) {
      Drive.zero();
    }
  }
  public void mainShutdown() {
    Dashboard.close();
  }
}
