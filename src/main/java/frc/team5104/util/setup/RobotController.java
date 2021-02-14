/*BreakerBots Robotics Team 2019*/
package frc.team5104.util.setup;

import edu.wpi.first.hal.FRCNetComm.tInstances;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.NotifierJNI;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import frc.team5104.Constants;
import frc.team5104.Robot;
import frc.team5104.RobotSim;
import frc.team5104.util.Looper;
import frc.team5104.util.Looper.Crash;
import frc.team5104.util.Looper.Loop;
import frc.team5104.util.Looper.TimedLoop;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.console.t;
import frc.team5104.util.setup.RobotState.RobotMode;

public class RobotController extends RobotBase {
	private final int notifier = NotifierJNI.initializeNotifier();
	private double expirationTime; //ms
	private BreakerRobot robot;

	//Init Robot
	public void startCompetition() {
		//Logging
		console.init();
		if (!RobotState.isSimulation())
			console.logFile.start();
		console.sets.create("RobotInit");
		if (Constants.config.robotName.length() < 4)
			console.error("Please deploy robot.txt with the correct robot name!");
		console.log(c.MAIN, t.INFO, "Initializing " + Constants.config.robotName + " Code...");

		//Set Child Class
		if (RobotState.isSimulation())
			robot = new RobotSim();
		else robot = new Robot();

		//HAL
		HAL.report(tResourceType.kResourceType_Framework, tInstances.kFramework_Timed);
		HAL.observeUserProgramStarting();

		//Logging
		console.sets.log(c.MAIN, t.INFO, "RobotInit", "Initialization took ");

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
					if (!RobotState.isSimulation())
						console.logFile.end();
					robot.mainStop();
				}
				else if (RobotState.getLastMode() == RobotMode.DISABLED) {
					if (!RobotState.isSimulation()) {
						console.logFile.end();
						console.logFile.start();
					}
					robot.mainStart();
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
						console.log(c.MAIN, t.INFO, "Teleop Enabled");
						robot.teleopStart();
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
						console.log(c.MAIN, t.INFO, "Auto Enabled");
						robot.autoStart();
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
						console.log(c.MAIN, t.INFO, "Test Enabled");
						robot.testStart();
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
								robot.teleopStop(); 
								console.log(c.MAIN, t.INFO, "Teleop Disabled"); 
								break;
							}
							case AUTONOMOUS: {
								robot.autoStop(); 
								console.log(c.MAIN, t.INFO, "Autonomous Disabled"); 
								break;
							}
							case TEST: { 
								robot.testStop(); 
								console.log(c.MAIN, t.INFO, "Test Disabled"); 
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

		LiveWindow.setEnabled(RobotState.isEnabled());
		LiveWindow.updateValues();
		if (RobotState.isSimulation()) {
			HAL.simPeriodicBefore();
			HAL.simPeriodicAfter();
		}
		RobotState.setLastMode(RobotState.getMode());
	}

	//Child Class
	/**
	 * The Main Robot Interface. Called by this, Breaker Robot Controller
	 * <br>Override these methods to run code
	 * <br>Functions Call Order:
	 * <br> - All Enable/Disable Functions are called before the corresponding loop function
	 * <br> - Main Functions are called last (teleop, test, auto are before)
	 */
	public static abstract class BreakerRobot {
		public void mainLoop() { }
		public void mainStart() { }
		public void mainStop() { }
		public void autoLoop() { }
		public void autoStart() { }
		public void autoStop() { }
		public void teleopLoop() { }
		public void teleopStart() { }
		public void teleopStop() { }
		public void testLoop() { }
		public void testStart() { }
		public void testStop() { }
		public void win() {
			/* TODO: Implement this plz */
		}
	}
}