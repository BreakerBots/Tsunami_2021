package com.team5104.lib.dashboard;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5104.lib.Looper;
import com.team5104.lib.Looper.Crash;
import com.team5104.lib.console;

import java.util.LinkedHashMap;
import java.util.Map;

public class JSONConstructor {
  private LinkedHashMap<String, Object> properties;

  public JSONConstructor() {
    properties = new LinkedHashMap<String, Object>();
  }

  public void put(String key, Object data) {
    properties.put(key, data);
  }

  @JsonAnyGetter
  public LinkedHashMap<String, Object> getProperties() {
    return properties;
  }

  public void print() {
    StringBuilder builder = new StringBuilder();
    for (Map.Entry<String, Object> entry : properties.entrySet()) {
      builder.append(entry.getKey());
      builder.append(", ");
    }
    console.log(builder);
  }

  public String toString() {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) { Looper.logCrash(new Crash(e)); }
    return "";
  }
}
