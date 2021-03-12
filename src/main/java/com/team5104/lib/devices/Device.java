package com.team5104.lib.devices;

import com.fasterxml.jackson.annotation.JsonGetter;

public abstract class Device {

  @JsonGetter
  public abstract Health getHealth();
  public abstract void stop();

}

