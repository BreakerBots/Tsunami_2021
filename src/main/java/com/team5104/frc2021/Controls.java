/*BreakerBots Robotics Team 2020*/
package com.team5104.frc2021;

import com.team5104.lib.controller.*;
import com.team5104.lib.controller.Button.Slot;

/** All the controls for the robot */
public class Controls {
  public static XboxController driver = XboxController.create(0);
  public static XboxController operator = XboxController.create(1);
  public static XboxControllerGroup all = new XboxControllerGroup(driver, operator);

  //Main
  public static Button IDLE = all.button(Slot.LIST);
  public static Button COMPRESSOR_TOGGLE = all.button(Slot.MENU);

  //Drive
  public static Axis DRIVE_TURN = driver.axis(Axis.Slot.LEFT_JOYSTICK_X, new Deadband(0.08), new BezierCurve(.8, .32, .75, .27));
  public static Axis DRIVE_FORWARD = driver.axis(Axis.Slot.RIGHT_TRIGGER, new Deadband(0.01));
  public static Axis DRIVE_REVERSE = driver.axis(Axis.Slot.LEFT_TRIGGER, new Deadband(0.01));
  public static Button DRIVE_KICKSTAND = driver.button(Slot.LEFT_JOYSTICK_PRESS);

  //Intake
  public static Button INTAKE = all.button(Slot.X);

  //Shooter
  public static Button SHOOT = all.button(Slot.B);
  public static Button CHARGE_FLYWHEEL = all.button(Slot.RIGHT_BUMPER);
  public static Button SHOOT_LOW = all.button(Slot.DIRECTION_PAD_LEFT);
  public static Button SHOOT_HIGH = all.button(Slot.DIRECTION_PAD_RIGHT);

  //Panel
//  public static Button PANEL_DEPLOY = all.button(Slot.Y);
  public static Button PANEL_SPIN = all.button(Slot.A);
  public static Button PANEL_ROTATION = all.button(Slot.DIRECTION_PAD_UP);
//  public static Button PANEL_POSITION = all.button(Slot.DIRECTION_PAD_DOWN);

  //Climb
  public static Button CLIMBER_DEPLOY = all.button(Slot.RIGHT_JOYSTICK_PRESS);
  public static Axis CLIMBER_WINCH = all.axis(Axis.Slot.RIGHT_JOYSTICK_Y, new Deadband(0.08));
  public static Button BRAKE = all.button(Slot.DIRECTION_PAD_DOWN);
}
