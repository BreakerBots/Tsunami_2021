package com.team5104.lib.dashboard;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.team5104.frc2021.auto.actions.DriveTrajectory;
import com.team5104.frc2021.auto.paths.ExamplePath;
import com.team5104.lib.auto.AutoManager;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Odometry;
import com.team5104.lib.auto.Position;
import edu.wpi.first.wpilibj.trajectory.Trajectory.State;
import edu.wpi.first.wpilibj.util.Units;

import java.util.List;

/** Processes dashboard data for trajectories */
public class DashboardTrajectory {
  /** Process JSON data of a trajectory and send
   * back the generated points. */
  public static void processAndSend(JsonNode trajectoryNodes) throws Exception {
    //process trajectories
    int trajectoriesSize = trajectoryNodes.size();
    JSONConstructor trajectoryData = new JSONConstructor();
    for (int t = 0; t < trajectoriesSize; t++) {
      JsonNode positionNodes = trajectoryNodes.get(t).get("positions");
      int positionsSize = positionNodes.size();
      Position[] positions = new Position[positionsSize];
      for (int p = 0; p < positionsSize; p++) {
        JsonNode nums = positionNodes.get(p);
        positions[p] = new Position(nums.get(0).asDouble(), nums.get(1).asDouble(), nums.get(2).asDouble());
      }
      DriveTrajectory traj = new DriveTrajectory(
          trajectoryNodes.get(t).get("reversed").asBoolean(),
          positions
      );
      trajectoryData.put("traj"+t, new PlottableTrajectory(traj.getPoints()));
    }

    //send trajectories
    JSONConstructor data = new JSONConstructor();
    data.put("trajectory", trajectoryData);
    Dashboard.sendMessage(data.toString());
  }

  /** Sets the AutoManager's target path from JSON data */
  public static void setTargetPath(JsonNode targetPathNode) throws Exception {
    //set target path
    AutoManager.setTargetPath(
        (AutoPath) Class.forName(
            ExamplePath.class.getPackageName() + "." + targetPathNode.asText()
        ).getConstructor().newInstance(),
        true
    );
  }

  /** Sends the AutoManager's target path */
  public static void sendTargetPath() {
    if (AutoManager.getTargetPath() != null) {
      JSONConstructor data = new JSONConstructor();
      data.put("targetPath", AutoManager.getTargetPath().getClass().getSimpleName());
      Dashboard.sendMessage(data.toString());
    }
  }

  /** Sends out Odometry data */
  private static boolean firstOdometryPoint = true;
  public static void sendOdometry() {
    JSONConstructor odometryData = new JSONConstructor();
    Position pos = Odometry.getPositionFeet();
    odometryData.put("xfeet", pos.getXFeet());
    odometryData.put("yfeet", pos.getYFeet());
    odometryData.put("deg", pos.getDegrees());
    odometryData.put("first", firstOdometryPoint);
    JSONConstructor data = new JSONConstructor();
    data.put("odometry", odometryData);
    Dashboard.sendMessage(data.toString());
    firstOdometryPoint = false;
  }

  /** Sets up for new path */
  public static void alertStartingPath() {
    firstOdometryPoint = true;
  }

  /** A util file for processAndSend() that stores
   * trajectories as arrays of x and y points */
  private static class PlottableTrajectory {
    public static final double SCALE_FACTOR = 4;
    public double[] x;
    public double[] y;

    public PlottableTrajectory(List<State> points) {
      int size = points.size();
      int newSize = (int) (size / SCALE_FACTOR);
      x = new double[newSize + 1];
      y = new double[newSize + 1];
      for (int i = 0; i < size - SCALE_FACTOR + 1; i += SCALE_FACTOR) {
        x[(int) (i / SCALE_FACTOR)] = Units.metersToFeet(points.get(i).poseMeters.getTranslation().getX());
        y[(int) (i / SCALE_FACTOR)] = Units.metersToFeet(points.get(i).poseMeters.getTranslation().getY());
      }
      //connect last point
      x[newSize] = Units.metersToFeet(points.get(size-1).poseMeters.getTranslation().getX());
      y[newSize] = Units.metersToFeet(points.get(size-1).poseMeters.getTranslation().getY());
    }

    @JsonGetter("x")
    public double[] getX() {
      return x;
    }

    @JsonGetter("y")
    public double[] getY() {
      return y;
    }
  }
}

