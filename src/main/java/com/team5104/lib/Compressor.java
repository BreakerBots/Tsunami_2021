package com.team5104.lib;

/** Yeah idk why i exist either */
public class Compressor {
  private static edu.wpi.first.wpilibj.Compressor compressor = new edu.wpi.first.wpilibj.Compressor();

  public static void stop() {
    if (compressor != null)
      compressor.stop();
  }

  public static void start() {
    if (compressor != null)
      compressor.start();
  }

  public static boolean isRunning() {
    return (compressor != null) ? compressor.enabled() : false;
  }

}

