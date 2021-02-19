package com.team5104.lib;

import com.team5104.lib.setup.RobotState;
import java.util.Arrays;

public class ConstantsUtils {
  // Robot Name Manager
  public static class RobotNameManager {
    public final String name;
    public final int id;

    /**
     * @param robotNames List out all the robot names this code is targeted for. The first name is
     *     considered the "competition robot"
     */
    public RobotNameManager(String... robotNames) {
      if (RobotState.isSimulation()) name = robotNames[0];
      else name = Filer.readFile(Filer.HOME_PATH + "robot.txt");
      id = Arrays.asList(robotNames).indexOf(name);
      if (id == -1) console.error("Invalid Robot Name!");
    }

    /** Returns a value that corresponds to the robotName index */
    public double switchOnBot(double... out) {
      return out[id];
    }

    /** Returns a value that corresponds to the robotName index */
    public boolean switchOnBot(boolean... out) {
      return out[id];
    }

    /** Returns a value that corresponds to the robotName index */
    public String switchOnBot(String... out) {
      return out[id];
    }

    /** Returns a value that corresponds to the robotName index */
    public int switchOnBot(int... out) {
      return out[id];
    }

    /** Returns a value that corresponds to the robotName index */
    public double switchOnBotArray(double[] out) {
      return out[id];
    }

    /** Returns a value that corresponds to the robotName index */
    public int switchOnBotArray(int[] out) {
      return out[id];
    }
  }

  // Subsystem Constants
  public static class SubsystemConstants {
    public double KP, KI, KD, KS, KV, KA, MAX_VEL, MAX_ACC, GEARING;

    /**
     * @param kP proportional constant for PID (use frc-characterization)
     * @param kI integral constant PID (use frc-characterization)
     * @param kD derivative constant for PID (use frc-characterization)
     * @param kS minimum voltage to move this mechanism (use frc-characterization)
     * @param kV voltage required to reach a specific velocity (use frc-characterization)
     * @param kA voltage required to reach a specific acceleration (use frc-characterization)
     * @param maxVelocity the max velocity this mechanism should move at
     * @param maxAccel the max acceleration this mechanism should move at
     * @param gearing the gearing from the motor to the final output
     */
    public SubsystemConstants(
        double kP,
        double kI,
        double kD,
        double kS,
        double kV,
        double kA,
        double maxVelocity,
        double maxAccel,
        double gearing) {
      this.KP = kP;
      this.KI = kI;
      this.KD = kD;
      this.KS = kS;
      this.KV = kV;
      this.KA = kA;
      this.MAX_VEL = maxVelocity;
      this.MAX_ACC = maxAccel;
      this.GEARING = gearing;
    }
  }

  // Servo Constants
  public static class ServoConstants {
    public double KP, KI, KD, KS, KV, KA, MAX_VEL, MAX_ACC, GEARING;
  }
}
