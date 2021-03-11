/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib;

import edu.wpi.first.wpiutil.math.MathUtil;

/** Random Util Stuff :l */
public class  Util {
    //Limit
    /** Limits the top and bottom of a number so
     * limit(2, 0, 1) -> 1, limit(-1, 0, 1) -> 0 */
    public static double limit(double value, double min, double max) {
        return limitBottom(limitTop(value, max), min);
    }
    /** Limits the bottom of a number so
     * limit(-1, 0) -> 0 */
    public static double limitBottom(double value, double min) {
        return value < min ? min : value;
    }
    /** Limits the top of a number so
     * limit(2, 1) -> 1 */
    public static double limitTop(double value, double max) {
        return value > max ? max : value;
    }
    /** Limits the top and bottom of a number so
     * limit(2, 0, 1) -> 1, limit(-1, 0, 1) -> 0 */
    public static int limit(int value, int min, int max) {
        return limitBottom(limitTop(value, max), min);
    }
    /** Limits the bottom of a number so
     * limit(-1, 0) -> 0 */
    public static int limitBottom(int value, int min) {
        return value < min ? min : value;
    }
    /** Limits the top of a number so
     * limit(2, 1) -> 1 */
    public static int limitTop(int value, int max) {
        return value > max ? max : value;
    }

    //Roughly Equals
    /** Whether `a` roughly equals `b` within a tolerance
     * `difference <= tolerance` */
    public static boolean roughlyEquals(double a, double b, double tolerance) {
        return Math.abs(a - b) <= tolerance;
    }
    /** Whether `a` roughly equals `b` within a tolerance
     * `difference <= tolerance` */
    public static boolean roughlyEquals(int a, int b, int tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    //Rounding
    /** Rounds a number to a certain number of decimal places
     * so round(0.12345, 2) -> 0.12 */
    public static double round(double a, int places) {
        double b = Math.pow(10, places);
        return Math.round(a * b) / b;
    }

    //Angle Wrapping
    /** Wraps an angle between -180 and 180
     * so 190 -> -170, 200 -> -160... */
    public static double wrap180(double degrees) {
        return MathUtil.inputModulus(degrees, -180, 180);
    }
}
