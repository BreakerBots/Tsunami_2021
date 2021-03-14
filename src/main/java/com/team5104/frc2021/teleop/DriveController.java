/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.frc2021.teleop;

import com.team5104.frc2021.Controls;
import com.team5104.frc2021.subsystems.Drive;
import com.team5104.lib.teleop.TeleopController;

public class DriveController extends TeleopController {
  private static final double MIN_SPEED_FORWARD = 0.055; //volts
  private static final double MIN_SPEED_TURN = 0.055; //volts
  private static final double KICKSTAND_SCALAR = 0.4; //percent
  private static final double TURN_SPEED_ADJ = 0.2; //percent - reduces turning bases on forward velocity
  private boolean kickstand = false;

  //Loop
  protected void update() {
    //get forward input
    double forward = Controls.DRIVE_FORWARD.get() - Controls.DRIVE_REVERSE.get();

    //reduce the turning speed based on forward speed
    Controls.DRIVE_TURN.changeCurveX1((1 - Math.abs(forward)) * (1 - TURN_SPEED_ADJ) + TURN_SPEED_ADJ);

    //get turn input
    double turn = Controls.DRIVE_TURN.get();

    //swap the turn if driving in reverse
//    if (forward < 0) {
//      turn *= -1;
//    }

    //make drive signal
    DriveSignal signal = new DriveSignal(
        (forward + turn) * 12,
        (forward - turn) * 12
    );

    //kickstand
    if (Controls.DRIVE_KICKSTAND.get()) {
      kickstand = !kickstand;
    }
    if (kickstand) {
      signal.leftVolts *= KICKSTAND_SCALAR;
      signal.rightVolts *= KICKSTAND_SCALAR;
    }

    //min-speed
    applyMinSpeed(signal);

    //send drivetrain command
    Drive.set(signal);
  }

  //Methods
  private static void applyMinSpeed(DriveSignal signal) {
    double forward = (signal.leftVolts + signal.rightVolts) / 2.0d; //volts -12 to 12
    double turn = signal.leftVolts - forward; //volts -12 to 12

    //min speed volts 0-12
    double combined = Math.abs(forward) + Math.abs(turn);
    double minSpeed = Math.abs(forward) / combined * MIN_SPEED_FORWARD +
                      Math.abs(turn) / combined * MIN_SPEED_TURN;

    //slope 0-12
    double slope = (12.0d - minSpeed) / 12.0d;

    //add in min speed, but also angle the voltage slope down to account for min speed
    if (signal.leftVolts != 0) {
      signal.leftVolts = (signal.leftVolts * slope) + (minSpeed * Math.signum(signal.leftVolts));
    }
    if (signal.rightVolts != 0) {
      signal.rightVolts = (signal.rightVolts * slope) + (minSpeed * Math.signum(signal.rightVolts));
    }
  }

  //Drive Signal
  /** A simple class for sending/saving drive-train movement signals. */
  public static class DriveSignal {
      public enum DriveMode {
        DRIVING, STOPPED
      }

      public double leftVolts, rightVolts;
      public DriveMode mode;

      public DriveSignal() {
          this.mode = DriveMode.STOPPED;
      }

      public DriveSignal(double leftVolts, double rightVolts) {
          this.leftVolts = leftVolts;
          this.rightVolts = rightVolts;
          this.mode = DriveMode.DRIVING;
      }

      public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("lv: ");
        builder.append(leftVolts);
        builder.append(", rv: ");
        builder.append(rightVolts);
        builder.append(", unit: ");
        builder.append(mode);
        return builder.toString();
      }
  }
}
