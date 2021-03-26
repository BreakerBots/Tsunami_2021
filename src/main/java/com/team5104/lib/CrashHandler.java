package com.team5104.lib;

import com.team5104.lib.Looper.Loop;

public class CrashHandler {

  //Log Crash
  public static void log(Exception exception) {
    try {
      SavedCrash crash = new SavedCrash(exception);
      console.error(
          crash.getLoopName(),
          "loop crashed!",
          crash.getErrorMessage()
      );
    } catch (Exception e) {
      console.error("fatal error in Crash.log() method");
      e.printStackTrace();
    }
  }

  //Saved Crash
  private static class SavedCrash {
    Loop loop;
    Exception exception;

    /** Creates a crash object and located threadName based on the current loop */
    public SavedCrash(Exception exception) {
      this.loop = Looper.getLoop(Thread.currentThread());
      this.exception = exception;
    }

    //Getters
    public String getLoopName() {
      if (loop == null)
        return "Unknown";
      else return loop.name;
    }
    public String getErrorMessage() {
      StringBuilder builder = new StringBuilder();
      try {
        String message = exception.getMessage();
        if (message == null)
          message = exception.toString();
        builder.append(message);
        builder.append("\n");

        int stackCount = 0;
        StackTraceElement[] rawTrace = exception.getStackTrace();
        int size = rawTrace.length - stackCount;
        for (int i = 0; i < size; i++) {
          StackTraceElement el = rawTrace[i + stackCount];
          builder.append(" @");
          builder.append(el.getClassName().substring(el.getClassName().lastIndexOf(".") + 1));
          builder.append(":");
          builder.append(el.getLineNumber());
          builder.append("\n");
        }
      } catch (Exception e) { console.error("error in SavedCrash.getErrorMessage()", e.getMessage()); }
      return builder.toString();
    }

    //Equals
    public boolean equals(SavedCrash otherCrash) {
      try {
        if (otherCrash == null)
          return false;

        return (
            this.loop == otherCrash.loop &&
            this.exception.toString().equals(otherCrash.exception.toString())
        );
      } catch (Exception e) { return false; }
    }
  }
}

