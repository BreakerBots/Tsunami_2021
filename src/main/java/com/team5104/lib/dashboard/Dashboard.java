package com.team5104.lib.dashboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5104.frc2021.Constants;
import com.team5104.frc2021.Controls;
import com.team5104.frc2021.Superstructure;
import com.team5104.lib.CrashHandler;
import com.team5104.lib.Looper;
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

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.ArrayList;

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
      if (!isConnected()) {
        //dont send anything if not connected
        return;
      }

      //data constructor
      JSONConstructor data = new JSONConstructor();

      //page data
      JSONConstructor pageData = getPageData(false);
      if (pageData != null) {
        data.put("pageData", pageData);
      }

      //status data
      JSONConstructor robotData = new JSONConstructor();
      robotData.put("fpgaTimestamp", (int) Timer.getFPGATimestamp());
      robotData.put("batteryVoltage", RobotController.getBatteryVoltage());
      robotData.put("mode", RobotState.getMode().toString());
      data.put("robotData", robotData);

      //logs
      //data.put("logs", new ArrayList(console.readBuffer()));

      //#sendit
      sendMessage(data.toString());

      //kill extra connections
      killExtraConnections();
    }, 1, 500));
  }
  public static JSONConstructor getPageData(boolean init) {
    JSONConstructor pageData = new JSONConstructor();
    pageData.put("url", url);

    //home
    if (url.equals("/")) {
      pageData.put("Superstructure", new Superstructure());
      if (SubsystemManager.getSubsystems() != null) {
        for (Subsystem subsystem : SubsystemManager.getSubsystems()) {
          pageData.put(subsystem.getClass().getSimpleName(), subsystem);
        }
      }
    }
    else if (url.equals("/controls") && init) {
      try {
        Field[] fields = Controls.class.getDeclaredFields();
        for (Field field : fields) {
          field.setAccessible(true);
          pageData.put(field.getName(), field.get(null));
          field.setAccessible(false);
        }
      } catch (IllegalAccessException e) { CrashHandler.log(e); }
    }
    else if (url.indexOf("/tuner") != -1) {
      if (url.length() > 6) {
        String query = url.substring(14);
        if (SubsystemManager.getSubsystems() != null) {
          for (Subsystem subsystem : SubsystemManager.getSubsystems()) {
            if (subsystem.getClass().getSimpleName().equals(query)) {
              pageData.put("name", query);
              pageData.put("meta", subsystem);
              pageData.put("constants", subsystem.getConstants());
            }
          }
        }
      }

      Subsystem[] subsystems = SubsystemManager.getSubsystems();
      int size = subsystems.length;
      String[] subsystemNames = new String[size];
      for (int i = 0; i < size; i++) {
        subsystemNames[i] = subsystems[i].getClass().getSimpleName();
      }
      pageData.put("subsystems", subsystemNames);
    }
    else if (url.indexOf("/devices") != -1) {
      if (SubsystemManager.getSubsystems() != null) {
        for (Subsystem subsystem : SubsystemManager.getSubsystems()) {
          pageData.put(subsystem.getClass().getSimpleName(),
                       new ArrayList(subsystem.getDevices()));
        }
        //TODO
      }
    }

    //return page data
    if (pageData.getProperties().size() > 1) {
      return pageData;
    }
    return null; //dont return empty data
  }
  public static void killExtraConnections() {
    if (lastConnection != null && lastConnection.isOpen()) {
      JSONConstructor data = new JSONConstructor();
      data.put("die", true); //get rekt m8
      lastConnection.send(data.toString());
    }
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

      //Inputs
      if (node.has("url")) {
        url = node.get("url").asText();

        //url inits
        JSONConstructor pageData = getPageData(true);
        if (pageData != null) {
          JSONConstructor data = new JSONConstructor();
          data.put("pageData", pageData);
          sendMessage(data.toString());
        }
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
      if (node.has("trajectories")) {
        DashboardTrajectory.processAndSend(node.get("trajectories"));
      }
      if (node.has("targetPath")) {
        DashboardTrajectory.setTargetPath(node.get("targetPath"));
      }
      if (node.has("getTargetPath")) {
        DashboardTrajectory.sendTargetPath();
      }
      //TODO other inputs

    } catch (Exception e) { CrashHandler.log(e); }
  }
  public void onOpen(WebSocket newConnection, ClientHandshake handshake) {
    lastConnection = this.connection;
    connection = newConnection;
    if (lastConnection != null && lastConnection.isOpen()) {
      killExtraConnections();
      //console.log("new connection - replacing other");
    }
    else {
      //console.log("new connection");
    }

    //console.resetBuffer();

    //welcome package
    JSONConstructor data = new JSONConstructor();
    JSONConstructor robotData = new JSONConstructor();
    robotData.put("name", Constants.robot.name);
    robotData.put("inSim", RobotState.isSimulation());
    data.put("robotData", robotData);
    connection.send(data.toString());
  }
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    if (conn == lastConnection)
      return;

    //console.log("lost connection");
    if (RobotState.isSimulation()) {
      DriverStationSim.setEnabled(false);
    }
  }
  private Dashboard() {
    super(new InetSocketAddress(PORT));
  }
  public void onStart() {
    console.log("initialized");
    setConnectionLostTimeout(1000);
  }
  public void onError(WebSocket conn, Exception e) {
    CrashHandler.log(e);
  }

  //Other (non-instance)
  public static void close() {
    try {
      instance.stop();
    } catch (Exception e) { CrashHandler.log(e); }
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
