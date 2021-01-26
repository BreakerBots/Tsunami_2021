package frc.team5104.util;

import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.simulation.AnalogGyroSim;

public abstract class Gyro {
    public abstract double get();
    public abstract void set(double degrees);
    public void reset() {
        set(0);
    }

    public static class GyroPigeon extends Gyro {
        private PigeonIMU gyro;

        public GyroPigeon(int canID) {
            gyro = new PigeonIMU(canID);
        }

        public double get() {
            return gyro.getFusedHeading();
        }

        public void set(double degrees) {
            gyro.setFusedHeading(degrees);
        }
    }

    public static class GyroSim extends Gyro {
        private AnalogGyro gyro;
        private AnalogGyroSim gyroSim;

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
    }
}