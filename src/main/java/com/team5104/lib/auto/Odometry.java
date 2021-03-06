package com.team5104.lib.auto;

import com.team5104.frc2021.subsystems.Drive;
import com.team5104.lib.console;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.util.Units;

/** keeps of the position of the robot relative to the field (in x, y, angle) */
public class Odometry {
  private static DifferentialDriveOdometry odometry = new DifferentialDriveOdometry(new Rotation2d());

  public static void init() {
    odometry = new DifferentialDriveOdometry(new Rotation2d());
    console.log("initialized");
  }

  public static void update() {
    if (odometry != null) {
      odometry.update(
        Rotation2d.fromDegrees(-Drive.getHeading()),
        Drive.getDriveStateMeters()[0],
        Drive.getDriveStateMeters()[1]
      );
    }
  }

  public static Pose2d getPose2dMeters() {
    return (odometry == null) ? null : odometry.getPoseMeters();
  }

  public static Position getPositionFeet() {
    if (odometry == null)
      return new Position(0, 0, 0);
    return new Position(
        Units.metersToFeet(getPose2dMeters().getTranslation().getX()),
        Units.metersToFeet(getPose2dMeters().getTranslation().getY()),
        getPose2dMeters().getRotation().getDegrees()
      );
  }

  public static DifferentialDriveWheelSpeeds getWheelSpeeds() {
    return new DifferentialDriveWheelSpeeds(
        Drive.getDriveStateMeters()[2],
        Drive.getDriveStateMeters()[3]
    );
  }

  public static void reset(Position position) {
    if (odometry != null) {
      Drive.zero();
      odometry.resetPosition(
          position.toPose2dMeters(),
          Rotation2d.fromDegrees(position.getDegrees())
        );
      console.log(position.toString());
    }
  }
}
