/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util;

/** Some basic unit conversions */
public class Units {
	//Talons
	public static double encoderTicksToDegrees(int ticks) {
		return ticks / 4096.0 * 360.0;
	}
	public static double degreesToEncoderTicks(int degrees) {
		return degrees / 360.0 * 4090.0;
	}
	
	// Feet and Inches
	public static double feetToInches(double feet) {
		return feet * 12.0;
	}
	public static double inchesToFeet(double inches) {
		return inches / 12.0;
	}
	
	
	// Meters and Feet
	public static double metersToFeet(double meters) {
		return meters * 3.28084;
	}
	public static double feetToMeters(double feet) {
		return feet / 3.2808;
	}
	
	// Radians and Degrees
	public static double degreesToRadians(double deg) {
		return Math.toRadians(deg);
	}
	public static double radiansToDegress(double radians) {
		return Math.toDegrees(radians);
	}
}
