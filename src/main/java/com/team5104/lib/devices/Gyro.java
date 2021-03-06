package com.team5104.lib.devices;

import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU.CalibrationMode;
import com.team5104.lib.devices.Health.Status;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.simulation.AnalogGyroSim;

public abstract class Gyro extends Device {
  public abstract double get();
  public double getRadians() {
      return get() * (Math.PI / 180d);
  }
  public abstract void set(double degrees);
  public void reset() {
      set(0);
  }
  public abstract void calibrate();
  public void stop() { /* do nothing */ }

  //Sub-Classes
  public static class GyroPigeon extends Gyro {
        private final PigeonIMU gyro;

        public GyroPigeon(int canID) {
            gyro = new PigeonIMU(canID);
        }

        public double get() {
            return gyro.getFusedHeading();
        }

        public void set(double degrees) {
            gyro.setFusedHeading(degrees);
        }

        public void calibrate() { gyro.enterCalibrationMode(CalibrationMode.BootTareGyroAccel); }

        public Health getHealth() {
//          PigeonIMU_StickyFaults faults = new PigeonIMU_StickyFaults();
//          gyro.getStickyFaults(faults); <-- these dont exist yet
          return new Health(Status.GOOD);
        }
    }

    public static class GyroSim extends Gyro {
      private final AnalogGyro gyro;
      private final AnalogGyroSim gyroSim;

      public GyroSim() {
          gyro = new AnalogGyro(1);
          gyroSim = new AnalogGyroSim(gyro);
      }

      public double get() {
          return Math.IEEEremainder(gyro.getAngle(), 360);
      }

      public void set(double degrees) {
          gyroSim.setAngle(degrees);
      }

      public void reset() {
          set(0);
          gyro.reset();
      }

      public void calibrate() { /* do nothing */ }

      public Health getHealth() {
        return new Health(Status.GOOD);
      }
    }
}
