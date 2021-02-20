package com.team5104.lib.devices;

import com.ctre.phoenix.motorcontrol.Faults;

public class Health {
  public enum Status {
    GOOD(2),
    FAIR(1), //something went wrong but can run
    BAD(0); //cannot run at all
    final int val;
    private Status(int val) { this.val = val; }
    static Status from(int val) { return val == Status.GOOD.val ? Status.GOOD : (val == Status.FAIR.val ? Status.FAIR : Status.BAD); }
  }
  private final Status status;
  private final String error;

  //Constructors
  public Health(Status status) {
    this(status, null);
  }
  public Health(Faults faults) {
    if (!faults.hasAnyFault()) {
      this.status = Status.GOOD;
      this.error = null;
      return;
    }
    StringBuilder work = new StringBuilder();
    if (faults.UnderVoltage)
      work.append("UnderVoltage");
    if (faults.ForwardLimitSwitch)
      work.append("ForwardLimitSwitch");
    if (faults.ReverseLimitSwitch)
      work.append("ReverseLimitSwitch");
    if (faults.ForwardSoftLimit)
      work.append("ForwardSoftLimit");
    if (faults.ReverseSoftLimit)
      work.append("ReverseSoftLimit");
    if (faults.HardwareFailure)
      work.append("HardwareFailure");
    if (faults.ResetDuringEn)
      work.append("ResetDuringEn");
    if (faults.SensorOverflow)
      work.append("SensorOverflow");
    if (faults.SensorOutOfPhase)
      work.append("SensorOutOfPhase");
    if (faults.HardwareESDReset)
      work.append("HardwareESDReset");
    if (faults. RemoteLossOfSignal)
      work.append("RemoteLossOfSignal");
    if (faults.APIError)
      work.append("APIError");
    if (faults.SupplyOverV)
      work.append("SupplyOverV");
    if (faults.SupplyUnstable)
      work.append("SupplyUnstable");

    this.status = Status.FAIR;
    this.error = work.toString();
  }
  public Health(Status status, String error) {
    this.status = status;
    this.error = error;
  }

  //Getters
  public Status getStatus() {
    return status;
  }
  public String getError() {
    return error;
  }
  public static Health merge(Health[] healths) {
    int worstVal = Status.GOOD.val;
    String worstError = null;
    for (Health health : healths) {
      if (health.getStatus().val < worstVal) {
        worstVal = health.getStatus().val;
        worstError = health.getError();
      }
    }
    return new Health(Status.from(worstVal), worstError);
  }
  public static Health merge(Device[] devices) {
    int worstVal = Status.GOOD.val;
    String worstError = null;
    for (Device device : devices) {
      if (device.getHealth().getStatus().val < worstVal) {
        worstVal = device.getHealth().getStatus().val;
        worstError = device.getHealth().getError();
      }
    }
    return new Health(Status.from(worstVal), worstError);
  }

  //To String
  public String toString() {
    return status.toString() + (error != null ? (" " + error) : "");
  }
}

