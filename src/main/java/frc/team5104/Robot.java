/*BreakerBots Robotics Team 2020*/
package frc.team5104;

import frc.team5104.Superstructure.SystemState;
import frc.team5104.auto.AutoManager;
import frc.team5104.auto.Odometry;
import frc.team5104.auto.paths.SixBall_Right;
import frc.team5104.subsystems.*;
import frc.team5104.teleop.CompressorController;
import frc.team5104.teleop.DriveController;
import frc.team5104.teleop.SuperstructureController;
import frc.team5104.util.Limelight;
import frc.team5104.util.Plotter;
import frc.team5104.util.Webapp;
import frc.team5104.util.XboxController;
import frc.team5104.util.console;
import frc.team5104.util.managers.SubsystemManager;
import frc.team5104.util.managers.TeleopControllerManager;
import frc.team5104.util.setup.RobotController;
import frc.team5104.util.setup.RobotState;
import frc.team5104.util.setup.RobotState.RobotMode;

public class Robot extends RobotController.BreakerRobot {
	public Robot() {
		console.logFile.start();

		//Win
		this.win();
		
		//Managers
		SubsystemManager.useSubsystems(
			new Drive(),
			new Intake(),
			new Turret(),
			new Flywheel(),
			new Hopper(),
			new Hood(),
			new Climber(),
			new Paneler()
		);
		TeleopControllerManager.useTeleopControllers(
			new DriveController(),
			new SuperstructureController(),
			new CompressorController()
		);
		
		//Other Initialization
		Webapp.run();
		Plotter.reset();
		Odometry.init();
		Limelight.init();
		CompressorController.stop();
		AutoManager.setTargetPath(new SixBall_Right());
		SubsystemManager.debug();
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
		Superstructure.setSystemState(SystemState.DISABLED);
		Drive.stop();
		CompressorController.start(); 
	}
	
	//Main
	public void mainStart() {
		Superstructure.reset();
		SubsystemManager.reset();
		CompressorController.stop();
	}
	public void mainStop() {
		Superstructure.reset();
		SubsystemManager.reset();
		CompressorController.stop();
		console.logFile.end();
	}
	public void mainLoop() {
		Superstructure.update();
		SubsystemManager.update();
		XboxController.update();
		
		if (RobotState.getMode() == RobotMode.DISABLED) {
			Drive.resetEncoders();
			Drive.resetGyro();
			Odometry.resetWithoutWaiting();
		}
	}
}