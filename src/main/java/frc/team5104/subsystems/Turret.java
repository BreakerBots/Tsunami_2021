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
import frc.team5104.Superstructure.SystemState;
import frc.team5104.util.PositionController;
import frc.team5104.util.Tuner;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.LatencyCompensator;
import frc.team5104.util.BreakerMath;
import frc.team5104.util.Filer;
import frc.team5104.util.Limelight;
import frc.team5104.util.MovingAverage;
import frc.team5104.util.managers.Subsystem;

public class Turret extends Subsystem {
	private static TalonFX motor;
	private static PositionController controller;
	private static LatencyCompensator compensator;
	private static MovingAverage outputAverage;
	private static double targetAngle = 0, fieldOrientedOffset = 0, tunerFieldOrientedOffsetAdd;
	
	//Loop
	public void update() {
		//Competition Debugging
		if (Constants.AT_COMP) {
			Tuner.setTunerOutput("Turret Output", motor.getMotorOutputPercent());
			tunerFieldOrientedOffsetAdd = Tuner.getTunerInputDouble("Turret Field Oriented Offset Add", tunerFieldOrientedOffsetAdd);
			Constants.TURRET_KP = Tuner.getTunerInputDouble("Turret KP", Constants.TURRET_KP);
			Constants.TURRET_KD = Tuner.getTunerInputDouble("Turret KD", Constants.TURRET_KD);
			controller.setPID(Constants.TURRET_KP, 0, Constants.TURRET_KD);
		}
		
		//Automatic
		if (Superstructure.getSystemState() == SystemState.AUTOMATIC) {
			//Calibrating
			if (isCalibrating()) {
				enableSoftLimits(false);
				setPercentOutput(Constants.TURRET_CALIBRATE_SPEED);
			}
			
			//Vision
			else if (Superstructure.getMode() == Mode.AIMING || Superstructure.getMode() == Mode.SHOOTING) {
				if (/*Limelight.hasTarget() &&*/ Superstructure.getMode() == Mode.AIMING) {
					setAngle(
						compensator.getValueInHistory(Limelight.getLatency()) - Limelight.getTargetX()
					);
				}
				else setAngle(targetAngle);
			}

			//Field Oriented Mode
			else setAngle(
				BreakerMath.boundDegrees180(
					Drive.getGyro() + fieldOrientedOffset + tunerFieldOrientedOffsetAdd
				)
			);
		}
		
		//Disabled
		else {
			stop();
			targetAngle = BreakerMath.boundDegrees180(Drive.getGyro() + fieldOrientedOffset);
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
	}
	
	//Debugging
	public void debug() {
		Tuner.setTunerOutput("Turret FF", controller.getLastFFOutput());
		Tuner.setTunerOutput("Turret PID", controller.getLastPIDOutput());
		Tuner.setTunerOutput("Turret Error", controller.getLastError());
		Tuner.setTunerOutput("Turret Angle", getAngle());
		Tuner.setTunerOutput("Turret Target Angle", targetAngle);
		//Constants.TURRET_KP = Tuner.getTunerInputDouble("Turret KP", Constants.TURRET_KP);
		//Constants.TURRET_KD = Tuner.getTunerInputDouble("Turret KD", Constants.TURRET_KD);
		//controller.setPID(Constants.TURRET_KP, 0, Constants.TURRET_KD);
	}

	//Internal Functions
	private void setAngle(double angle) {
		targetAngle = BreakerMath.clamp(angle, -260, 260);
		outputAverage.update(controller.calculate(getAngle(), targetAngle));
		setVoltage(outputAverage.getDoubleOutput());
	}
	private void setVoltage(double volts) {
		volts = BreakerMath.clamp(volts, -Constants.TURRET_VOLT_LIMIT, Constants.TURRET_VOLT_LIMIT);
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
		motor.setSelectedSensorPosition((int) (angle / 360.0 * Constants.TURRET_TICKS_PER_REV));
	}
	private void enableSoftLimits(boolean enabled) {
		motor.configForwardSoftLimitEnable(enabled);
		motor.configReverseSoftLimitEnable(enabled);
	}

	//External Functions
	public static double getAngle() {
		if (motor == null) return 0;
		return motor.getSelectedSensorPosition() / Constants.TURRET_TICKS_PER_REV * 360.0;
	}
	public static boolean leftLimitHit() {
		if (motor == null) return true;
		else if (Constants.COMP_BOT)
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
		motor.setInverted(Constants.COMP_BOT ? false : true);
		
		motor.configForwardSoftLimitThreshold((int) (Constants.TURRET_TICKS_PER_REV * (Constants.TURRET_SOFT_LEFT / 360.0)));
		motor.configReverseSoftLimitThreshold((int) (Constants.TURRET_TICKS_PER_REV * (Constants.TURRET_SOFT_RIGHT / 360.0)));
		enableSoftLimits(false);
		motor.configReverseLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled);
		
		controller = new PositionController(
				Constants.TURRET_KP,
				0,
				Constants.TURRET_KD,
				Constants.TURRET_MAX_VEL,
				Constants.TURRET_MAX_ACC,
				Constants.TURRET_KS,
				Constants.TURRET_KV,
				Constants.TURRET_KA
			);
		compensator = new LatencyCompensator(() -> getAngle());
		outputAverage = new MovingAverage(4, 0);
		
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
		compensator.reset();
		outputAverage.reset();
	}
}