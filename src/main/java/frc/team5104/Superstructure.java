/*BreakerBots Robotics Team 2020*/
package frc.team5104;

import frc.team5104.subsystems.*;
import frc.team5104.util.*;
import frc.team5104.util.LatchedBoolean.LatchedBooleanMode;
import frc.team5104.util.Limelight.LEDMode;
import frc.team5104.util.console.c;

/** 
 * The Superstructure is a massive state machine that handles the Panel, Climb, and of course, Ballz
 * The Superstructure only controls the states... its up the subsystems to figure out what to do
 * based on the state of the Superstructure.
 */
public class Superstructure {
	//States and Variables
	public enum Mode {
		IDLE,
		INTAKE, AIMING, SHOOTING,
		CLIMBER_DEPLOYING, CLIMBING,
		PANEL_DEPLOYING, PANELING
	};
	public enum PanelState { ROTATION, POSITION };
	public enum FlywheelState { STOPPED, SPINNING };
	public enum Target { LOW, HIGH };
	
	private static Mode mode = Mode.IDLE;
	private static PanelState panelState = PanelState.ROTATION;
	private static FlywheelState shooterWheelState = FlywheelState.STOPPED;
	private static Target target = Target.HIGH;
	private static boolean isEnabled;
	private static long systemStateStart = System.currentTimeMillis();
	private static LatchedBoolean flywheelOnTarget = new LatchedBoolean(LatchedBooleanMode.RISING), 
								  hoodOnTarget = new LatchedBoolean(LatchedBooleanMode.RISING), 
								  turretOnTarget = new LatchedBoolean(LatchedBooleanMode.RISING),
								  limelightOn = new LatchedBoolean();
	private static MovingAverage readyToFire = new MovingAverage(15, false);
	
	//External Functions
	public static long getTimeInSystemState() { return System.currentTimeMillis() - systemStateStart; }
	public static Mode getMode() { return mode; }
	public static void setMode(Mode mode) { Superstructure.mode = mode; }
	public static boolean isEnabled() { return isEnabled; }
	public static boolean isDisabled() { return !isEnabled; }
	public static void enable() { isEnabled = true; }
	public static void disable() { isEnabled = false; }

	public static void setPanelState(PanelState panelState) { Superstructure.panelState = panelState; }
	public static PanelState getPanelState() { return panelState; }
	public static void setFlywheelState(FlywheelState shooterWheelState) { Superstructure.shooterWheelState = shooterWheelState; }
	public static FlywheelState getFlywheelState() { return shooterWheelState; }
	public static void setTarget(Target target) { Superstructure.target = target; }
	public static Target getTarget() { return target; }
	public static boolean isClimbing() { return getMode() == Mode.CLIMBER_DEPLOYING || getMode() == Mode.CLIMBING; }
	public static boolean isPaneling() { return getMode() == Mode.PANEL_DEPLOYING || getMode() == Mode.PANELING; }
	
	//Loop
	protected static void update() {
		//Competition Debugging
		if (Constants.AT_COMP) {
			Constants.SUPERSTRUCTURE_TOL_SCALAR = Tuner.getTunerInputDouble("Superstructure Tolerance Scalar", Constants.SUPERSTRUCTURE_TOL_SCALAR);
		}
		
		//Exit Paneling
		if (Superstructure.getMode() == Mode.PANELING && Paneler.isFinished()) {
			Superstructure.setMode(Mode.IDLE);
			console.log(c.SUPERSTRUCTURE, "finished paneling... idling");
		}
		
		//Exit Intake
		if (getMode() == Mode.INTAKE && Hopper.isFull()) {
			setMode(Mode.IDLE);
			console.log(c.SUPERSTRUCTURE, "hopper full... idling");
		}
		
		//Exit Shooting
		if (getMode() == Mode.SHOOTING && !Hopper.isFullAverage() && Hopper.hasFedAverage()) {
			setMode(Mode.IDLE);
			setFlywheelState(FlywheelState.STOPPED);
			console.log(c.SUPERSTRUCTURE, "done shooting... idling");
		}
		
		//Start Shooting after done Aiming
		if (flywheelOnTarget.get(Flywheel.isSpedUp()) && getMode() == Mode.AIMING)
			console.log(c.FLYWHEEL, "sped up");
		if (hoodOnTarget.get(Hood.onTarget()) && getMode() == Mode.AIMING)
			console.log(c.HOOD, "on target");
		if (turretOnTarget.get(Turret.onTarget()) && getMode() == Mode.AIMING)
			console.log(c.TURRET, "on target");
		readyToFire.update(getMode() == Mode.AIMING && Flywheel.isSpedUp() && Turret.onTarget() && Hood.onTarget() && Limelight.hasTarget());
		if (getMode() == Mode.AIMING && readyToFire.getBooleanOutput()) {
			setMode(Mode.SHOOTING);
			console.log(c.SUPERSTRUCTURE, "finished aiming... shooting");
		}
		
		//Spin Flywheel while Shooting
		if (getMode() == Mode.SHOOTING || getMode() == Mode.AIMING) {
			setFlywheelState(FlywheelState.SPINNING);
		}
		
		//Limelight
		if (limelightOn.get(getMode() == Mode.AIMING || getMode() == Mode.SHOOTING)) {
			if (getMode() == Mode.AIMING || getMode() == Mode.SHOOTING)
				Limelight.setLEDMode(LEDMode.ON);
			else if (Constants.LIMELIGHT_DEFAULT_OFF)
				Limelight.setLEDMode(LEDMode.OFF);
		}
	}
	
	//Reset
	protected static void reset() {
		console.log(c.SUPERSTRUCTURE, "Resetting Superstructure!");
		setMode(Mode.IDLE);
		setPanelState(PanelState.ROTATION);
		setFlywheelState(FlywheelState.STOPPED);
		setTarget(Target.HIGH);
		readyToFire.reset();
	}
}