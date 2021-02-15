/*BreakerBots Robotics Team 2020*/
package frc.team5104.frc2021;

import frc.team5104.frc2021.subsystems.*;
import frc.team5104.frc2021.teleop.CompressorController;
import frc.team5104.frc2021.teleop.DriveController;
import frc.team5104.frc2021.teleop.SuperstructureController;
import frc.team5104.lib.XboxController;
import frc.team5104.lib.auto.AutoManager;
import frc.team5104.lib.auto.Odometry;
import frc.team5104.lib.console;
import frc.team5104.lib.managers.SubsystemManager;
import frc.team5104.lib.managers.TeleopControllerManager;
import frc.team5104.lib.sensors.Limelight;
import frc.team5104.lib.setup.RobotController;
import frc.team5104.lib.webapp.Plotter;
import frc.team5104.lib.webapp.Webapp;

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
		SubsystemManager.debug( );
		TeleopControllerManager.useTeleopControllers(
			new DriveController(),
			new SuperstructureController(),
			new CompressorController()
		);
		
		//Other Initialization
		Webapp.run();
		Plotter.reset();
		Odometry.init();
		Limelight.init(true);
		CompressorController.stop();
		//AutoManager.setTargetPath(new ExamplePath());
		//AutoManager.enabledPlotting();
		AutoManager.characterize(Drive.class);
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
		Superstructure.enable();
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
		Superstructure.enable();
		AutoManager.update();
	}
	
	//Test
	public void testLoop() {
		Drive.stop();
		Superstructure.disable();
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
	}
}