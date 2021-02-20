package com.team5104.lib.devices;

import com.team5104.lib.devices.Health.Status;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Solenoid extends Device {
  private DoubleSolenoid doubleBase;
  private edu.wpi.first.wpilibj.Solenoid singleBase;
  private boolean reversed;

  public Solenoid(int port) { this(port, false); }
  public Solenoid(int port, boolean reversed) {
    this.reversed = reversed;
    singleBase = new edu.wpi.first.wpilibj.Solenoid(port);
  }

  public Solenoid(int[] ports) { this(ports, false); }
  public Solenoid(int forwardPort, int reversePort) { this(forwardPort, reversePort, false); }
  public Solenoid(int[] ports, boolean reversed) { this(ports[0], ports[1], reversed); }
  public Solenoid(int forwardPort, int reversedPort, boolean reversed) {
    this.reversed = reversed;
    doubleBase = new DoubleSolenoid(forwardPort, reversedPort);
  }

  public void set(boolean forward) {
    if (doubleBase != null)
      doubleBase.set(forward ^ reversed ? Value.kForward : Value.kReverse);
    else singleBase.set(forward ^ reversed);
  }

  public void stop() {
    if (doubleBase != null)
      doubleBase.set(Value.kOff);
    else singleBase.set(false);
  }

  public Health getHealth() {
    return new Health(Status.GOOD); //no way of detecting issues
  }
}

