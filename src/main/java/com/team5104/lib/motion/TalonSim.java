package com.team5104.lib.motion;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.RobotController;

public class TalonSim extends WPI_TalonSRX {
    public TalonSim(int deviceNumber) {
        super(deviceNumber);
    }

    private static double random(double min, double max) {
        return (max - min) / 2 * Math.sin(Math.IEEEremainder(Math.random(), 2 * 3.14159)) + (max + min) / 2;
    }

    public void update() {
        double supplyCurrent = Math.abs(getMotorOutputPercent()) * 30 * random(0.95, 1.05);
        getSimCollection().setSupplyCurrent(supplyCurrent);
        getSimCollection().setStatorCurrent(getMotorOutputPercent() == 0 ? 0 : supplyCurrent / Math.abs(getMotorOutputPercent()));
        getSimCollection().setBusVoltage(RobotController.getBatteryVoltage());
    }
}
