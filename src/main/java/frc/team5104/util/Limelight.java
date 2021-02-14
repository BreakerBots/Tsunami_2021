package frc.team5104.util;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Notifier;
import frc.team5104.util.LatchedBoolean.LatchedBooleanMode;

public class Limelight {
    public static boolean defaultOff;
    private static NetworkTable table;
    private static final LatchedBoolean connected = new LatchedBoolean(LatchedBooleanMode.RISING);
    private static Notifier limelightThread;

    public static NetworkTableEntry getEntry(String key) {
        if (table != null)
            return table.getEntry(key);
        else return null;
    }

    public static void setEntry(String key, double entry) {
        if (table != null)
            getEntry(key).setDouble(entry);
    }

    public static double getDouble(String key, double defaultValue) {
        if (table != null)
            return getEntry(key).getDouble(defaultValue);
        else return defaultValue;
    }

    public static double getTargetX() { return getDouble("tx", 0); }

    public static double getTargetY() { return getDouble("ty", 0); }

    public static double getLatency() { return getDouble("tl", 0); }

    public static boolean hasTarget() { return getDouble("tv", 0) == 1; }

    public static boolean isConnected() { return getDouble("tl", 0) != 0.0; }

    public enum LEDMode {
        OFF(1), ON(3), BLINK(2);
        int value;

        LEDMode(int value) { this.value = value; }
    }

    public static void setLEDMode(LEDMode ledMode) {
        if (isConnected())
            setEntry("ledMode", ledMode.value);
        else console.warn("not connected");
    }

    public static void init(boolean defaultOff) {
        Limelight.defaultOff = defaultOff;
        table = NetworkTableInstance.getDefault().getTable("limelight");
        limelightThread = new Notifier(() -> {
            if (connected.get(isConnected())) {
                if (defaultOff)
                    setLEDMode(LEDMode.OFF);
                else setLEDMode(LEDMode.ON);
                setEntry("camMode", 0);
                setEntry("pipeline", 0);
                setEntry("stream", 0);
                setEntry("snapshot", 0);
                console.log("connected");
            }
        });
        limelightThread.startPeriodic(1);
    }
}