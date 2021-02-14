package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import frc.team5104.Constants;
import frc.team5104.Ports;
import frc.team5104.Superstructure;
import frc.team5104.Superstructure.Mode;
import frc.team5104.util.*;
import frc.team5104.util.console.c;
import frc.team5104.util.managers.Subsystem;
import frc.team5104.util.Encoder.FalconEncoder;


public class Turret extends Subsystem {
	private static TalonFX motor;
	private static FalconEncoder encoder;
	private static PositionController controller;
	private static LatencyCompensator latencyCompensator;
	private static MovingAverage outputAverage;
	private static double targetAngle = 0, fieldOrientedOffset = 0, tunerFieldOrientedOffsetAdd;
	
	//Loop
	public void update() {
		//Automatic
		if (Superstructure.isEnabled()) {
			//Calibrating
			if (isCalibrating()) {
				enableSoftLimits(false);
				setPercentOutput(Constants.TURRET_CALIBRATE_SPEED);
			}

			//Characterizing
			else if (isCharacterizing()) {
				//do nothing
			}
			
			//Vision
			else if (Superstructure.is(Mode.AIMING) || Superstructure.is(Mode.SHOOTING)) {
				if (/*Limelight.hasTarget() &&*/ Superstructure.is(Mode.AIMING)) {
					setAngle(
							latencyCompensator.getValueInHistory(Limelight.getLatency()) - Limelight.getTargetX()
					);
				}
				else setAngle(targetAngle);
			}

			//Field Oriented Mode
			else setAngle(
						ExtraMath.boundDegrees180(
					Drive.getHeading() + fieldOrientedOffset + tunerFieldOrientedOffsetAdd
				)
			);
		}
		
		//Disabled
		else {
			stop();
			targetAngle = ExtraMath.boundDegrees180(Drive.getHeading() + fieldOrientedOffset);
			controller.calculate(getAngle(), targetAngle);
		}
	}

	//Fast Loop
	public void fastUpdate() {
		//Exit Calibration
		if (isCalibrating() && leftLimitHit()) {
			console.log(c.TURRET, "finished calibration!");
			Filer.createFile("/tmp/turret_calibrated.txt");
			stopCalibrating();
		}
		
		//Zero Encoder
		if (leftLimitHit()) {
			resetEncoder(Constants.TURRET_ZERO);
			enableSoftLimits(true);
		}

		latencyCompensator.update();
	}
	
	//Debugging
	public void debug() {
		//Competition Debugging
		if (Constants.config.isAtCompetition) {
			Tuner.setTunerOutput("Turret Output", motor.getMotorOutputPercent());
			tunerFieldOrientedOffsetAdd = Tuner.getTunerInputDouble("Turret Field Oriented Offset Add", tunerFieldOrientedOffsetAdd);
			Constants.turret.kP = Tuner.getTunerInputDouble("Turret KP", Constants.turret.kP);
			Constants.turret.kD = Tuner.getTunerInputDouble("Turret KD", Constants.turret.kD);
			controller.setPID(Constants.turret.kP, 0, Constants.turret.kD);
			return;
		}

		Tuner.setTunerOutput("Turret FF", controller.getLastFFOutput());
		Tuner.setTunerOutput("Turret PID", controller.getLastPIDOutput());
		Tuner.setTunerOutput("Turret Error", controller.getLastError());
		Tuner.setTunerOutput("Turret Angle", getAngle());
		Tuner.setTunerOutput("Turret Target Angle", targetAngle);
		Constants.turret.kP = Tuner.getTunerInputDouble("Turret KP", Constants.turret.kP);
		Constants.turret.kD = Tuner.getTunerInputDouble("Turret KD", Constants.turret.kD);
		controller.setPID(Constants.turret.kP, 0, Constants.turret.kD);
	}

	//Internal Functions
	private void setAngle(double angle) {
		targetAngle = ExtraMath.clamp(angle, -260, 260);
		outputAverage.update(controller.calculate(getAngle(), targetAngle));
		setVoltage(outputAverage.getDoubleOutput());
	}
	private void setVoltage(double volts) {
		volts = ExtraMath.clamp(volts, -Constants.TURRET_VOLT_LIMIT, Constants.TURRET_VOLT_LIMIT);
		setPercentOutput(volts / motor.getBusVoltage());
	}
	private void setPercentOutput(double percent) {
		motor.setNeutralMode(NeutralMode.Brake);
		motor.set(ControlMode.PercentOutput, percent);
	}
	private void stop() {
		motor.set(ControlMode.Disabled, 0);
	}
	private void resetEncoder(double angle) {
		encoder.setComponentRevs(angle / 360d);
	}
	private void enableSoftLimits(boolean enabled) {
		motor.configForwardSoftLimitEnable(enabled);
		motor.configReverseSoftLimitEnable(enabled);
	}

	//External Functions
	public static double getAngle() {
		if (motor == null) return 0;
		return encoder.getComponentRevs() * 360d;
	}
	public static boolean leftLimitHit() {
		if (motor == null) return true;
		else if (Constants.config.isCompetitionRobot)
			return motor.isFwdLimitSwitchClosed() == 1;
		return motor.isRevLimitSwitchClosed() == 1;
	}
	public static boolean onTarget() {
		if (motor == null) return true;
		return Math.abs(getAngle() - targetAngle) < (Constants.TURRET_VISION_TOL * Constants.SUPERSTRUCTURE_TOL_SCALAR);
	}
	public static void setFieldOrientedTarget(double angle) {
		fieldOrientedOffset = angle;
	}

	//Config
	public void init() {
		motor = new TalonFX(Ports.TURRET_MOTOR);
		motor.configFactoryDefault();
		motor.setInverted(Constants.config.isCompetitionRobot ? false : true);

		encoder = new FalconEncoder(motor, Constants.turret.gearing);

		motor.configForwardSoftLimitThreshold((int) encoder.componentRevsToTicks(Constants.TURRET_SOFT_LEFT / 360d));
		motor.configReverseSoftLimitThreshold((int) encoder.componentRevsToTicks(Constants.TURRET_SOFT_RIGHT / 360d));
		enableSoftLimits(false);
		motor.configReverseLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled);
		
		controller = new PositionController(Constants.turret);
		latencyCompensator = new LatencyCompensator(() -> getAngle());
		outputAverage = new MovingAverage(4, 0);

		configCharacterization(
				() -> encoder.getComponentRevs() * 360d,
				() -> encoder.getComponentRPS() * 360d,
				(double voltage) -> setVoltage(voltage)
		);

		//Only calibrate once per roborio boot while not.
		if (!Filer.fileExists("/tmp/turret_calibrated.txt")) {
			console.log(c.TURRET, "ready to calibrate!");
			startCalibrating();
		}
		else enableSoftLimits(true);
	}

	//Reset
	public void disabled() {
		stop();
		motor.setNeutralMode(NeutralMode.Coast);
		latencyCompensator.reset();
		outputAverage.reset();
	}
}