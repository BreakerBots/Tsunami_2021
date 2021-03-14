/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib.subsystem;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.team5104.lib.devices.Device;
import com.team5104.lib.devices.Health;
import com.team5104.lib.devices.MotorDevice;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/** A snickers wrapper of all the requirements of a subsystem. */
@JsonPropertyOrder({ "mode", "health", "finiteState" })
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
  private Object constants;

  /** Base Constructor
   * @param constants Link SubsystemConstants class from Constants.java */
  public Subsystem(Object constants) {
    this.constants = constants;
  }

  /** Main Update Loop (runs 50hz even while disabled) */
  public abstract void update();

  /** Alt Update Loop (runs 100hz even while disabled) */
  public void fastUpdate() { }

  /** Reset Values Here (called when enabled and disabled) */
  public void reset() { }

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
  @JsonGetter("health")
  public Health getHealth() {
    return Health.merge(devices);
  }
  /** @return the current SubsystemMode */
  @JsonGetter("mode")
  public SubsystemMode getMode() {
    return mode;
  }
  /** @return whether the subsystem is in the given SubsystemMode */
  public boolean is(SubsystemMode mode) {
    return this.mode == mode;
  }
  /** @return the finite state of the subsystem */
  @JsonGetter("state")
  public String getFiniteState() { return finiteState; }

  /** Attempts to change the Subsystem Mode @param mode
   * @return whether the change was successful */
  public boolean setMode(SubsystemMode mode) { return setMode(mode, false); }
  /** Attempts to change the Subsystem Mode @param mode
   * @force forces the state change
   * @return whether the change was successful */
  public boolean setMode(SubsystemMode mode, boolean force) {
    //otherwise you can do anything if you force it
    if (force) {
      this.mode = mode;
      return true;
    }

    //cant get out of disabled, detached, homing, or characterizing wo/ forcing it
    if (this.mode == SubsystemMode.DISABLED ||
        this.mode == SubsystemMode.HOMING ||
        this.mode == SubsystemMode.CHARACTERIZING ||
        this.mode == SubsystemMode.DETACHED)
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

  /** @return a list of devices on this subsystem */
  @JsonIgnore
  public List<Device> getDevices() {
    return devices;
  }

  /** @return the corresponding object from Constants.java for this subsystem */
  @JsonIgnore
  public Object getConstants() {
    return constants;
  }
}
