package com.team5104.lib.dashboard;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.HashMap;
import java.util.Map;

public class DataConstructor {
  private Map<String, String> properties;

  public DataConstructor() {
    properties = new HashMap<String, String>();
  }

  public void put(String key, String data) {
    properties.put(key, data);
  }
  public void put(String key, double data) {
    put(key, Double.toString(data));
  }
  public void put(String key, int data) {
    put(key, Integer.toString(data));
  }
  public void put(String key, boolean data) {
    put(key, Boolean.toString(data));
  }

  @JsonAnyGetter
  public Map<String, String> getProperties() {
    return properties;
  }
}
