/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib;

import edu.wpi.first.wpiutil.math.MathUtil;

/**
 * Extra Math Functions :)
 * Stay in school kids.
 */
public class  Util {
    public static double limit(double value, double min, double max) { return limitBottom(limitTop(value, max), min); }
    public static double limitBottom(double value, double min) { return value < min ? min : value; }
    public static double limitTop(double value, double max) { return value > max ? max : value; }

    //Roughly Equals
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

    //Angle Rapping
    public static double wrap180(double degrees) {
        return MathUtil.inputModulus(degrees, -180, 180);
    }
}
