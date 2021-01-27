package frc.team5104.util;

import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public abstract class Encoder {
    public double gearing, ticksPerRev;

    public Encoder(double gearing, double ticksPerRev) {
        this.gearing = gearing;
        this.ticksPerRev = ticksPerRev;
    }

    //Get
    public abstract double getTicks();
    public abstract double getTicksPer100Ms();
    public double getComponentRevs() {
        return getTicks() / (gearing * ticksPerRev);
    }
    public double getComponentRPS() {
        return getTicksPer100Ms() / (gearing * ticksPerRev) * 10.0;
    }
    public double getComponentRPM() {
        return getComponentRPS() * 60.0;
    }
    public double componentRevsToTicks(double componentRevs) {
        return componentRevs * (gearing * ticksPerRev);
    }

    //Set
    public abstract void setTicks(double ticks);
    public void reset() {
        setTicks(0);
    }
    public void setComponentRevs(double componentRevs) {
        setTicks(componentRevs * (gearing * ticksPerRev));
    }

    //Sub Classes
    private static abstract class CTRE_Encoder extends Encoder {
        private BaseTalon motorController;
        public CTRE_Encoder(BaseTalon motorController, double gearing, double ticksPerRev) {
            super(gearing, ticksPerRev);
            this.motorController = motorController;
        }

        public double getTicks() {
            return motorController.getSelectedSensorPosition();
        }
        public double getTicksPer100Ms() {
            return motorController.getSelectedSensorVelocity();
        }
        public void setTicks(double ticks) {
            motorController.setSelectedSensorPosition(ticks);
        }
    }

    public static class MagEncoder extends CTRE_Encoder {
        public MagEncoder(TalonSRX motorController, double gearing) {
            super(motorController, gearing, 4096);
        }
    }

    public static class FalconIntegratedEncoder extends CTRE_Encoder {
        public FalconIntegratedEncoder(TalonFX motorController, double gearing) {
            super(motorController, gearing, 2048);
        }
    }
}
