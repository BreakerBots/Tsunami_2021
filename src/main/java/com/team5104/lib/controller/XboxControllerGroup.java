package com.team5104.lib.controller;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.team5104.lib.controller.Axis.DoubleAxis;
import com.team5104.lib.controller.Button.DoubleButton;
import com.team5104.lib.controller.Rumble.DoubleRumble;

/** Groups two controllers together */
public class XboxControllerGroup {
  private XboxController controller1, controller2;

  public XboxControllerGroup(XboxController controller1, XboxController controller2) {
    this.controller1 = controller1;
    this.controller2 = controller2;
  }

  //Buttons
  public Button button(Button.Slot slot) {
    return new DoubleButton(
        controller1.button(slot),
        controller2.button(slot)
    );
  }

  public Button holdButton(Button.Slot slot) {
    return new DoubleButton(
        controller1.holdButton(slot),
        controller2.holdButton(slot)
    );
  }

  public Button holdTimeButton(Button.Slot slot, int holdTime, XboxController controller1, XboxController controller2) {
    return new DoubleButton(
        controller1.holdTimeButton(slot, holdTime),
        controller2.holdTimeButton(slot, holdTime)
    );
  }

  public Button doubleClickButton(Button.Slot slot, int killOffTime) {
    return new DoubleButton(
        controller1.doubleClickButton(slot, killOffTime),
        controller2.doubleClickButton(slot, killOffTime)
    );
  }

  //Axis'
  public Axis axis(Axis.Slot slot) {
    return new DoubleAxis(
        controller1.axis(slot),
        controller2.axis(slot)
    );
  }

  public Axis axis(Axis.Slot slot, Deadband deadband) {
    return new DoubleAxis(
        controller1.axis(slot, deadband),
        controller2.axis(slot, deadband)
    );
  }

  public Axis axis(Axis.Slot slot, Deadband deadband, BezierCurve curve) {
    return new DoubleAxis(
        controller1.axis(slot, deadband, curve),
        controller2.axis(slot, deadband, curve)
    );
  }

  public Axis axis(Axis.Slot slot, Deadband deadband, BezierCurve curve, boolean reversed) {
    return new DoubleAxis(
        controller1.axis(slot, deadband, curve, reversed),
        controller2.axis(slot, deadband, curve, reversed)
    );
  }

  //Rumble
  public Rumble rumble(double strength, boolean hard, int timeoutMs) {
    return new DoubleRumble(
        controller1.rumble(strength, hard, timeoutMs),
        controller2.rumble(strength, hard, timeoutMs)
    );
  }

  public Rumble rumble(double strength, boolean hard, int timeoutMs, int dipCount) {
    return new DoubleRumble(
        controller1.rumble(strength, hard, timeoutMs, dipCount),
        controller2.rumble(strength, hard, timeoutMs, dipCount)
    );
  }

  //Ports
  @JsonGetter("ports")
  public int[] getPorts() {
    return new int[] {
        controller1.port,
        controller2.port
    };
  }
}

