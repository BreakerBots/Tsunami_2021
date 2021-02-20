package com.team5104.lib.devices;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

/** A class that groups BaseTalons to simulate a Device */
public class MotorGroup extends Device {
  private BaseMotorController[] motors;

  public MotorGroup(BaseMotorController... motors) {
    this.motors = motors;
  }

  public Health getHealth() {
    Health[] healths = new Health[motors.length];

    for (int i = 0; i < motors.length; i++) {
      Faults faults = new Faults();
      motors[i].getFaults(faults);
      healths[i] = new Health(faults);
    }

    return Health.merge(healths);
  }

  public void stop() {
    for (BaseMotorController motor : motors) {
      motor.set(ControlMode.Disabled, 0);
    }
  }
}

