/*BreakerBots Robotics Team 2019*/
package frc.team5104;

import frc.team5104.util.Filer;

public class Constants {
	//Main
	public static final boolean OVERWRITE_NON_MATCH_LOGS = true;
	public static final boolean OVERWRITE_MATCH_LOGS = false;
	public static final int MAIN_LOOP_SPEED = 50;
	public static final boolean AT_COMP = true;
	public static final String ROBOT_NAME = Filer.readFile(Filer.HOME_PATH + "robot.txt");
	public static final boolean COMP_BOT = ROBOT_NAME.contains("Tsunami");
	public static final double LIMELIGHT_ANGLE = 50.0;
	public static final boolean LIMELIGHT_DEFAULT_OFF = true;
	public static double SUPERSTRUCTURE_TOL_SCALAR = 1;
	
	//Drive
	public static final double DRIVE_WHEEL_DIAMETER = 0.5; //ft
	public static final double DRIVE_TICKS_PER_REV = 2048.0 * (50.0/8.0) * (46.0/24.0);
	public static final double DRIVE_WHEEL_BASE_WIDTH = 2.0589795990198065; //ft
	public static final double DRIVE_KP = 1;//0.153;
	public static final double DRIVE_KD = 0;
	public static final double DRIVE_KS = COMP_BOT ? 0.162 : 0.151;
	public static final double DRIVE_KV = COMP_BOT ? 0.824*4 : 0.832*4;
	public static final double DRIVE_KA = COMP_BOT ? 0.0991 : 0.135;
	public static final double AUTO_MAX_VELOCITY = 6; //ft/s
	public static final double AUTO_MAX_ACCEL = 4;
	public static final double AUTO_CORRECTION_FACTOR = 1; //>0
	public static final double AUTO_DAMPENING_FACTOR  = 0.5; //0-1
	public static final boolean AUTO_PLOT_ODOMETRY = true;
	
	//Flywheel
	public static final boolean FLYWHEEL_OPEN_LOOP = false;
	public static final double FLYWHEEL_RAMP_RATE_UP = 0.7;
	public static final double FLYWHEEL_RAMP_RATE_DOWN = 3;
	public static double FLYWHEEL_RPM_TOL = 500;
	public static final double FLYWHEEL_TICKS_PER_REV = 2048.0 * (24.0 / 44.0);
	public static double FLYWHEEL_KP = COMP_BOT ? 0.2 : 1;//4;
	public static double FLYWHEEL_KD = COMP_BOT ? 0.01 : 0.2;//0.4;
	public static final double FLYWHEEL_KS = COMP_BOT ? 0.334 : 0.261;
	public static final double FLYWHEEL_KV = COMP_BOT ? 0.06 : 0.111;
	
	//Hood
	public static final double HOOD_TOL = 3;
	public static double HOOD_EQ_CONST = 0;
	public static final double HOOD_CALIBRATE_SPEED = 0.4;
	public static final double HOOD_TICKS_PER_REV = 4096.0 * (20.0 / 18.0) * (360.0 / 18.0);
	public static double HOOD_KD = 0.0;
	public static final double HOOD_KS = COMP_BOT ? .835 : .49;
	public static final double HOOD_KV = COMP_BOT ? .06 : .0605;
	public static final double HOOD_KA = COMP_BOT ? .00195 : .00218;
	public static final double HOOD_MAX_VEL = 200;
	public static final double HOOD_MAX_ACC = 2000;
	
	//Hopper
	public static final double HOPPER_START_INTAKE_SPEED = 7;
	public static final double HOPPER_START_INDEX_SPEED = 0.5;
	public static double HOPPER_INDEX_BALL_SIZE = Constants.COMP_BOT ? 2 : 2;
	public static double HOPPER_INDEX_TOL = 0.05;
	public static final double HOPPER_INDEX_TICKS_PER_REV = 2048 * (70.0/12.0);
	public static double HOPPER_INDEX_KP = 14;
	public static double HOPPER_INDEX_KI = 0.5;
	public static double HOPPER_INDEX_KD = 0.25;
	public static final double HOPPER_INDEX_KS = COMP_BOT ? 0.308 : 0.42;
	public static double HOPPER_FEED_SPEED = 6;
	
	//Intake
	public static final double INTAKE_SPEED = 1.0;
	public static final double INTAKE_FIRE_SPEED = 0;//0.25;
	public static final double INTAKE_REJECT_SPEED = 0;//.25;//0;
	
	//Paneler
	public static final double PANELER_ROT_SPEED = 0.15;
	public static final double PANELER_POS_SPEED = 0.0;
	public static final double PANELER_TICKS_PER_REV = 4096.0 * (/*control panel*/16.0 / 2);
	public static final double PANELER_ROTATIONS = 3;
	public static final int PANELER_BRAKE_INT = 25;
	
	//Turret
	public static final double TURRET_CALIBRATE_SPEED = 0.15;
	public static final double TURRET_VOLT_LIMIT = 6;
	public static final double TURRET_TICKS_PER_REV = 2048.0 / (8.0 / 60.0 /*gear*/) / (22.0 / 150.0 /*sprocket*/);
	public static double TURRET_KP = COMP_BOT ? 0.2 : 0.2;
	public static double TURRET_KD = COMP_BOT ? 0.01 : 0.0;
	public static final double TURRET_KS = COMP_BOT ? .2/*0.258*/ : 0.35;
	public static final double TURRET_KV = 0.015;
	public static final double TURRET_KA = COMP_BOT ? 0.000121 : 0.000384;
	public static final double TURRET_MAX_VEL = 200;
	public static final double TURRET_MAX_ACC = 2000;
	public static final double TURRET_VISION_TOL = 6;
	public static final double TURRET_SOFT_LEFT = COMP_BOT ? 120 : 110;
	public static final double TURRET_SOFT_RIGHT = COMP_BOT ? -120 : -120;
	public static final double TURRET_ZERO = COMP_BOT ? 140 : 130;
}