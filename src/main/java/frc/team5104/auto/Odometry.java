package frc.team5104.auto;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import frc.team5104.subsystems.Drive;
import frc.team5104.util.Units;
import frc.team5104.util.console;
import frc.team5104.util.console.c;

/**
 * A wrapper class for keeping track of the the robots position.
 * Gets called from AutoManager.java for updating
 * and initializing/resetings from Robot.java
 */
public class Odometry {
	private static DifferentialDriveOdometry odometry;
	
	public static void init() {
		Drive.reset();
		odometry = new DifferentialDriveOdometry(new Rotation2d());
		console.log(c.AUTO, "Initialized Odometry");
	}
	
	public static void update() {
		if (odometry != null) {
			odometry.update(
				Rotation2d.fromDegrees(-Drive.getGyro()),
				Drive.getLeftEncoder().getPositionMeters(),
				Drive.getRightEncoder().getPositionMeters()
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
				Drive.getLeftEncoder().getVelocityMetersSecond(),
				Drive.getRightEncoder().getVelocityMetersSecond()
		);
	}
	
	public static void resetHold() {
		if (odometry != null) {
			reset();
			try { Thread.sleep(10); } catch (Exception e) { }
			reset();
			console.log(c.AUTO, "Reset Odometry at " + getPose2dMeters());
		}
	}
	
	public static void reset() {
		if (odometry != null) {
			Drive.reset();
			odometry.resetPosition(new Pose2d(), Rotation2d.fromDegrees(-Drive.getGyro()));
		}
	}
}