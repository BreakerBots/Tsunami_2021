/*BreakerBots Robotics Team 2020*/
package frc.team5104;

/** All port numbers on the robot */
public class Ports {
	//Drive
	public static final int DRIVE_MOTOR_L1 = 11;
	public static final int DRIVE_MOTOR_L2 = 12;
	public static final int DRIVE_MOTOR_R1 = 13;
	public static final int DRIVE_MOTOR_R2 = 14;
	public static final int DRIVE_GYRO = 27;
	
	//Hood
	public static final int HOOD_MOTOR = 15;
	
	//Flywheel
	public static final int FLYWHEEL_MOTOR_1 = 16;
	public static final int FLYWHEEL_MOTOR_2 = 17;
	
	//Turret
	public static final int TURRET_MOTOR = 18;
	
	//Intake
	public static final int INTAKE_MOTOR = 19;
	public static final int INTAKE_DEPLOYER_FORWARD = Constants.COMP_BOT ? 2 : 7;
	public static final int INTAKE_DEPLOYER_REVERSE = Constants.COMP_BOT ? 3 : 6;
	
	//Hopper
	public static final int HOPPER_START_MOTOR = 20;  
	public static final int HOPPER_MIDDLE_MOTOR = 21;
	public static final int HOPPER_FEEDER_MOTOR = 22; 
	public static final int HOPPER_SENSOR_START = Constants.COMP_BOT ? 0 : 1;
	public static final int HOPPER_SENSOR_END = Constants.COMP_BOT ? 1 : 0;
					
	//Climber
	public static final int CLIMBER_MOTOR = 23;
	public static final int CLIMBER_DEPLOYER_FORWARD = 0;
	public static final int CLIMBER_DEPLOYER_REVERSE = 1;
	public static final int CLIMBER_BRAKE_FORWARD = 6;
	public static final int CLIMBER_BRAKE_REVERSE = 7;
	
	//Paneler
	public static final int PANELER_MOTOR = 26;
	public static final int PANELER_DEPLOYER_FORWARD = Constants.COMP_BOT ? 4 : 3;
	public static final int PANELER_DEPLOYER_REVERSE = Constants.COMP_BOT ? 5 : 2;
}