package com.team5104.lib.dashboard;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.team5104.frc2021.auto.actions.DriveTrajectory;
import com.team5104.lib.auto.Position;
import edu.wpi.first.wpilibj.trajectory.Trajectory.State;
import edu.wpi.first.wpilibj.util.Units;

import java.util.List;

public class DashboardAuto {
  public static void handleTrajectory(JsonNode node) {
    JsonNode trajectoryNodes = node.get("trajectories");
    int trajectoriesSize = trajectoryNodes.size();
    DataConstructor trajectoryData = new DataConstructor();
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
    DataConstructor data = new DataConstructor();
    data.put("trajectory", trajectoryData);
    Dashboard.sendMessage(data.toString());
  }

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

