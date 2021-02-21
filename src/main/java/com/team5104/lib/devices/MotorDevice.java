package com.team5104.lib.devices;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

/** A class that groups BaseTalons to simulate a Device */
public class MotorDevice extends Device {
  private BaseMotorController motor;

  public MotorDevice(BaseMotorController motor) {
    this.motor = motor;
  }

  public Health getHealth() {
    Faults faults = new Faults();
    motor.getFaults(faults);
    return new Health(faults);
  }

  public void stop() {
    if (motor.getControlMode() != ControlMode.Follower)
      motor.set(ControlMode.Disabled, 0);
  }
}

