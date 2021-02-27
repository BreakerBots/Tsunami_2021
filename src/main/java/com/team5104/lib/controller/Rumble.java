package com.team5104.lib.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.hal.HAL;

/** A class that stores and manages a rumble for XboxController.java */
public class Rumble {
  private final int controllerPort, timeoutMs, dipCount;
  @JsonProperty("strength")
  private final short strength;
  @JsonProperty("hard")
  private final boolean hard;
  private long startTime;

  Rumble(int controllerPort, double strength, boolean hard, int timeoutMs, int dipCount) {
    this.controllerPort = controllerPort;
    this.strength = (short) ((strength < 0 ? 0 : (strength > 1 ? 1 : strength)) * 65535);
    this.hard = hard;
    this.timeoutMs = timeoutMs < 50 ? 50 : timeoutMs;
    this.dipCount = dipCount < 0 ? 0 : dipCount;
  }

  void update() {
    boolean on = true;
    double x = ((double) (System.currentTimeMillis() - startTime)) / timeoutMs;
    for (int i = 0; i < dipCount; i++) {
      if (x > (0.4 / dipCount) + ((double) i / dipCount) && x < (0.6 / dipCount) + ((double) i / dipCount))
        on = false;
    }
    setRumble(on);

    if (System.currentTimeMillis() > startTime + timeoutMs) {
      for (XboxController controller : XboxController.getControllers()){
        if (controller.port == controllerPort) {
          controller.setActiveRumble(null);
        }
      }
    }
  }

  private void setRumble(boolean on) {
    if (XboxController.isConnected(controllerPort)) {
      if (on)
        HAL.setJoystickOutputs((byte) controllerPort, 0, hard ? strength : 0, hard ? 0 : strength);
      else HAL.setJoystickOutputs((byte) controllerPort, 0, (short) 0, (short) 0);
    }
  }

  public void start() {
    startTime = System.currentTimeMillis();
    for (XboxController controller : XboxController.getControllers()) {
      if (controller.port == controllerPort) {
        controller.setActiveRumble(this);
      }
    }
  }

  public static class DoubleRumble extends Rumble {
    @JsonProperty("child1")
    private final Rumble rumble1;
    @JsonProperty("child2")
    private final Rumble rumble2;

    public DoubleRumble(Rumble rumble1, Rumble rumble2) {
        super(0, 0, false, 0, 0);
        this.rumble1 = rumble1;
        this.rumble2 = rumble2;
    }

    void update() {
        // do nothing
    }
    public void start() {
        rumble1.start();
        rumble2.start();
    }
  }
}

