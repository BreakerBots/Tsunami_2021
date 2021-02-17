/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import com.team5104.lib.Looper.TimedLoop;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * <h1>Console</h1>
 * A class for handling logging/printing
 */
public class console {

    public static final boolean OVERWRITE_NON_MATCH_LOGS = true;
    public static final boolean OVERWRITE_MATCH_LOGS = false;

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
        String location = new Throwable().getStackTrace()[stackCount].getClassName();
        if (location.indexOf("frc.team5104.") != -1)
            location = location.substring("frc.team5104.".length());
        String f = round(Timer.getFPGATimestamp(), 2) + ": " + type.message + "[" + location + "]: " + parseAndRound(2, data);
        addToPrintBuffer(f);
        if (logFile.isLogging)
            logFile.log += f + "\n";
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
        String returnString = "";
        for (int dataIndex = 0; dataIndex < data.length; dataIndex++) {
            returnString += data[dataIndex] + (dataIndex != (data.length - 1) ? ", " : "");
        }
        return returnString;
    }

    /** Rounds all doubles, then parses multiple objects into one string. The same function as used when normally calling console.log(). */
    public static String parseAndRound(int decimalPlaces, Object... data) {
        String returnString = "";
        for (int dataIndex = 0; dataIndex < data.length; dataIndex++) {
            if (data[dataIndex] instanceof Double)
                returnString += round(((Double) data[dataIndex]).doubleValue(), decimalPlaces);
            else returnString += data[dataIndex];
            returnString += (dataIndex != (data.length - 1) ? ", " : "");
        }
        return returnString;
    }

    /** Parts a list of objects into a CC (colon-comma) pattern. Output will be "v1: v2, v3: v4..." Rounds all doubles to 2 decimal places */
    public static String parseCCPattern(Object... data) {
        String returnString = "";
        for (int dataIndex = 0; dataIndex < data.length; dataIndex++) {
            if (data[dataIndex] instanceof Double)
                returnString += round(((Double) data[dataIndex]).doubleValue(), 2);
            else returnString += data[dataIndex];
            returnString += (dataIndex % 2 == 0) ? (": ") : (dataIndex != (data.length - 1) ? ", " : "");
        }
        return returnString;
    }

    // -- Rounding

    /** Rounds a number to 2 decimal places */
    public static String round(double number) { return round(number, 2); }

    /** Rounds a number to N decimal places */
    public static String round(double number, int decimalPlaces) {
        return String.format("%." + ((int) Util.clamp(decimalPlaces, 0, 10)) + "f", number);
    }

    // -- System Logging/Thread/Loop
    private static ArrayList<String> buffer = new ArrayList();
    public static void init() {
        buffer.clear();
        Looper.registerLoop(new TimedLoop("Console", 1, 100));
        Looper.attach(() -> {
            for (String line : buffer)
                System.out.println(line);
            buffer.clear();
        }, "Console");
    }
    public static void addToPrintBuffer(String line) {
        buffer.add(line);
    }

    // -- Timing Groups/Sets
    public static class sets {
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

    //  --  File Logging
    public static class logFile {
        private static String log = "";
        public static boolean isLogging = false;

        /**
         * Starts Recording the "console.logs" into a string that can be saved with "console.logFile.end()"
         * If the logger is already logging it will not clear the log, but just do nothing
         */
        public static void start() {
            if (!isLogging) {
                isLogging = true;
                log = "";
            }
        }

        /**
         * Saves the log string that is logged onto after calling "console.logFile.start()"
         * Match => "MatchLog" if Constants.SaveMatchLogs
         * Other => "GeneralLog" if Constants.SaveNonMatchLogs
         */
        public static void end() {
            try {
                if (isLogging) {
                    isLogging = false;
                    boolean hasFMS = DriverStation.getInstance().isFMSAttached();

                    //File Path
                    String filePath = "/home/lvuser/" + (hasFMS ? "MatchLog/" : "GeneralLog/");
                    String fileName;

                    //File Name
                    if (hasFMS ? OVERWRITE_MATCH_LOGS : OVERWRITE_NON_MATCH_LOGS)
                        fileName = "log.txt";
                    else
                        fileName = DateTimeFormatter.ofPattern("MM-dd-yyyy_HH-mm").format(LocalDateTime.now()) + ".txt";

                    File directory = new File(filePath);
                    if (!directory.exists())
                        directory.mkdir();

                    //Save File
                    PrintWriter writer = new PrintWriter(filePath + fileName, StandardCharsets.UTF_8);
                    writer.print(log);
                    writer.close();
                }
            } catch (Exception e) { console.error(e); }
        }
    }

    // -- Timed Printing
    public static class timedPrinter {

        private static final ArrayList<TimedPrinter> printers = new ArrayList<TimedPrinter>();

        private static void create(String printerId, int deltaLoops) {
            printers.add(new TimedPrinter(printerId, deltaLoops));
        }

        private static TimedPrinter get(String printerId, int deltaLoops) {
            printerId = printerId.toLowerCase();
            for (TimedPrinter printer : printers) {
                if (printer.id.equals(printerId))
                    return printer;
            }
            create(printerId, deltaLoops);
            return get(printerId, deltaLoops);
        }

        /**
         * Returns if you should print that loop
         *
         * @param printerId  A string identifier for the printer. If it doesnt exist it will make it.
         * @param deltaLoops
         */
        public static boolean shouldPrint(String printerId, int deltaLoops) {
            return get(printerId, deltaLoops).get();
        }

        private static class TimedPrinter {
            String id;
            int deltaLoops;
            int i = 1;

            public TimedPrinter(String id, int deltaLoops) {
                this.id = id;
                this.deltaLoops = deltaLoops;
            }

            public boolean get() {
                i++;
                if (i > deltaLoops) {
                    i = 1;
                    return true;
                }
                return false;
            }
        }
    }
}












