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

    //Rounding
    public static double round(double a, int places) {
        double b = Math.pow(10, places);
        return Math.round(a * b) / b;
    }

    //Angle Bounding

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
}