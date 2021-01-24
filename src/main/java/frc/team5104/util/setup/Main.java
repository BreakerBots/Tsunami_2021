package frc.team5104.util.setup;

/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

import edu.wpi.first.hal.HALUtil;
import edu.wpi.first.wpilibj.RobotBase;
import frc.team5104.Simulation;

public final class Main {
	private Main() { 
		
	}
	
	public static void main(String... args) {
		if (HALUtil.getHALRuntimeType() == 0)
			RobotBase.startRobot(RobotController::new);
		else new Simulation();
	}
}