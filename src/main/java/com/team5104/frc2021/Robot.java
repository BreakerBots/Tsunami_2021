/*BreakerBots Robotics Team 2020*/
package com.team5104.frc2021;

import com.team5104.frc2021.subsystems.*;
import com.team5104.frc2021.teleop.DriveController;
import com.team5104.frc2021.teleop.SuperstructureController;
import com.team5104.lib.Compressor;
import com.team5104.lib.auto.AutoManager;
import com.team5104.lib.controller.XboxController;
import com.team5104.lib.devices.Limelight;
import com.team5104.lib.setup.RobotController;
import com.team5104.lib.subsystem.SubsystemManager;
import com.team5104.lib.teleop.TeleopControllerManager;

public class Robot extends RobotController.BreakerRobot {
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
      new Climber(),
      new Paneler()
    );

    //Teleop Controllers
    TeleopControllerManager.useTeleopControllers(
      new DriveController(),
      new SuperstructureController()
    );

    //Other
    Limelight.init(true);
    Compressor.stop();
    Superstructure.init();
  }

  //Teleop
  public void teleopStart() {
    TeleopControllerManager.enabled();
  }
  public void teleopStop() {
    TeleopControllerManager.disabled();
  }
  public void teleopLoop() {
    TeleopControllerManager.update();
  }

  //Autonomous
  public void autoStart() {
    AutoManager.enabled();
  }
  public void autoStop() {
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
  public void mainStart() {
    Superstructure.reset();
    SubsystemManager.reset();
    Compressor.stop();
  }
  public void mainStop() {
    Superstructure.reset();
    SubsystemManager.reset();
    Compressor.stop();
  }
  public void mainLoop() {
    Superstructure.update();
    SubsystemManager.update();
    XboxController.update();
  }
}
