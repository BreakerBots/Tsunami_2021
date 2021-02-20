package com.team5104.lib.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5104.frc2021.Constants;
import com.team5104.lib.Looper;
import com.team5104.lib.Looper.Crash;
import com.team5104.lib.Looper.Loop;
import com.team5104.lib.Looper.TimedLoop;
import com.team5104.lib.console;
import com.team5104.lib.setup.RobotState;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class Dashboard extends WebSocketServer {
  private static WebSocket connection, lastConnection;
  private static Dashboard instance;
  public static final int PORT = 5804; //5802-5810
  public static String url;

  //Init
  public static void init() {
    Looper.registerLoop(new Loop("Dashboard-Read", () -> {
      instance = new Dashboard();
      instance.run();
    }, 2));

    Looper.registerLoop(new TimedLoop("Dashboard-Send", () -> {
      try {
        //status data
        ObjectMapper objectMapper = new ObjectMapper();
        DataConstructor data = new DataConstructor();
        data.put("fpgaTimestamp", (int) Timer.getFPGATimestamp());
        data.put("batteryVoltage", RobotController.getBatteryVoltage());
        data.put("mode", RobotState.getMode().toString());
        sendMessage("{\"robotData\":" + objectMapper.writeValueAsString(data) + "}");
      } catch (JsonProcessingException e) { Looper.logCrash(new Crash(e)); }
    }, 1, 500));
  }

  //Instance
  public void onMessage(WebSocket conn, String message) {
    if (conn != connection) {
      console.error("received data from invalid host");
      return;
    }

    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode node = mapper.readTree(message);

      if (node.has("url")) {
        url = node.get("url").asText();
        console.log(url);

        if (url.equals("/home")) {
          //welcome package
//          ObjectMapper objectMapper = new ObjectMapper();
//          DataConstructor data = new DataConstructor();

//          for ()
//
//          data.put("name", Constants.robot.name);
//          data.put("inSim", RobotState.isSimulation());
//          connection.send("{\"robotData\":" + objectMapper.writeValueAsString(data) + "}");
        }

        //TODO other urls
      }
      //TODO other inputs

    } catch (JsonProcessingException e) { Looper.logCrash(new Crash(e)); }
  }
  public void onOpen(WebSocket connection, ClientHandshake handshake) {
    lastConnection = this.connection;
    this.connection = connection;
    if (lastConnection != null && lastConnection.isOpen()) {
      lastConnection.close();
      console.log("new connection - replacing other");
    }
    else {
      console.log("new connection");
    }

    try {
      //welcome package
      ObjectMapper objectMapper = new ObjectMapper();
      DataConstructor data = new DataConstructor();
      data.put("name", Constants.robot.name);
      data.put("inSim", RobotState.isSimulation());
      connection.send("{\"robotData\":" + objectMapper.writeValueAsString(data) + "}");
    } catch (JsonProcessingException e) { Looper.logCrash(new Crash(e)); }
  }
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    if (conn == lastConnection)
      return;

    console.log("lost connection");
  }
  private Dashboard() {
    super(new InetSocketAddress(PORT));
  }
  public void onStart() {
    console.log("running on " + getOrigin() + ":" + getPort());
    setConnectionLostTimeout(1000);
  }
  public void onError(WebSocket conn, Exception e) {
    Looper.logCrash(new Crash(e));
  }

  //Other (non-instance)
  public static void close() {
    try {
      instance.stop();
    } catch (Exception e) { Looper.logCrash(new Crash(e)); }
  }
  public static boolean isConnected() {
    return connection != null && connection.isOpen();
  }
  public static void sendMessage(String text) {
    if (isConnected()) {
      connection.send(text);
    }
  }
  public static String getOrigin() {
    return RobotState.isSimulation() ? "localhost" : "10.51.4.2";
  }
}
