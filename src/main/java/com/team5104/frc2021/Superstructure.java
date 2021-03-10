/*BreakerBots Robotics Team 2020*/
package com.team5104.frc2021;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.team5104.frc2021.subsystems.*;
import com.team5104.frc2021.subsystems.Flywheel.FlywheelState;
import com.team5104.lib.MovingAverage;
import com.team5104.lib.console;
import com.team5104.lib.devices.Limelight;
import com.team5104.lib.setup.RobotState;
import com.team5104.lib.setup.RobotState.RobotMode;

/** The Superstructure is a massive state machine for all subsystems, except drive. */
@JsonPropertyOrder({ "mode", "panelState", "target" })
public class Superstructure {
  public enum Mode {
    IDLE,
    INTAKE, AIMING, SHOOTING,
    CLIMBER_DEPLOYING, CLIMBING,
    PANEL_DEPLOYING, PANELING
  }
  private static Mode mode;
  private static MovingAverage readyToFire;

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
      Flywheel.state = FlywheelState.STOPPED;
      console.log("done shooting... idling");
    }

    //Start shooting after aiming
    readyToFire.update(
        is(Mode.AIMING) &&
        Flywheel.isSpedUp() &&
        Turret.onTarget() &&
        Hood.onTarget() &&
        Limelight.hasTarget()
    );
    if (is(Mode.AIMING) && readyToFire.getBooleanOutput()) {
      set(Mode.SHOOTING);
      console.log("finished aiming... shooting");
    }
  }

  //Update Mode
  public static void updateMode(Mode lastMode, Mode newMode) {
    //turn on/off limelight
    if (isFiringState(lastMode) != isFiringState(newMode)) {
      Limelight.setLEDMode(isFiringState(newMode));
    }

    if (isFiringState(newMode)) {
      Flywheel.state = FlywheelState.SPINNING;
    }
  }

  //Reset
  protected static void reset() {
    console.log("resetting");
    set(Mode.IDLE);
    if (readyToFire == null)
      readyToFire = new MovingAverage(15, false);
    readyToFire.reset();
  }

  //Enabled/Disabled
  public static boolean isEnabled() {
    return RobotState.isEnabled() && RobotState.getMode() != RobotMode.TEST;
  }
  public static boolean isDisabled() {
    return !isEnabled();
  }

  //Mode Getters/Setters
  public static boolean is(Mode mode) {
    return mode == Superstructure.mode;
  }
  @JsonGetter("mode")
  public Mode getMode() {
    return mode;
  }
  public static void set(Mode mode) {
    updateMode(Superstructure.mode, mode);
    Superstructure.mode = mode;
  }

  //Robot Specific
  public static boolean isClimbing() {
    return is(Mode.CLIMBER_DEPLOYING) || is(Mode.CLIMBING);
  }
  public static boolean isPaneling() {
    return is(Mode.PANEL_DEPLOYING) || is(Mode.PANELING);
  }
  public static boolean isFiringState(Mode mode) {
    return mode == Mode.AIMING || mode == Mode.SHOOTING;
  }
}
