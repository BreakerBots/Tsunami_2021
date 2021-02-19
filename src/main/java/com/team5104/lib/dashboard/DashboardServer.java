package com.team5104.lib.dashboard;

import com.team5104.lib.Looper;
import com.team5104.lib.Looper.Crash;
import com.team5104.lib.console;
import com.team5104.lib.setup.RobotState;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class DashboardServer extends WebSocketServer {
  public static final int PORT = 5804; //5802-5810
  private WebSocket connection, lastConnection;

  //Constructor
  public DashboardServer() {
    super(new InetSocketAddress(PORT));
  }

  //Receive
  public void onMessage(WebSocket conn, String message) {
    if (conn != connection)
      console.error("received data from invalid host");

    console.log(message);
  }

  //Send
  public void sendMessage(String text) {
    if (isConnected()) {
      connection.send(text);
    }
  }

  //New Connection
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

    connection.send("Welcome to the server!");
  }

  //Lost Connection
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    if (conn == lastConnection)
      return;

    console.log("lost connection");
  }

  public boolean isConnected() {
    return connection != null && connection.isOpen();
  }

  //Init
  public void onStart() {
    console.log("running on " + getOrigin() + ":" + getPort());
    setConnectionLostTimeout(1000);
  }

  //Origin
  public String getOrigin() {
    return RobotState.isSimulation() ? "localhost" : "10.51.4.2";
  }

  //Error
  public void onError(WebSocket conn, Exception e) {
    Looper.logCrash(new Crash(e));
  }
}
