package com.team5104.lib;

import com.team5104.lib.setup.RobotState;
import edu.wpi.first.hal.NotifierJNI;

import java.util.ArrayList;

/** Manages all the loops/threads running on the robot and tries to figure out wtf happened when they crash */
public class Looper {
  private static ArrayList<Loop> loops = new ArrayList();

  /** Registers a loop with the looper
   * @note DO THIS TO ALL LOOPS PLZ */
  public static void registerLoop(Loop loop) {
    loops.add(loop);
  }

  /** Attaches a runnable to a thread, to be called periodically.
   * @warn if the thread does not exist or is not attachable an error will
   * be thrown via console.error */
  public static void attach(Runnable runnable, String loopName) {
    for (Loop loop : loops) {
      if (loop.name.equals(loopName) && loop.getClass() == TimedLoop.class) {
        ((TimedLoop) loop).attach(runnable);
      }
    }
  }

  /** Locates a loop based on a given thread.
   * @warn returns null if no loop is found */
  public static Loop getLoop(Thread thread) {
    for (Loop loop : loops) {
      if (loop.thread == thread)
        return loop;
    }
    return null;
  }

  /** Nukes the entire world lol */
  public static void killAll() {
    for (Loop loop : loops) {
      try {
        if (loop.getClass() == TimedLoop.class)
          ((TimedLoop) loop).kill();
        else loop.thread.interrupt();
      } catch (Exception e) { CrashHandler.log(e); }
    }
  }

  // -- Loop Types
  /** The loop base class. Use this class if you handle the loop updating yourself, otherwise
   * there are options below this class to handle updating, attaching, etc. This class
   * will set the thread name and priority.*/
  public static class Loop {
    Thread thread;
    String name;
    int priority;

    /** Registers a loop
     * @param name the name of the loop/thread
     * @param thread the thread the loop is on
     * @param priority 1-10 thread priority */
    public Loop(String name, Thread thread, int priority) {
      this.name = name;
      this.thread = thread;
      this.priority = priority;

      if (thread != null) {
        thread.setPriority(priority);
        thread.setName(name);
      }
    }

    /** Creates a new thread, runs it, and registers it as a loop
     * @param name the name of the loop/thread
     * @param runnable code to run in the thread
     * @param priority 1-10 thread priority */
    public Loop(String name, Runnable runnable, int priority) {
      this(name, new Thread(() -> {
        try {
          runnable.run();
        } catch (Exception e) { CrashHandler.log(e); }
      }), priority);
      thread.start();
    }
  }
  /** Handles the creation of a thread and periodic updating of it. Then runnables can
   * attached to the thread to be updated in the loop. */
  public static class TimedLoop extends Loop {
    private final int notifier = NotifierJNI.initializeNotifier();
    private ArrayList<Runnable> runnables = new ArrayList<>();
    private double period, expirationTime; //ms

    /** @param name the name of the loop
     * @param priority 1-10 thread priority
     * @param period loop period in ms */
    public TimedLoop(String name, int priority, double period) {
      super(name, null, priority);
      this.period = period;

      NotifierJNI.setNotifierName(notifier, name);
      expirationTime = RobotState.getFPGATimestamp() + period;

      thread = new Thread(() -> {
        while (true) {
          NotifierJNI.updateNotifierAlarm(notifier, (long) (expirationTime * 1e3d));
          long curTime = NotifierJNI.waitForNotifierAlarm(notifier);
          if (curTime == 0)
            break;

          try {
            for (Runnable runnable : runnables) {
              runnable.run();
            }
          } catch (Exception e) { CrashHandler.log(e); }

          expirationTime += period;
        }
      });
      thread.start();
      thread.setPriority(priority);
      thread.setName(name);
    }

    /** @param name the name of the loop
     * @param runnable a runnable to attach
     * @param priority 1-10 thread priority
     * @param period loop period in ms */
    public TimedLoop(String name, Runnable runnable, int priority, double period) {
      this(name, priority, period);
      attach(runnable);
    }

    public void kill() {
      NotifierJNI.stopNotifier(notifier);
      NotifierJNI.cleanNotifier(notifier);
      thread.interrupt();
    }

    public void attach(Runnable runnable) {
      runnables.add(runnable);
    }
  }
}
