/*BreakerBots Robotics Team 2020*/
package com.team5104.frc2021;

import com.team5104.frc2021.subsystems.*;
import com.team5104.lib.LatchedBoolean;
import com.team5104.lib.LatchedBoolean.LatchedBooleanMode;
import com.team5104.lib.MovingAverage;
import com.team5104.lib.console;
import com.team5104.lib.devices.Limelight;
import com.team5104.lib.devices.Limelight.LEDMode;

/** The Superstructure is a massive state machine for all subsystems, except drive. */
public class Superstructure {
  //States and Variables
  public enum Mode {
    IDLE,
    INTAKE, AIMING, SHOOTING,
    CLIMBER_DEPLOYING, CLIMBING,
    PANEL_DEPLOYING, PANELING
  }
  public enum PanelState { ROTATION, POSITION }
  public enum FlywheelState { STOPPED, SPINNING }
  public enum Target { LOW, HIGH }

  private static Mode mode = Mode.IDLE;
  private static PanelState panelState = PanelState.ROTATION;
  private static FlywheelState flywheelState = FlywheelState.STOPPED;
  private static Target target = Target.HIGH;
  private static boolean isEnabled;
  private static LatchedBoolean flywheelOnTarget = new LatchedBoolean(LatchedBooleanMode.RISING), hoodOnTarget = new LatchedBoolean(LatchedBooleanMode.RISING),
                  turretOnTarget = new LatchedBoolean(LatchedBooleanMode.RISING), limelightOn = new LatchedBoolean();
  private static MovingAverage readyToFire = new MovingAverage(15, false);

  //External Functions
  public static boolean is(Mode mode) { return mode == Superstructure.mode; }
  public static void set(Mode mode) { Superstructure.mode = mode; }
  public static boolean isEnabled() { return isEnabled; }
  public static boolean isDisabled() { return !isEnabled; }
  public static void enable() { isEnabled = true; }
  public static void disable() { isEnabled = false; }

  public static void set(PanelState panelState) { Superstructure.panelState = panelState; }
  public static boolean is(PanelState panelState) { return panelState == Superstructure.panelState; }
  public static void set(FlywheelState shooterWheelState) { Superstructure.flywheelState = shooterWheelState; }
  public static boolean is(FlywheelState flywheelState) { return flywheelState == Superstructure.flywheelState; }
  public static void set(Target target) { Superstructure.target = target; }
  public static boolean is(Target target) { return target == Superstructure.target; }
  public static boolean isClimbing() { return is(Mode.CLIMBER_DEPLOYING) || is(Mode.CLIMBING); }
  public static boolean isPaneling() { return is(Mode.PANEL_DEPLOYING) || is(Mode.PANELING); }

  //Loop
  protected static void update() {
    //Exit Paneling
    if (Superstructure.is(Mode.PANELING) && Paneler.isFinished()) {
      Superstructure.set(Mode.IDLE);
      console.log("finished paneling... idling");
    }

    //Exit Intake
    if (is(Mode.INTAKE) && Hopper.isFull()) {
      set(Mode.IDLE);
      console.log("hopper full... idling");
    }

    //Exit Shooting
    if (is(Mode.SHOOTING) && !Hopper.isFullAverage() && Hopper.hasFedAverage()) {
      set(Mode.IDLE);
      set(FlywheelState.STOPPED);
      console.log("done shooting... idling");
    }

    //Start Shooting after done Aiming
    if (flywheelOnTarget.get(Flywheel.isSpedUp()) && is(Mode.AIMING))
      console.log("sped up");
    if (hoodOnTarget.get(Hood.onTarget()) && is(Mode.AIMING))
      console.log("on target");
    if (turretOnTarget.get(Turret.onTarget()) && is(Mode.AIMING))
      console.log("on target");
    readyToFire.update(is(Mode.AIMING) && Flywheel.isSpedUp() && Turret.onTarget() && Hood.onTarget() && Limelight.hasTarget());
    if (is(Mode.AIMING) && readyToFire.getBooleanOutput()) {
      set(Mode.SHOOTING);
      console.log("finished aiming... shooting");
    }

    //Spin Flywheel while Shooting
    if (is(Mode.SHOOTING) || is(Mode.AIMING)) {
      set(FlywheelState.SPINNING);
    }

    //Limelight
    if (limelightOn.get(is(Mode.AIMING) || is(Mode.SHOOTING))) {
      if (is(Mode.AIMING) || is(Mode.SHOOTING))
        Limelight.setLEDMode(LEDMode.ON);
      else if (Limelight.defaultOff)
        Limelight.setLEDMode(LEDMode.OFF);
    }
  }

  //Reset
  protected static void reset() {
    console.log("resetting!");
    set(Mode.IDLE);
    set(PanelState.ROTATION);
    set(FlywheelState.STOPPED);
    set(Target.HIGH);
    readyToFire.reset();
  }

  //Init
  protected static void init() {
    console.log("initializing!");
    reset();
  }
}
