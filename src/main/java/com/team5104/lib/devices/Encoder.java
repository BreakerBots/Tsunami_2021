package com.team5104.lib.devices;

import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.util.Units;

public abstract class Encoder extends Device {
    public double gearing, ticksPerRev;

    /** @param gearing gearing constant in the form `(driving / driven) * (driving / driven)...` (so 5:1 and 3:2 is (5/1)*(3/2))
     * @param ticksPerRev ticksPerRev constant of the encoder. Subclasses will fill this automatically */
    public Encoder(double gearing, double ticksPerRev) {
        this.gearing = gearing;
        this.ticksPerRev = ticksPerRev;
    }

    //Get
    public abstract double getTicks();

    public abstract double getTicksPer100Ms();

    public double getComponentRevs() {
        return ticksToComponentRevs(getTicks()); //ticks to revs
    }

    public double getComponentRPS() {
        return ticksToComponentRevs(getTicksPer100Ms()) * 10.0; //ticks to rev vel
    }

    public double getComponentRPM() {
        return getComponentRPS() * 60.0;
    }

    public double ticksToComponentRevs(double ticks) {
        return ticks / (gearing * ticksPerRev); //ticks to comp revs
    }

    public double componentRevsToTicks(double componentRevs) {
        return componentRevs * (gearing * ticksPerRev); //revs to ticks
    }

    //Set
    public abstract void setTicks(double ticks);

    public void reset() {
        setTicks(0);
    }

    public void setComponentRevs(double componentRevs) {
        setTicks(componentRevsToTicks(componentRevs));
    }

    //Sub Classes
    private static abstract class CTRE_Encoder extends Encoder {
        BaseTalon motorController;

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

        public Health getHealth() {
            Faults faults = new Faults();
            motorController.getFaults(faults);
            return new Health(faults);
        }

        public void stop() { /* do nothing */ }
    }

    public static class MagEncoder extends CTRE_Encoder {
        public MagEncoder(TalonSRX motorController, double gearing) {
            super(motorController, gearing, 4096);
        }
    }

    public static class FalconEncoder extends CTRE_Encoder {
        public FalconEncoder(TalonFX motorController, double gearing) {
            super(motorController, gearing, 2048);
        }
    }

    public static class EncoderSim extends MagEncoder {
        public EncoderSim(TalonSim motorController, double gearing) {
            super(motorController, gearing);
        }

        public void setTicks(double ticks) {
            ((TalonSRX) motorController).getSimCollection().setQuadratureRawPosition((int) ticks);
        }

        public void setTicksVel(double ticksPer100MsVel) {
            ((TalonSRX) motorController).getSimCollection().setQuadratureVelocity((int) ticksPer100MsVel);
        }

        public void setVelocityAccelMeters(double metersPerSecond, double metersPerSecondSquared, double wheelDiameter) {
            setTicks(componentRevsToTicks(
                    Units.metersToFeet(metersPerSecond) / (wheelDiameter * Math.PI)));
            setTicksVel(componentRevsToTicks(
                    Units.metersToFeet(metersPerSecondSquared) / (wheelDiameter * Math.PI)) / 10d);
        }
    }
}
