/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.Timer;

import java.util.ArrayList;
import java.util.Arrays;
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

        Log log = new Log(
            Timer.getFPGATimestamp(),
            type,
            stackCount + 1,
            data,
            1
        );

        int lastLogIndex = logs.size() - 1;
        if (logs.size() > 0 && logs.get(lastLogIndex).equals(log)) {
            //group and add to counter
            log.count = logs.get(lastLogIndex).count + 1;
            logs.set(lastLogIndex, log);
            if (bufferIndex > lastLogIndex) {
                bufferIndex = lastLogIndex;
            }
        }
        else {
            //add log
            addToLogs(log);
        }
    }
    /** Prints out text to the console under the type "INFO" and category "OTHER" */
    public static void log(Object... data) { logBase(2, Type.INFO, data); }
    /** Prints out text to the console under the type "ERROR" and category "OTHER" */
    public static void error(Object... data) { logBase(2, Type.ERROR, data); }
    /** Prints out text to the console under the type "WARN" and category "OTHER" */
    public static void warn(Object... data) { logBase(2, Type.WARN, data); }

    // System Logging/Thread/Loop
    private static final int MAX_ELEMENTS = 500;
    private volatile static List<Log> logs = new ArrayList<Log>();
    private volatile static int bufferIndex = 0;
    public static void addToLogs(Log log) {
        logs.add(log);
        if (logs.size() > MAX_ELEMENTS) {
            /* cap size to save memory
              @ ~64 bytes per log */
            logs.remove(0);
            if (bufferIndex > 0) {
                bufferIndex--;
            }
        }
    }
    public static List<Log> readBuffer() {
        int logsSize = logs.size();
        if (bufferIndex <= logsSize) {
            int bufferIndexTemp = Util.limitBottom(bufferIndex, 0);
            bufferIndex = logsSize;
            return logs.subList(bufferIndexTemp, logsSize);
        }
        return new ArrayList<Log>();
    }
    public static void resetBuffer() {
        bufferIndex = 0;
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
    }

    //Log Object
    @JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
    public static class Log {
        @JsonProperty("timestamp")
        private double timestamp;
        @JsonProperty("type")
        private Type type;
        @JsonProperty("trace")
        private TraceElement[] trace;
        @JsonProperty("data")
        private Object[] data;
        @JsonProperty("count")
        int count;

        public Log(double timestamp, Type type, int stackCount, Object[] data, int count) {
            this.timestamp = timestamp;
            this.type = type;

            StackTraceElement[] rawTrace = new Throwable().getStackTrace();
            int size = Math.min(rawTrace.length - stackCount, 3);
            this.trace = new TraceElement[size];
            for (int i = 0; i < size; i++) {
                this.trace[i] = new TraceElement(rawTrace[i + stackCount]);
            }

            this.data = data;
            this.count = count;
        }

        public boolean equals(Log log) {
            return this.type == log.type &&
                   this.trace[0].className.equals(log.trace[0].className) &&
                   this.trace[0].lineNumber == log.trace[0].lineNumber;
        }

        public String getLogString() {
            StringBuilder builder = new StringBuilder();
            builder.append(type.name());
            builder.append(" [");
            builder.append(trace[0].className);
            builder.append("]: ");
            builder.append(Arrays.toString(data));
            return builder.toString();
        }
    }
    @JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
    public static class TraceElement {
        @JsonProperty("className")
        private String className;
        @JsonProperty("lineNumber")
        private int lineNumber;

        public TraceElement(StackTraceElement stackTraceElement) {
            if (stackTraceElement == null)
                return;

            this.className = stackTraceElement.getClassName();
            this.lineNumber = stackTraceElement.getLineNumber();
        }
    }
}












