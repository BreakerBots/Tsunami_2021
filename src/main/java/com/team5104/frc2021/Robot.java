/*BreakerBots Robotics Team 2020*/
package com.team5104.frc2021;

import com.team5104.frc2021.auto.paths.AutoNavBarrel;
import com.team5104.frc2021.auto.paths.ExamplePath;
import com.team5104.frc2021.auto.paths.AutoNavSlalom;
import com.team5104.frc2021.auto.paths.AutoNavBounce;
import com.team5104.frc2021.subsystems.*;
import com.team5104.frc2021.teleop.DriveController;
import com.team5104.frc2021.teleop.SuperstructureController;
import com.team5104.lib.Compressor;
import com.team5104.lib.auto.AutoManager;
import com.team5104.lib.auto.Odometry;
import com.team5104.lib.controller.XboxController;
import com.team5104.lib.devices.Limelight;
import com.team5104.lib.setup.RobotController;
import com.team5104.lib.subsystem.SubsystemManager;
import com.team5104.lib.teleop.TeleopManager;

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
    TeleopManager.use(
      new DriveController(),
      new SuperstructureController()
    );

    //Other
    Limelight.init(true);
    Odometry.init();
    Superstructure.init();
    AutoManager.setTargetPath(new AutoNavBounce());
    Compressor.stop();
    Drive.zero();
  }

  //Teleop
  public void teleopStart() {
    TeleopManager.enabled();
  }
  public void teleopStop() {
    TeleopManager.disabled();
  }
  public void teleopLoop() {
    TeleopManager.update();
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
