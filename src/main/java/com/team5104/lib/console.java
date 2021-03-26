/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib;

import com.team5104.lib.Looper.TimedLoop;
import edu.wpi.first.wpilibj.Timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * <h1>Console</h1>
 * A class for handling logging/printing
 */
public class console {

    /** Types of Logging */
    private enum Type {
        ERROR("ERROR "), INFO(""), WARNING("WARNING ");
        String message;

        Type(String message) { this.message = message; }
    }

    // -- Logging Methods

    /** Prints out text in a separate thread (lowest priority) and adds prints to a text file (if Console.logFile.isLogging)
     * Examples:
     * 2.12 [Robot]: Message
     * 90.12 ERROR [subsystems.Turret]: Message */
    private static void logBase(int stackCount, Type type, Object... data) {
        StackTraceElement trace = new Throwable().getStackTrace()[stackCount];
        String simpleClassName = trace.getFileName();
        simpleClassName = simpleClassName.substring(0, simpleClassName.indexOf('.'));

        StringBuilder out = new StringBuilder();
        out.append(round(Timer.getFPGATimestamp(), 2));
        out.append(" ");
        out.append(type.message);
        out.append("[");
        out.append(simpleClassName);
        out.append(":");
        out.append(trace.getLineNumber());
        out.append("] ");
        out.append(parseAndRound(2, data));
        addToPrintBuffer(out.toString());
    }
    /** Prints out text to the console under the type "INFO" and category "OTHER" */
    public static void log(Object... data) { logBase(2, Type.INFO, data); }
    /** Prints out text to the console under the type "ERROR" and category "OTHER" */
    public static void error(Object... data) { logBase(2, Type.ERROR, data); }
    /** Prints out text to the console under the type "WARN" and category "OTHER" */
    public static void warn(Object... data) { logBase(2, Type.WARNING, data); }


    // -- Parse

    /** Parses multiple objects into one string (no rounding). The same function as used when normally calling console.log() */
    public static String parse(Object... data) {
        StringBuilder out = new StringBuilder();
        for (int dataIndex = 0; dataIndex < data.length; dataIndex++) {
            out.append(data[dataIndex]);
            if (dataIndex != data.length - 1) {
                out.append(" ");
            }
        }
        return out.toString();
    }

    /** Rounds all doubles, then parses multiple objects into one string. The same function as used when normally calling console.log(). */
    public static String parseAndRound(int decimalPlaces, Object... data) {
        StringBuilder out = new StringBuilder();
        for (int dataIndex = 0; dataIndex < data.length; dataIndex++) {
            if (data[dataIndex] instanceof Double) {
                out.append(round(((Double) data[dataIndex]).doubleValue(), decimalPlaces));
            }
            else out.append(data[dataIndex]);

            if (dataIndex != data.length - 1) {
                out.append(" ");
            }
        }
        return out.toString();
    }

    // -- Rounding

    /** Rounds a number to 2 decimal places */
    public static String round(double number) { return round(number, 2); }

    /** Rounds a number to N decimal places */
    public static String round(double number, int decimalPlaces) {
        return String.format("%." + ((int) Util.limit(decimalPlaces, 0, 10)) + "f", number);
    }

    // -- System Logging/Thread/Loop
    private static volatile List<String> buffer = Collections.synchronizedList(new ArrayList<String>());
    public static void init() {
        Looper.registerLoop(new TimedLoop("Console", () -> {
            try {
                StringBuilder build = new StringBuilder();
                for (Iterator<String> iterator = buffer.iterator(); iterator.hasNext();) {
                    build.append(iterator.next() + "\n");
                }
                buffer.clear();
                System.out.print(build.toString());
            } catch (Exception e) {}
        }, 1, 100));
    }
    public static void addToPrintBuffer(String line) {
        buffer.add(line);
    }

    // -- Timing Groups/Sets
    public static class Set {
        public static final int MaxSets = 10;
        public static String[] sn = new String[MaxSets];
        public static long[] sv = new long[MaxSets];
        public static int si = 0;

        /** Creates a new timing group/set and starts the timer */
        public static void create(String name) {
            if (getIndex(name) != -1) {
                reset(name);
            }
            else if (si < (MaxSets - 1)) {
                sn[si] = name.toLowerCase();
                sv[si] = System.currentTimeMillis();
                si++;
            }
            else
                console.error("Max Amount of Sets Created");
        }

        /** Returns the index of the timing group/set (-1 if not found) */
        public static int getIndex(String name) {
            name = name.toLowerCase();
            for (int i = 0; i < sn.length; i++) {
                if (name.equals(sn[i]))
                    return i;
            }
            return -1;
        }

        /** Resets the time of the corresponding timing group/set */
        public static void reset(String name) {
            int i = getIndex(name);
            if (i != -1) {
                sv[i] = System.currentTimeMillis();
            }
        }

        public enum TimeFormat {Milliseconds, Seconds, Minutes}

        /** Returns the time (in seconds) of the corresponding timing group/set. Returns -1 if nothing found */
        public static double getTime(String name) {
            return getTime(name, TimeFormat.Seconds);
        }

        /** Returns the time (in specified format) of the corresponding timing group/set. Returns -1 if nothing found */
        public static double getTime(String name, TimeFormat format) {
            int i = getIndex(name);
            if (i == -1) return 0;
            else {
                double r = System.currentTimeMillis() - sv[i];
                switch (format) {
                    case Milliseconds:
                        return r;
                    case Minutes:
                        return r / 1000.0 / 60.0;
                    default:
                        return r / 1000.0;
                }
            }
        }

        /** Similar to normal "console.log" with the time of a timing group/set appended
         * @param a               The text to print out
         * @param timingGroupName The name of the timing group/set */
        public static void log(String timingGroupName, Object... a) {
            console.logBase(2, Type.INFO, parse(a) + " " + String.format("%.2f", getTime(timingGroupName)) + "s");
        }
    }
}
