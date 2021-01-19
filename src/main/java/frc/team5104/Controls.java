/*BreakerBots Robotics Team 2020*/
package frc.team5104;

import frc.team5104.util.BezierCurve;
import frc.team5104.util.Deadband;
import frc.team5104.util.XboxController;
import frc.team5104.util.XboxController.Axis;
import frc.team5104.util.XboxController.Button;

/** All the controls for the robot */
public class Controls {
	public enum Driver { ANNEKA, LIAM }
	public static Driver currentDriver = Driver.LIAM;
	public static XboxController driver = XboxController.create(0);
	public static XboxController operator = XboxController.create(1);
	
	//Main
	public static final Button IDLE = XboxController.getButton(Button.LIST, operator, driver);
	public static final Button COMPRESSOR_TOGGLE = XboxController.getButton(Button.MENU, operator, driver);
	
	//Drive
	public static final BezierCurve liamTurnCurve = new BezierCurve(.8, .32, .75, .27),
									annekaTurnCurve = new BezierCurve(.8, .32, .75, .27);
	public static final Axis DRIVE_TURN = driver.getAxis(Axis.LEFT_JOYSTICK_X, new Deadband(0.08), 
		currentDriver == Driver.LIAM ? liamTurnCurve : annekaTurnCurve
	);
	public static final Axis DRIVE_FORWARD = driver.getAxis(Axis.RIGHT_TRIGGER, new Deadband(0.01));
	public static final Axis DRIVE_REVERSE = driver.getAxis(Axis.LEFT_TRIGGER, new Deadband(0.01));
	public static final Button DRIVE_KICKSTAND = driver.getButton(Button.LEFT_JOYSTICK_PRESS);
	
	//Intake
	public static final Button INTAKE = XboxController.getButton(Button.X, operator, driver);
	
	//Shooter
	public static final Button SHOOT = XboxController.getButton(Button.B, operator, driver);
	public static final Button CHARGE_FLYWHEEL = XboxController.getButton(Button.RIGHT_BUMPER, operator, driver);
	public static final Button SHOOT_LOW = XboxController.getButton(Button.DIRECTION_PAD_LEFT, operator, driver);
	public static final Button SHOOT_HIGH = XboxController.getButton(Button.DIRECTION_PAD_RIGHT, operator, driver);
	
	//Panel
	public static final Button PANEL_DEPLOY = XboxController.getButton(Button.Y, operator, driver);
	public static final Button PANEL_SPIN = XboxController.getButton(Button.A, operator, driver);
	public static final Button PANEL_ROTATION = XboxController.getButton(Button.DIRECTION_PAD_UP, operator, driver);
	public static final Button PANEL_POSITION = XboxController.getButton(Button.DIRECTION_PAD_DOWN, operator, driver);
	
	//Climb
	public static final Button CLIMBER_DEPLOY = XboxController.getButton(Button.RIGHT_JOYSTICK_PRESS, operator, driver);
	public static final Axis CLIMBER_WINCH = driver.getAxis(Axis.RIGHT_JOYSTICK_Y, new Deadband(0.08));
	public static final Axis CLIMBER_WINCH_OP = operator.getAxis(Axis.RIGHT_JOYSTICK_Y, new Deadband(0.08));
}
