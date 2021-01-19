/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util;

/**
 * A deadband cuts out areas of an input. 
 * For example in a clipping deadband with a radius of .05, .05 would go to 0 and .06 would not change.
 * This class has two deadbands. 
 *  - Clipping: in which areas will be directly cut out (r=.05: .05->0, .06->.06)
 *  - Slope Adjustment: in which the slope is adjusted (r=.05: .05->0, 0.06->0.01)
 * Desmos Link: https://www.desmos.com/calculator/xhbilptzt9
 */
public class Deadband {
	//Deadband Types
	/**
	 * Clipping: in which areas will be directly cut out (r=.05: .05->0, .06->.06)
	 * Slope Adjustment: in which the slope is adjusted (r=.05: .05->0, 0.06->0.01)
	 */
	public static enum DeadbandType {
		CLIPPING,
		SLOPE_ADJ,
		NONE
	};
	
	//Main Getter Function
	/**
	 * Processes a deadband upon "x" at "radius" distance.
	 * @param x The input value
	 * @param radius The size of the deadband
	 * @param type The type of deadband (clipping or slope adj)
	 */
	public static double get(double x, double radius, DeadbandType type) {
		//Call function for specified deadband and send it abs(x), then undo the abs(x)
		if (type == DeadbandType.CLIPPING)
			return getClipping(Math.abs(x), radius) * (x > 0 ? 1 : -1);
		if (type == DeadbandType.SLOPE_ADJ)
			return getSlopeAdjustment(Math.abs(x), radius) * (x > 0 ? 1 : -1);
		else return x;
	}
	
	//Deadband Processors
	private static double getSlopeAdjustment(double x, double radius) {
		double m = 1 / (1-radius);
		double b = -m*radius;
		if (x <= radius)
			return 0;
		else
			return m*x + b;
	}
	private static double getClipping(double x, double radius) {
		if (x <= radius)
			return 0;
		else
			return x;
	}
	
	//Saved Deadband
	double savedRadius;
	DeadbandType savedType;
	public Deadband() { this(0, DeadbandType.NONE); }
	public Deadband(double radius) { this(radius, DeadbandType.SLOPE_ADJ); }
	public Deadband(double radius, DeadbandType type) {
		this.savedRadius = radius;
		this.savedType = type;
	}
	public double get(double x) {
		return get(x, this.savedRadius, this.savedType);
	}
}