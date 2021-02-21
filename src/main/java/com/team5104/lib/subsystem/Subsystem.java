/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib.subsystem;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.team5104.lib.devices.Device;
import com.team5104.lib.devices.Health;
import com.team5104.lib.devices.MotorDevice;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * A snickers wrapper of all the requirements of a subsystem.
 */
public abstract class Subsystem {
  public enum SubsystemMode {
    DETACHED,
    DISABLED,
    OPERATING, //running normally
    HOMING,
    CHARACTERIZING
  }

  SubsystemMode mode = SubsystemMode.DETACHED;
  private List<Device> devices = new ArrayList<>();
  private String finiteState = "Not Set";
  private SubsystemConstants constants;

  /** Base Constructor
   * @param constants Link SubsystemConstants class from Constants.java */
  public Subsystem(SubsystemConstants constants) {
    this.constants = constants;
  }

  /** Main Update Loop (runs 50hz even while disabled) */
  public abstract void update();

  /** Alt Update Loop (runs 100hz even while disabled) */
  public void fastUpdate() { }

  /** Reset Values Here (called when enabled and disabled) */
  public void reset() { }

  /** Subsystem Constants (Extendable Class) */
  public static class SubsystemConstants { }

  //Built-In
  /** Identifies all devices in the subsystem */
  final void identifyDevices() throws IllegalAccessException {
    Field[] fields = this.getClass().getDeclaredFields();
    for (Field field : fields) {
      //if device or motor
      if (Device.class.isAssignableFrom(field.getType()) ||
          BaseMotorController.class.isAssignableFrom(field.getType())) {
        //make sure we can read it
        field.setAccessible(true);

        //get the obj
        Object obj;
        if (Modifier.isStatic(field.getModifiers()))
          obj = field.get(null);
        else obj = field.get(this);

        //turn motor into MotorDevice
        if (BaseMotorController.class.isAssignableFrom(field.getType()) && obj != null) {
          obj = new MotorDevice((BaseMotorController) obj);
        }

        //add obj to list
        if (obj != null)
          devices.add((Device) obj);

        //prevent ability to read
        field.setAccessible(false);
      }
    }
  }

  /** Stops all devices on the subsystem */
  public final void stop() {
    if (devices != null && devices.size() > 0) {
      for (Device device : devices) {
        device.stop();
      }
    }
  }

  /** @return the merged health of all devices */
  public Health getHealth() {
    return Health.merge(devices);
  }
  /** @return the current SubsystemMode */
  public SubsystemMode getMode() {
    return mode;
  }
  /** @return whether the subsystem is in the given SubsystemMode */
  public boolean is(SubsystemMode mode) {
    return this.mode == mode;
  }
  /** @return the finite state of the subsystem */
  public String getFiniteState() { return finiteState; }

  /** Attempts to change the Subsystem Mode @param mode
   * @return whether the change was successful */
  public boolean setMode(SubsystemMode mode) { return setMode(mode, true); }
  /** Attempts to change the Subsystem Mode @param mode
   * @force forces the state change
   * @return whether the change was successful */
  public boolean setMode(SubsystemMode mode, boolean force) {
    //cant switch to/from detached no matter what
    if (mode == SubsystemMode.DETACHED || this.mode == SubsystemMode.DETACHED)
      return false;

    //otherwise you can do anything if you force it
    if (force) {
      this.mode = mode;
      return true;
    }

    //cant get out of disabled, homing, or characterizing wo/ forcing it
    if (this.mode == SubsystemMode.DISABLED ||
        this.mode == SubsystemMode.HOMING ||
        this.mode == SubsystemMode.CHARACTERIZING)
      return false;

    //otherwise free to do anything
    this.mode = mode;
    return true;
  }
  /** Sets the finite state (the current state in time that doesnt rely
   * on past values) of the subsystem to be displayed on the dashboard */
  public void setFiniteState(String finiteState) {
    this.finiteState = finiteState;
  }
}
