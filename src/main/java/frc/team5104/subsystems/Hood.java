package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.team5104.Constants;
import frc.team5104.Ports;
import frc.team5104.Superstructure;
import frc.team5104.Superstructure.Mode;
import frc.team5104.Superstructure.SystemState;
import frc.team5104.Superstructure.Target;
import frc.team5104.util.PositionController;
import frc.team5104.util.Tuner;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.Limelight;
import frc.team5104.util.BreakerMath;
import frc.team5104.util.MovingAverage;
import frc.team5104.util.managers.Subsystem;

public class Hood extends Subsystem {
	private static TalonSRX motor;
	private static MovingAverage visionFilter;
	private static PositionController controller;
	private static double targetAngle = 0;

	//Loop
	public void update() {
		//Competition Debugging
		if (Constants.AT_COMP) {
			Tuner.setTunerOutput("Hood Output", motor.getMotorOutputPercent());
			Constants.HOOD_EQ_CONST = Tuner.getTunerInputDouble("Hood Eq Const", Constants.HOOD_EQ_CONST);
		}
		
		//Automatic
		if (Superstructure.getSystemState() == SystemState.AUTOMATIC) {
			//Calibrating
			if (isCalibrating()) {
				setPercentOutput(-Constants.HOOD_CALIBRATE_SPEED);
			}
			
			//Low
			else if (Superstructure.getTarget() == Target.LOW) {
				setAngle(40);
			}
			
			//Vision
			else if (Superstructure.getMode() == Mode.AIMING || Superstructure.getMode() == Mode.SHOOTING) {
				if (/*Limelight.hasTarget() && */Superstructure.getMode() == Mode.AIMING) {
					visionFilter.update(Limelight.getTargetY());
					setAngle(getTargetVisionAngle());
				}
				else setAngle(targetAngle);
			}
				
			//Pull Back
			else setAngle(-1);
		}
			
		//Disabled
		else stop();
	}
	
	//Fast Loop
	public void fastUpdate() {
		//Exit Calibrating
		if (isCalibrating() && backLimitHit()) {
			console.log(c.HOOD, "finished calibration!");
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
		Constants.HOOD_KD = Tuner.getTunerInputDouble("Hood KD", Constants.HOOD_KD);
		//tunerTargetAngle = Tuner.getTunerInputDouble("Hood Target Vision Angle", 10);
	}

	//Internal Functions
	private void setAngle(double degrees) {
		targetAngle = BreakerMath.clamp(degrees, -1, 40);
		controller.setPID(getKP(), 0, Constants.HOOD_KD);
		setVoltage(controller.calculate(getAngle(), targetAngle));
	}
	private void setVoltage(double volts) {
		volts = BreakerMath.clamp(volts, -6, 6); //TODO DELETE ME!!
		setPercentOutput(volts / motor.getBusVoltage());
	}
	private void setPercentOutput(double percent) {
		motor.set(ControlMode.PercentOutput, percent);
	}
	private void stop() {
		motor.set(ControlMode.Disabled, 0);
	}
	private void resetEncoder() {
		motor.setSelectedSensorPosition(0);
	}
	private double getKP() {
		double x = getAngle();
		return -0.000250 * x * x * x + 0.0136 * x * x - 0.209 * x + 1.7;
	}

	//External Functions
	public static double getAngle() {
		if (motor == null) return 0;
		return motor.getSelectedSensorPosition() / Constants.HOOD_TICKS_PER_REV * 360.0;
	}
	public static boolean backLimitHit() {
		if (motor == null) return true;
		return motor.isRevLimitSwitchClosed() == 0;
	}
	public static boolean onTarget() {
		if (motor == null) return true;
		return Math.abs(getAngle() - getTargetVisionAngle()) < (Constants.HOOD_TOL * Constants.SUPERSTRUCTURE_TOL_SCALAR);
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
		motor.setInverted(Constants.COMP_BOT ? true : false);
		motor.setSensorPhase(Constants.COMP_BOT ? false : true);
		
		motor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed);
		motor.configForwardSoftLimitThreshold((int) (Constants.HOOD_TICKS_PER_REV * (38.0 / 360.0)));
		motor.configForwardSoftLimitEnable(false);
		
		controller = new PositionController(
				getKP(),
				0,
				Constants.HOOD_KD,
				Constants.HOOD_MAX_VEL,
				Constants.HOOD_MAX_ACC,
				Constants.HOOD_KS,
				Constants.HOOD_KV,
				Constants.HOOD_KA
			);
		visionFilter = new MovingAverage(3, 0);
		
		console.log(c.HOOD, "ready to calibrate!");
		startCalibrating();
	}

	//Reset
	public void disabled() {
		stop();
		
		if (!this.isCalibrating()) {
			console.log(c.HOOD, "ready to calibrate!");
			startCalibrating();
		}
	}
}