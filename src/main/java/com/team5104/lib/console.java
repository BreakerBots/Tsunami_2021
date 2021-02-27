/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.Timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Handles all robot logging
 * Outputs to the Dashboard (pref) or Driver Station console (System.out) */
public class console {

    // Types
    private enum Type {
        ERROR, INFO, WARN;
    }

    // Logging Methods
    /** Prints out text in a separate thread (lowest priority) and adds prints to a text file (if Console.logFile.isLogging)
     * Examples:
     * 2.12 [Robot]: Message
     * 90.12 ERROR [subsystems.Turret]: Message */
    private static void logBase(int stackCount, Type type, Object... data) {
        StackTraceElement trace = new Throwable().getStackTrace()[stackCount];
        addToBuffer(new Log(
            Timer.getFPGATimestamp(),
            type,
            trace.getClassName(),
            trace.getLineNumber(),
            data
        ));
    }
    /** Prints out text to the console under the type "INFO" and category "OTHER" */
    public static void log(Object... data) { logBase(2, Type.INFO, data); }
    /** Prints out text to the console under the type "ERROR" and category "OTHER" */
    public static void error(Object... data) { logBase(2, Type.ERROR, data); }
    /** Prints out text to the console under the type "WARN" and category "OTHER" */
    public static void warn(Object... data) { logBase(2, Type.WARN, data); }

    // System Logging/Thread/Loop
    private static volatile List<Log> buffer = Collections.synchronizedList(new ArrayList<Log>());
    public static List<Log> readBuffer() {
        return buffer;
    }
    public static void clearBuffer() {
        buffer.clear();
    }
    public static boolean hasBuffer() {
        return buffer.size() > 0;
    }
    public static void addToBuffer(Log line) {
        buffer.add(line);
    }

    // Timing Groups/Sets
    public static class Set {
        // TODO: Plz rewrite
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
        public static void log(String timingGroupName, String a) {
            StringBuilder builder = new StringBuilder();
            builder.append(a);
            builder.append(getTime(timingGroupName));
            builder.append("s");
            console.logBase(2, Type.INFO, builder.toString());
        }
    }

    //Log Object
    @JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
    public static class Log {
        @JsonProperty("timestamp")
        private double timestamp;
        @JsonProperty("type")
        private Type type;
        @JsonProperty("location")
        private String location;
        @JsonProperty("lineNumber")
        private int lineNumber;
        @JsonProperty("data")
        private Object[] data;

        public Log(double timestamp, Type type, String location, int lineNumber, Object[] data) {
            this.timestamp = timestamp;
            this.type = type;
            this.location = location;
            this.lineNumber = lineNumber;
            this.data = data;
        }

        public String getLogString() {
            StringBuilder builder = new StringBuilder();
            builder.append(type.name());
            builder.append(" [");
            builder.append(location);
            builder.append("]: ");
            builder.append(Arrays.toString(data));
            return builder.toString();
        }
    }
}












