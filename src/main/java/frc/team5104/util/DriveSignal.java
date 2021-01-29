/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util;

/** A simple class for sending/saving drive-train movement signals. */
public class DriveSignal {
    public enum DriveUnit {VOLTAGE, STOP}

    public double leftSpeed, rightSpeed;
    public DriveUnit unit;

    public DriveSignal() {
        this(0, 0, DriveUnit.STOP);
    }

    public DriveSignal(double leftSpeed, double rightSpeed, DriveUnit unit) {
        this.leftSpeed = leftSpeed;
        this.rightSpeed = rightSpeed;
        this.unit = unit;
    }

    public String toString() {
        return "l: " + leftSpeed + ", r: " + rightSpeed + ", unit: " + unit;
    }
}