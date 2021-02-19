/*BreakerBots Robotics Team 2020*/
package com.team5104.frc2021;

import com.team5104.lib.XboxController;
import com.team5104.lib.XboxController.*;

/** All the controls for the robot */
public class Controls {
  public static XboxController driver = XboxController.create(0);
  public static XboxController operator = XboxController.create(1);
  public static XboxControllerGroup all = new XboxControllerGroup(driver, operator);

  // Main
  public static Button IDLE = all.button(Button.LIST);
  public static Button COMPRESSOR_TOGGLE = all.button(Button.MENU);

  // Drive
  public static Axis DRIVE_TURN =
      driver.axis(Axis.LEFT_JOYSTICK_X, new Deadband(0.08), new BezierCurve(.8, .32, .75, .27));
  public static Axis DRIVE_FORWARD = driver.axis(Axis.RIGHT_TRIGGER, new Deadband(0.01));
  public static Axis DRIVE_REVERSE = driver.axis(Axis.LEFT_TRIGGER, new Deadband(0.01));
  public static Button DRIVE_KICKSTAND = driver.button(Button.LEFT_JOYSTICK_PRESS);

  // Intake
  public static Button INTAKE = all.button(Button.X);

  // Shooter
  public static Button SHOOT = all.button(Button.B);
  public static Button CHARGE_FLYWHEEL = all.button(Button.RIGHT_BUMPER);
  public static Button SHOOT_LOW = all.button(Button.DIRECTION_PAD_LEFT);
  public static Button SHOOT_HIGH = all.button(Button.DIRECTION_PAD_RIGHT);

  // Panel
  public static Button PANEL_DEPLOY = all.button(Button.Y);
  public static Button PANEL_SPIN = all.button(Button.A);
  public static Button PANEL_ROTATION = all.button(Button.DIRECTION_PAD_UP);
  public static Button PANEL_POSITION = all.button(Button.DIRECTION_PAD_DOWN);

  // Climb
  public static Button CLIMBER_DEPLOY = all.button(Button.RIGHT_JOYSTICK_PRESS);
  public static Axis CLIMBER_WINCH = operator.axis(Axis.RIGHT_JOYSTICK_Y, new Deadband(0.08));
}
