package com.team5104.lib.auto;

import com.team5104.lib.Util;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory.State;
import edu.wpi.first.wpilibj.util.Units;
import java.util.ArrayList;
import java.util.List;

/** A class that helps store a field-relative position in feet */
public class Position {

  private double xFeet, yFeet, degrees;

  public Position(Pose2d poseMeters) {
    this(
        Units.metersToFeet(poseMeters.getTranslation().getX()),
        Units.metersToFeet(poseMeters.getTranslation().getY()),
        poseMeters.getRotation().getDegrees());
  }

  public Position(double xFeet, double yFeet) {
    this(xFeet, yFeet, 0);
  }

  public Position(double xFeet, double yFeet, double degrees) {
    this.xFeet = xFeet;
    this.yFeet = yFeet;
    this.degrees = degrees;
  }

  public double getXFeet() {
    return xFeet;
  }

  public double getYFeet() {
    return yFeet;
  }

  public double getDegrees() {
    return degrees;
  }

  public void subtract(Position offset) {
    this.xFeet -= offset.getXFeet();
    this.yFeet -= offset.getYFeet();
    this.degrees -= offset.getDegrees();
  }

  public Pose2d toPose2dFeet() {
    return new Pose2d(xFeet, yFeet, Rotation2d.fromDegrees(degrees));
  }

  public Pose2d toPose2dMeters() {
    return new Pose2d(
        Units.feetToMeters(xFeet), Units.feetToMeters(yFeet), Rotation2d.fromDegrees(degrees));
  }

  public String toString() {
    return "position: {"
        + "xft: "
        + Util.round(xFeet, 2)
        + ", "
        + "yft: "
        + Util.round(yFeet, 2)
        + ", "
        + "deg: "
        + Util.round(degrees, 2)
        + "}";
  }

  public static List<Position> fromStates(List<State> states) {
    List<Position> positions = new ArrayList<Position>();
    for (State state : states) {
      positions.add(new Position(state.poseMeters));
    }
    return positions;
  }

  public static List<Pose2d> toPose2dMeters(Position[] positions) {
    List<Pose2d> pose2ds = new ArrayList<Pose2d>();
    for (Position position : positions) {
      pose2ds.add(position.toPose2dMeters());
    }
    return pose2ds;
  }
}
