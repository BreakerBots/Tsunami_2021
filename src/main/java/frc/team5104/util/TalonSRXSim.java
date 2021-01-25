package frc.team5104.util;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.RobotController;

public class TalonSRXSim extends WPI_TalonSRX {

    /**
     * Constructor for motor controller
     * @param deviceNumber device ID of motor controller
     */
    public TalonSRXSim(int deviceNumber) {
        super(deviceNumber);
    }

    static double random(double min, double max) {
        return (max - min) / 2 * Math.sin(Math.IEEEremainder(Math.random(), 2 * 3.14159)) + (max + min) / 2;
    }

    public void update() {
        double outPerc = getMotorOutputPercent();

        double supplyCurrent = Math.abs(outPerc) * 30 * random(0.95, 1.05);
        double statorCurrent = outPerc == 0 ? 0 : supplyCurrent / Math.abs(outPerc);

        getSimCollection().setSupplyCurrent(supplyCurrent);
        getSimCollection().setStatorCurrent(statorCurrent);

        getSimCollection().setBusVoltage(RobotController.getBatteryVoltage());
        //getSimCollection().setBusVoltage(12 - outPerc * outPerc * 3/4 * random(0.95, 1.05));
    }
}
