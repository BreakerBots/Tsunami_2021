package com.team5104.lib.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.DriverStation;

/** A class that stores and manages a axis for XboxController.java */
public class Axis {
  public enum Slot {
    LEFT_JOYSTICK_X(0),
    LEFT_JOYSTICK_Y(1),
    LEFT_TRIGGER(2),
    RIGHT_TRIGGER(3),
    RIGHT_JOYSTICK_X(4),
    RIGHT_JOYSTICK_Y(5),
    NONE(0);
    int port;
    private Slot(int port) {
      this.port = port;
    }
  }

  private final int controllerPort;
  @JsonProperty("slot")
  private final Axis.Slot slot;
  @JsonProperty("reversed")
  public boolean reversed;
  @JsonProperty("deadband")
  public Deadband deadband;
  @JsonProperty("curve")
  public BezierCurve curve;

  Axis(int controllerPort, Axis.Slot slot, Deadband deadband, BezierCurve curve, boolean reversed) {
    this.controllerPort = controllerPort;
    this.slot = slot;
    this.deadband = deadband == null ? new Deadband() : deadband;
    this.curve = curve;
    this.reversed = reversed;
  }

  //Reads
  public double get() {
    double val = deadband.get(getRawAxisValue()) * (reversed ? -1 : 1);
    if (curve != null)
      val = curve.getPoint(val);
    return val;
  }

  private double getRawAxisValue() {
    if (XboxController.isConnected(controllerPort))
      return DriverStation.getInstance().getStickAxis(controllerPort, slot.port);
    return 0;
  }

  //Settings
  public void changeCurveX1(double x1) {
    if (curve != null)
      curve.x1 = x1;
  }

  public static class DoubleAxis extends Axis {
    @JsonProperty("child1")
    private final Axis axis1;
    @JsonProperty("child2")
    private final Axis axis2;

    public DoubleAxis(Axis axis1, Axis axis2) {
        super(0, Axis.Slot.NONE, null, null, false);
        this.axis1 = axis1;
        this.axis2 = axis2;
    }

    public double get() {
          return axis1.get() + axis2.get();
      }
  }
}

