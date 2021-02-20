package com.team5104.lib.subsystem;

import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.ArrayList;

public class Characterizer {
  private static boolean running;
  private static NetworkTableEntry autoSpeedEntry, rotateEntry, telemetryEntry;
  private static ArrayList<Double> data = new ArrayList<Double>();
  private static ServoSubsystem subsystem;

  public static void init(Class<? extends ServoSubsystem> targetSubsystem) {
//    subsystem = SubsystemManager.getSubsystem(targetSubsystem);
//
//    if (subsystem == null) {
//      console.error("subsystem to characterize could not be found.");
//      return;
//    }
//
//    running = true;
//    RobotState.setLoopPeriod(0.005);
//    NetworkTableInstance.getDefault().setUpdateRate(0.01);
//    LiveWindow.disableAllTelemetry();
//    autoSpeedEntry = NetworkTableInstance.getDefault().getEntry("/robot/autospeed");
//    rotateEntry = NetworkTableInstance.getDefault().getEntry("/robot/rotate");
//    telemetryEntry = NetworkTableInstance.getDefault().getEntry("/robot/telemetry");
  }

  public static void update() {
//    double[] arr = subsystem.getCharacterization(
//        autoSpeedEntry.getDouble(0),
//        rotateEntry.getBoolean(false)
//    );
//
//    for (double num : arr)
//      data.add(num);
  }

  public static void enabled() {
//    data.clear();
  }

  public static void disabled() {
//    String str = data.toString();
//    telemetryEntry.setString(str.substring(1, str.length() - 1) + ", ");
//    data.clear();
  }

  public static boolean isRunning() {
    return running;
  }
}
