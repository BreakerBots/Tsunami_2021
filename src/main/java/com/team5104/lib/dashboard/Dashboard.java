package com.team5104.lib.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5104.frc2021.Constants;
import com.team5104.frc2021.Superstructure;
import com.team5104.lib.Looper;
import com.team5104.lib.Looper.Crash;
import com.team5104.lib.Looper.Loop;
import com.team5104.lib.Looper.TimedLoop;
import com.team5104.lib.console;
import com.team5104.lib.setup.RobotState;
import com.team5104.lib.setup.RobotState.RobotMode;
import com.team5104.lib.subsystem.Subsystem;
import com.team5104.lib.subsystem.SubsystemManager;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class Dashboard extends WebSocketServer {
  private static WebSocket connection, lastConnection;
  private static Dashboard instance;
  public static final int PORT = 5804; //5802-5810
  public static String url = "";

  //Init
  public static void init() {
    Looper.registerLoop(new Loop("Dashboard-Read", () -> {
      instance = new Dashboard();
      instance.run();
    }, 2));

    Looper.registerLoop(new TimedLoop("Dashboard-Send", () -> {
      DataConstructor data = new DataConstructor();

      //page data
      data.put("pageData", getPageData());

      //status data
      DataConstructor robotData = new DataConstructor();
      robotData.put("fpgaTimestamp", (int) Timer.getFPGATimestamp());
      robotData.put("batteryVoltage", RobotController.getBatteryVoltage());
      robotData.put("mode", RobotState.getMode().toString());
      data.put("robotData", robotData);

      //#sendit
      sendMessage(data.toString());
    }, 1, 500));
  }
  public static DataConstructor getPageData() {
    DataConstructor pageData = new DataConstructor();
    pageData.put("url", url);

    //home
    if (url.equals("/")) {
      pageData.put("Superstructure", new Superstructure());
      for (Subsystem subsystem : SubsystemManager.getSubsystems()) {
        pageData.put(subsystem.getClass().getSimpleName(), subsystem);
      }
    }
    //TODO other urls

    return pageData;
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

        //send page data
        DataConstructor data = new DataConstructor();
        data.put("pageData", getPageData());
        sendMessage(data.toString());
      }
      if (node.has("robotMode")) {
        String newRobotMode = node.get("robotMode").asText();
        if (newRobotMode.equals(RobotMode.AUTONOMOUS.toString())) {
          DriverStationSim.setAutonomous(true);
          DriverStationSim.setEnabled(true);
        }
        else if (newRobotMode.equals(RobotMode.DISABLED.toString())) {
          DriverStationSim.setEnabled(false);
        }
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

    //welcome package
    DataConstructor data = new DataConstructor();
    DataConstructor robotData = new DataConstructor();
    robotData.put("name", Constants.robot.name);
    robotData.put("inSim", RobotState.isSimulation());
    data.put("robotData", robotData);
    connection.send(data.toString());
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
