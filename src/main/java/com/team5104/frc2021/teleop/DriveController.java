/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.frc2021.teleop;

import com.team5104.frc2021.Controls;
import com.team5104.frc2021.subsystems.Drive;
import com.team5104.frc2021.teleop.DriveController.DriveSignal.DriveUnit;
import com.team5104.lib.teleop.TeleopController;

public class DriveController extends TeleopController {
  private static final double MIN_SPEED_FORWARD = 0.055; //volts
  private static final double MIN_SPEED_TURN = 0.055; //volts
  private static final double KICKSTAND_SCALAR = 0.2; //percent
  private static final double TURN_SPEED_ADJ = 0.2; //percent - reduces turning bases on forward velocity
  private boolean kickstand = false;

  //Loop
  protected void update() {
    if (Controls.DRIVE_KICKSTAND.get())
      kickstand = !kickstand;

    double forward = Controls.DRIVE_FORWARD.get() - Controls.DRIVE_REVERSE.get();
    Controls.DRIVE_TURN.changeCurveX1((1 - Math.abs(forward)) * (1 - TURN_SPEED_ADJ) + TURN_SPEED_ADJ);
    double turn = Controls.DRIVE_TURN.get();
    Drive.set(get(turn, forward, kickstand));
  }

  //Methods
  /** Forward Kinematics with added control features
   * @param turn percent
   * @param forward percent
   * @param kickstand
   * @return DriveSignal in volts */
  public static DriveSignal get(double turn, double forward, boolean kickstand) {
    DriveSignal signal = new DriveSignal(
        (forward + turn) * 12,
        (forward - turn) * 12,
        DriveUnit.VOLTAGE
    );
    if (kickstand) {
      signal.leftSpeed *= KICKSTAND_SCALAR;
      signal.rightSpeed *= KICKSTAND_SCALAR;
    }
    signal = applyMinSpeed(signal);
    return signal;
  }
  private static DriveSignal applyMinSpeed(DriveSignal signal) {
    double leftPercent = signal.leftSpeed / 12.0d;
    double rightPercent = signal.rightSpeed / 12.0d;
    double turn = Math.abs(leftPercent - rightPercent) / 2.0d;
    double biggerMax = (Math.abs(leftPercent) > Math.abs(rightPercent) ? Math.abs(leftPercent) : Math.abs(rightPercent));
    if (biggerMax != 0)
      turn = Math.abs(turn / biggerMax);
    double forward = 1 - turn;

    double minSpeed;
    minSpeed = (forward * (MIN_SPEED_FORWARD / 12.0d)) + (turn * (MIN_SPEED_TURN / 12.0d));

    if (leftPercent != 0)
      signal.leftSpeed = signal.leftSpeed * (1 - minSpeed) + (leftPercent > 0 ? minSpeed : -minSpeed);
    if (rightPercent != 0)
      signal.rightSpeed = signal.rightSpeed * (1 - minSpeed) + (rightPercent > 0 ? minSpeed : -minSpeed);

    return signal;
  }

  //Drive Signal
  /** A simple class for sending/saving drive-train movement signals. */
  public static class DriveSignal {
      public enum DriveUnit {VOLTAGE, STOP}

      public double leftSpeed, rightSpeed;
      public DriveUnit unit;

      public DriveSignal() {
          this(0, 0, DriveUnit.STOP);
      }

      public DriveSignal(double leftSpeed, double rightSpeed, DriveUnit unit) {
          this.leftSpeed = leftSpeed;
          this.rightSpeed = rightSpeed;
          this.unit = unit;
      }

      public String toString() {
          return "l: " + leftSpeed + ", r: " + rightSpeed + ", unit: " + unit;
      }
  }
}
