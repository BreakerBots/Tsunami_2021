package com.team5104.lib.devices;

import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.util.Units;

/** Manages an encoder, including many unit conversions */
public abstract class Encoder extends Device {
    public double gearing, ticksPerEncoderRev;

    /** @param gearing gearing constant to convert from encoder revs to comp. revs
     * use: `(driving / driven) * (driving / driven)...` (so 5:1 and 3:2 is (5/1)*(3/2))
     * @param ticksPerEncoderRev ticksPerRev constant of the encoder. Subclasses will fill this automatically */
    public Encoder(double gearing, double ticksPerEncoderRev) {
        this.gearing = gearing;
        this.ticksPerEncoderRev = ticksPerEncoderRev;
    }

    //Get
    /** returns the encoder ticks */
    public abstract double getTicks();

    /** returns the encoder ticks per seconds */
    public abstract double getTicksPerSecond();

    /** returns the component units */
    public double getComponentUnits() {
        return ticksToComponentUnits(getTicks());
    }

    /** returns the component units per second */
    public double getComponentUPS() {
        return ticksToComponentUnits(getTicksPerSecond()); //ticks to rev vel
    }

    /** returns the component units per minute */
    public double getComponentUPM() {
        return getComponentUPS() * 60.0;
    }

    /** converts encoder ticks to component units */
    public double ticksToComponentUnits(double ticks) {
        return ticks / (gearing * ticksPerEncoderRev);
    }

    /** converts component units to encoder ticks */
    public double componentUnitsToTicks(double componentRevs) {
        return componentRevs * (gearing * ticksPerEncoderRev);
    }

    //Set
    /** sets the encoder ticks to a specified value */
    public abstract void setTicks(double ticks);

    /** sets the encoder value to 0 */
    public void reset() {
        setTicks(0);
    }

    /** sets the encoder value to a specified component unit */
    public void setComponentUnits(double componentUnits) {
        setTicks(componentUnitsToTicks(componentUnits));
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

        public double getTicksPerSecond() {
            return motorController.getSelectedSensorVelocity() * 10.0;
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
            setTicks(componentUnitsToTicks(
                    Units.metersToFeet(metersPerSecond) / (wheelDiameter * Math.PI)));
            setTicksVel(componentUnitsToTicks(
                    Units.metersToFeet(metersPerSecondSquared) / (wheelDiameter * Math.PI)) / 10d);
        }
    }
}
