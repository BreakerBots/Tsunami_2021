package frc.team5104.util.setup;

/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

import edu.wpi.first.wpilibj.RobotBase;

public final class Main {
	private Main() { 
		
	}
	
	public static void main(String... args) {
		RobotBase.startRobot(RobotController::new);
	}
}