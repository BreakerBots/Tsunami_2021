package frc.team5104.lib.auto;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import frc.team5104.frc2021.subsystems.Drive;
import frc.team5104.lib.Util;
import frc.team5104.lib.console;

/** keeps of the position of the robot relative to the field (in x, y, angle) */
public class Odometry {
	private static DifferentialDriveOdometry odometry;
	
	public static void init() {
		Drive.reset();
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
				Util.metersToFeet(getPose2dMeters().getTranslation().getX()),
				Util.metersToFeet(getPose2dMeters().getTranslation().getY()),
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
			Drive.reset();
			odometry.resetPosition(
					position.toPose2dMeters(),
					Rotation2d.fromDegrees(position.getDegrees())
				);
		}
	}
}