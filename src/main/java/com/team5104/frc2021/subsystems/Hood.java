package com.team5104.frc2021.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team5104.frc2021.Constants;
import com.team5104.frc2021.Ports;
import com.team5104.frc2021.Superstructure;
import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.frc2021.Superstructure.Target;
import com.team5104.lib.MovingAverage;
import com.team5104.lib.Util;
import com.team5104.lib.console;
import com.team5104.lib.control.PositionController;
import com.team5104.lib.managers.Subsystem;
import com.team5104.lib.sensors.Encoder.MagEncoder;
import com.team5104.lib.sensors.Limelight;
import com.team5104.lib.webapp.Tuner;

public class Hood extends Subsystem {
	private static TalonSRX motor;
	private static MagEncoder encoder;
	private static MovingAverage visionFilter;
	private static PositionController controller;
	private static double targetAngle = 0;

	//Loop
	public void update() {
		//Automatic
		if (Superstructure.isEnabled()) {
			//Calibrating
			if (isCalibrating()) {
				setPercentOutput(-Constants.HOOD_CALIBRATE_SPEED);
			}

			//Characterizing
			else if (isCharacterizing()) {
				//do nothing
			}
			
			//Low
			else if (Superstructure.is(Target.LOW)) {
				setAngle(40);
			}
			
			//Vision
			else if (Superstructure.is(Mode.AIMING) || Superstructure.is(Mode.SHOOTING)) {
				if (/*Limelight.hasTarget() && */Superstructure.is(Mode.AIMING)) {
					visionFilter.update(Limelight.getTargetY());
					setAngle(getTargetVisionAngle());
				}
				else setAngle(targetAngle);
			}
				
			//Pull Back
			else setAngle(-1);
		}
			
		//Disabled
		else {
			stop();
		}
	}

	//Fast Loop
	public void fastUpdate() {
		//Exit Calibrating
		if (isCalibrating() && backLimitHit()) {
			console.log("finished calibration!");
			stopCalibrating();
		}
		
		//Zero
		if (backLimitHit()) {
			resetEncoder();
			motor.configForwardSoftLimitEnable(true);
		}
	}
	
	//Debugging
	public void debug() {
		Tuner.setTunerOutput("Hood Limelight Y", Limelight.getTargetY());
		Tuner.setTunerOutput("Hood Angle", getAngle());
		Tuner.setTunerOutput("Hood Output", controller.getLastOutput());
		Tuner.setTunerOutput("Hood FF", controller.getLastFFOutput());
		Tuner.setTunerOutput("Hood PID", controller.getLastPIDOutput());
		Tuner.setTunerOutput("Hood KP", getKP());
		Tuner.setTunerOutput("Hood Limit", backLimitHit());
		Constants.hood.kD = Tuner.getTunerInputDouble("Hood KD", Constants.hood.kD);
		//tunerTargetAngle = Tuner.getTunerInputDouble("Hood Target Vision Angle", 10);
	}

	//Internal Functions
	private void setAngle(double degrees) {
		targetAngle = Util.limit(degrees, -1, 40);
		controller.setP(getKP());
		setVoltage(controller.calculate(getAngle(), targetAngle));
	}
	private void setVoltage(double volts) {
		setPercentOutput(Util.limit(volts, -6, 6) / motor.getBusVoltage());
	}
	private void setPercentOutput(double percent) {
		motor.set(ControlMode.PercentOutput, percent);
	}
	private void stop() {
		motor.set(ControlMode.Disabled, 0);
	}
	private void resetEncoder() {
		encoder.reset();
	}
	private double getKP() {
		double x = getAngle();
		return -0.000250 * x * x * x + 0.0136 * x * x - 0.209 * x + 1.7;
	}

	//External Functions
	public static double getAngle() {
		if (motor == null) return 0;
		return encoder.getComponentRevs() * 360d;
	}
	public static boolean backLimitHit() {
		if (motor == null) return true;
		return motor.isRevLimitSwitchClosed() == 0;
	}
	public static boolean onTarget() {
		if (motor == null) return true;
		return Math.abs(getAngle() - getTargetVisionAngle()) < (Constants.HOOD_TOL);
	}
	public static boolean isTrenchMode() {
		return visionFilter.getDoubleOutput() < -13;
	}
	public static double getTargetVisionAngle() {
		if (isTrenchMode())
			return 7;
		double x = visionFilter.getDoubleOutput();
		return -0.00638178 * x * x * x - 0.297426 * x * x - 3.24309 * x + Constants.HOOD_EQ_CONST;
	}

	//Config
	public void init() {
		motor = new TalonSRX(Ports.HOOD_MOTOR);
		motor.configFactoryDefault();
		motor.setInverted(Constants.robot.switchOnBot(true, false));
		motor.setSensorPhase(Constants.robot.switchOnBot(false, true));

		encoder = new MagEncoder(motor, Constants.hood.gearing);

		motor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed);
		motor.configForwardSoftLimitThreshold((int) encoder.componentRevsToTicks(38 / 360d));
		motor.configForwardSoftLimitEnable(false);
		
		controller = new PositionController(Constants.hood);
		controller.setP(getKP());
		visionFilter = new MovingAverage(3, 0);
		
		console.log("ready to calibrate!");
		startCalibrating();

		configCharacterization(
			() -> encoder.getComponentRevs() * 360d,
			() -> encoder.getComponentRPS() * 360d,
			(double voltage) -> setVoltage(voltage)
		);
	}

	//Reset
	public void disabled() {
		stop();
		
		if (!this.isCalibrating()) {
			console.log("ready to calibrate!");
			startCalibrating();
		}
	}
}
