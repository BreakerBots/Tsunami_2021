/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util;

/**
 * Extra Math Functions :)
 * Stay in school kids.
 */
public class BreakerMath {
	//Number Range Clamping
	public static double clamp(double value, double min, double max) { return min(max(value, max), min); }
	public static double min(double value, double min) { return value < min ? min : value; }
	public static double max(double value, double max) { return value > max ? max : value; }
	
	//Equals
	public static boolean roughlyEquals(double a, double b, double tolerance) {
		return Math.abs(a - b) <= tolerance;
	}
	public static boolean roughlyEquals(int a, int b, double tolerance) {
		return Math.abs(a - b) <= tolerance;
	}
	
	//Range
	public static boolean inRange(double a, double mag) {
        return inRange(a, -mag, mag);
    }
	public static boolean inRange(double a, double min, double max) {
        return a > min && a < max;
    }
	
	//Rounding
	public static double round(double a, int places) {
		double b = Math.pow(10, places);
		return Math.round(a * b) / b;
	}
	
	//Angle Difference
	/** Find the difference between two angles (in radians). Accounts for overflow. */
	public static double radianDiff(double from, double to) {
		return boundRadiansPI(to - from);
	}
	/** Find the difference between two angles (in degrees). Accounts for overflow. */
	public static double degreeDiff(double from, double to) {
		return boundDegrees180(to - from);
	}

	//Angle Bounding
	/** Bounds an angle between 0d and 360d */
	public static double boundDegrees360(double angle) {
		while (angle >= 360.0) {
			angle -= 360.0;
		}
		while (angle < 0.0) {
			angle += 360.0;
		}
		return angle;
	}
	/** Bounds an angle between -180d to 180d */
	public static double boundDegrees180(double angle) {
		while (angle >= 180.0) {
			angle -= 360.0;
		}
		while (angle < -180.0) {
			angle += 360.0;
		}
		return angle;
	}
	/** Bounds an angle between 0 radians and 2PI radians */
	public static double boundRadians2PI(double angle) {
		while (angle >= 2.0 * Math.PI) {
			angle -= 2.0 * Math.PI;
		}
		while (angle < 0.0) {
			angle += 2.0 * Math.PI;
		}
		return angle;
	}
	/** Bounds an angle between -PI radians and PI radians */
	public static double boundRadiansPI(double angle) {
		while (angle >= Math.PI)
			angle -= 2.0 * Math.PI;
		while (angle < -Math.PI)
			angle += 2.0 * Math.PI;
		return angle;
	}
}