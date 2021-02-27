package com.team5104.lib.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.DriverStation;

/** A class that stores and manages a button for XboxController.java */
public class Button {
  public enum Slot {
    A(1),
    B(2),
    X(3),
    Y(4),
    LEFT_BUMPER(5),
    RIGHT_BUMPER(6),
    MENU(7),
    LIST(8),
    LEFT_JOYSTICK_PRESS(9),
    RIGHT_JOYSTICK_PRESS(10),
    DIRECTION_PAD_UP_LEFT(315),
    DIRECTION_PAD_UP(0),
    DIRECTION_PAD_UP_RIGHT(45),
    DIRECTION_PAD_RIGHT(90),
    DIRECTION_PAD_DOWN_LEFT(225),
    DIRECTION_PAD_DOWN(180),
    DIRECTION_PAD_DOWN_RIGHT(135),
    DIRECTION_PAD_LEFT(270),
    NONE(0);
    private int port;
    private Slot(int port) {
      this.port = port;
    }
  }
  public enum ButtonType {
    NORMAL,
    HOLD,
    HOLD_TIME,
    DOUBLE_CLICK
  }

  @JsonProperty("buttonType")
  private final ButtonType buttonType;
  private final int controllerPort;
  @JsonProperty("slot")
  private final Slot slot;
  private boolean value, lastValue, pressed, released, heldEventReturned;
  private long heldEventTime, doubleClickTime;
  private int heldEventLength, doubleClickKilloff, doubleClickIndex = 0;

  Button(int controllerPort, Slot slot, ButtonType buttonType, int demand) {
    this.controllerPort = controllerPort;
    this.slot = slot;
    this.buttonType = buttonType;
    if (buttonType == ButtonType.HOLD_TIME) heldEventLength = demand;
    if (buttonType == ButtonType.DOUBLE_CLICK) doubleClickKilloff = demand;
  }

  //Update
  void update() {
    pressed = false;
    released = false;
    value = isDown();

    //Pressed/Released
    if (value != lastValue) {
      lastValue = value;
      if (value) {
        pressed = true;
        heldEventTime = System.currentTimeMillis();
      }
      else released = true;
    }

    //Held Event
    if (!value) heldEventReturned = false;

    //Double Click
    doubleClickIndex = 0;
    if (doubleClickTime != -1 && System.currentTimeMillis() > doubleClickTime + doubleClickKilloff) {
      //Killoff
      doubleClickIndex = 1;
      doubleClickTime = -1;
    }
    if (pressed) {
      //First Click
      if (doubleClickTime == -1) {
        doubleClickTime = System.currentTimeMillis();
      }
      //Second Click
      else {
        doubleClickIndex = 2;
        doubleClickTime = -1;
      }
    }
  }

  //Reads

  /**
   * Returns true on event trigger
   * - Normal: button pressed event
   * - Hold: if button is down
   * - Hold Time: held event
   * - Double Click: double click event
   */
  public boolean get() {
    if (buttonType == ButtonType.NORMAL)
      return pressed;
    if (buttonType == ButtonType.HOLD)
      return isDown();
    if (buttonType == ButtonType.HOLD_TIME) {
      boolean temp = ((value ? ((double) (System.currentTimeMillis() - heldEventTime)) : 0) > heldEventLength) && (!heldEventReturned);
      if (temp)
        heldEventReturned = true;
      return temp;
    }
    if (buttonType == ButtonType.DOUBLE_CLICK)
      return doubleClickIndex == 2;
    return false;
  }

  /**
   * Returns an alternative trigger depending on button type.
   * - Normal: returns button released action.
   * - Hold: returns nothing
   * - Hold Time: returns nothing.
   * - Double Click: returns if the double click was killed off with only 1 button pressed (event).
   */
  public boolean getAlt() {
    if (buttonType == ButtonType.NORMAL)
      return released;
    if (buttonType == ButtonType.DOUBLE_CLICK)
      return doubleClickIndex == 1;
    return false;
  }

  /** Returns true if the button is down */
  private boolean isDown() {
    if (XboxController.isConnected(controllerPort)) {
      if (slot.port > 0 && slot.port < 11)
        return DriverStation.getInstance().getStickButton(controllerPort, (byte) slot.port);
      return DriverStation.getInstance().getStickPOV(controllerPort, 0) == slot.port;
    }
    return false;
  }

  /** A child class that stores buttons for two controllers
   * and gives outputs combining them */
  public static class DoubleButton extends Button {
    @JsonProperty("child1")
    public final Button button1;
    @JsonProperty("child2")
    public final Button button2;

    public DoubleButton(Button button1, Button button2) {
        super(0, Slot.NONE, null, 0);
        this.button1 = button1;
        this.button2 = button2;
    }

    void update() { }

    /**
     * Ors the result of get() on both buttons
     * Returns true on event trigger (button pressed, held event, or double click)
     */
    public boolean get() {
        return button1.get() || button2.get();
    }

    /**
     * Ors the result of getAlt() on both buttons
     * Returns an alternative trigger depending on button type.
     * - Normal: returns button released action.
     * - Hold: returns nothing.
     * - Double Click: returns if the double click was killed off with only 1 button pressed (event).
     */
    public boolean getAlt() {
          return button1.getAlt() || button2.getAlt();
      }
  }
}

