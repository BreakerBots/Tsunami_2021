package com.team5104.frc2021.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.team5104.frc2021.Constants;
import com.team5104.frc2021.Ports;
import com.team5104.frc2021.Superstructure;
import com.team5104.frc2021.Superstructure.FlywheelState;
import com.team5104.lib.MovingAverage;
import com.team5104.lib.Util;
import com.team5104.lib.control.VelocityController;
import com.team5104.lib.managers.Subsystem;
import com.team5104.lib.sensors.Encoder.FalconEncoder;
import com.team5104.lib.webapp.Tuner;

public class Flywheel extends Subsystem {
	private static TalonFX motor1, motor2;
	private static FalconEncoder encoder;
	private static MovingAverage avgRPM;
	private static VelocityController controller;
	
	//Loop
	public void update() {
		if (Superstructure.isEnabled() && Superstructure.is(FlywheelState.SPINNING)) {
			setRampRate(Constants.flywheel.RAMP_RATE_UP);
			if (Constants.flywheel.OPEN_LOOP)
				setPercentOutput(1.0);
			else setSpeed(getTargetRPM());
		}
		else if (isCharacterizing()) {
			setRampRate(Constants.flywheel.RAMP_RATE_UP);
			//do nothing
		}
		else {
			setRampRate(Constants.flywheel.RAMP_RATE_DOWN);
			stop();
		}
		
		avgRPM.update(getRPM());
	}

	//Debugging
	public void debug() {
		Tuner.setTunerOutput("Flywheel RPM", getRPM());
		Tuner.setTunerOutput("Flywheel ARPM", getAvgRPM());
		Tuner.setTunerOutput("Flywheel FF", controller.getLastFFOutput());
		Tuner.setTunerOutput("Flywheel PID", controller.getLastPIDOutput());
		Tuner.setTunerOutput("Flywheel Out", controller.getLastOutput());
		Tuner.setTunerOutput("Flywheel Avg Sped Up", isSpedUp());
		Tuner.setTunerOutput("Flywheel Current 1", motor1.getSupplyCurrent());
		Tuner.setTunerOutput("Flywheel Current 2", motor2.getSupplyCurrent());
		Tuner.setTunerOutput("Flywheel Voltage 1", motor1.getMotorOutputVoltage());
		Tuner.setTunerOutput("Flywheel Voltage 2", motor2.getMotorOutputVoltage());
		Constants.flywheel.KP = Tuner.getTunerInputDouble("Flywheel KP", Constants.flywheel.KP);
		Constants.flywheel.KD = Tuner.getTunerInputDouble("Flywheel KD", Constants.flywheel.KD);
		Constants.flywheel.RPM_TOL = Tuner.getTunerInputDouble("Flywheel Tol", Constants.flywheel.RPM_TOL);
		controller.setPID(Constants.flywheel.KP, 0, Constants.flywheel.KD);
	}
	
	//Internal Functions
	private void setSpeed(double rpm) {
		setVoltage(controller.calculate(encoder.getComponentRPS(), rpm / 60d));
	}
	private void setVoltage(double volts) {
		motor1.set(ControlMode.PercentOutput, volts / motor1.getBusVoltage());
	}
	private void setPercentOutput(double percent) {
		motor1.set(ControlMode.PercentOutput, percent);
	}
	private void stop() {
		motor1.set(ControlMode.Disabled, 0);
	}
	private void setRampRate(double rate) {
		motor1.configOpenloopRamp(rate);
		motor2.configOpenloopRamp(rate);
		motor1.configClosedloopRamp(rate);
		motor2.configClosedloopRamp(rate);
	}
	
	//External Functions
	public static double getRPM() {
		return (motor1 == null) ? 0 : encoder.getComponentRPM();
	}
	public static double getAvgRPM() {
		return (motor1 == null) ? 0 : avgRPM.getDoubleOutput();
	}
	public static double getTargetRPM() {
		return (Hood.isTrenchMode()) ? 11000 : 9000;
	}
	public static boolean isSpedUp() {
		if (motor1 == null)
			return true;
		return Util.roughlyEquals(getAvgRPM(), getTargetRPM(), Constants.flywheel.RPM_TOL);
	}
	
	//Config
	public void init() {
		motor1 = new TalonFX(Ports.FLYWHEEL_MOTOR_1);
		motor1.configFactoryDefault();
		motor1.setInverted(false);
		
		motor2 = new TalonFX(Ports.FLYWHEEL_MOTOR_2);
		motor2.configFactoryDefault();
		motor2.follow(motor1);
		motor2.setInverted(true);

		encoder = new FalconEncoder(motor1, Constants.flywheel.GEARING);
		
		setRampRate(Constants.flywheel.RAMP_RATE_UP);
		
		controller = new VelocityController(Constants.flywheel);
		
		avgRPM = new MovingAverage(50, 0);

		configCharacterization(encoder, (double voltage) -> setVoltage(voltage));
	}

	//Reset
	public void disabled() {
		stop();
	}
}
