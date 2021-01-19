package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import frc.team5104.Constants;
import frc.team5104.Ports;
import frc.team5104.Superstructure;
import frc.team5104.Superstructure.FlywheelState;
import frc.team5104.Superstructure.SystemState;
import frc.team5104.util.BreakerMath;
import frc.team5104.util.MovingAverage;
import frc.team5104.util.Tuner;
import frc.team5104.util.VelocityController;
import frc.team5104.util.managers.Subsystem;

public class Flywheel extends Subsystem {
	private static TalonFX motor1, motor2;
	private static MovingAverage avgRPMS;
	private static VelocityController controller;
	
	//Loop
	public void update() {
		if ((Superstructure.getSystemState() == SystemState.AUTOMATIC || 
			Superstructure.getSystemState() == SystemState.MANUAL) &&
			Superstructure.getFlywheelState() == FlywheelState.SPINNING) {
			setRampRate(Constants.FLYWHEEL_RAMP_RATE_UP);
			if (Constants.FLYWHEEL_OPEN_LOOP)
				setPercentOutput(1.0);
			else setSpeed(getTargetRPMS());
		}
		else {
			setRampRate(Constants.FLYWHEEL_RAMP_RATE_DOWN);
			stop();
		}
		
		avgRPMS.update(getRPMS());
	}

	//Debugging
	public void debug() {
		Tuner.setTunerOutput("Flywheel RPM", getRPMS());
		Tuner.setTunerOutput("Flywheel ARPM", getAvgRPMS());
		Tuner.setTunerOutput("Flywheel FF", controller.getLastFFOutput());
		Tuner.setTunerOutput("Flywheel PID", controller.getLastPIDOutput());
		Tuner.setTunerOutput("Flywheel Out", controller.getLastOutput());
		Tuner.setTunerOutput("Flywheel Avg Sped Up", isSpedUp());
		Tuner.setTunerOutput("Flywheel Current 1", motor1.getSupplyCurrent());
		Tuner.setTunerOutput("Flywheel Current 2", motor2.getSupplyCurrent());
		Tuner.setTunerOutput("Flywheel Voltage 1", motor1.getMotorOutputVoltage());
		Tuner.setTunerOutput("Flywheel Voltage 2", motor2.getMotorOutputVoltage());
		Constants.FLYWHEEL_KP = Tuner.getTunerInputDouble("Flywheel KP", Constants.FLYWHEEL_KP);
		Constants.FLYWHEEL_KD = Tuner.getTunerInputDouble("Flywheel KD", Constants.FLYWHEEL_KD);
		Constants.FLYWHEEL_RPM_TOL = Tuner.getTunerInputDouble("Flywheel Tol", Constants.FLYWHEEL_RPM_TOL);
		controller.setPID(Constants.FLYWHEEL_KP, 0, Constants.FLYWHEEL_KD);
	}
	
	//Internal Functions
	private void setSpeed(double rpms) {
		setVoltage(controller.calculate(getRPMS() / 60.0, rpms / 60.0));
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
	public static double getRPMS() {
		if (motor1 == null)
			return 0;
		return motor1.getSelectedSensorVelocity() / Constants.FLYWHEEL_TICKS_PER_REV * 60.0 * 10.0;
	}
	public static double getAvgRPMS() {
		if (motor1 == null)
			return 0;
		return avgRPMS.getDoubleOutput();
	}
	public static double getTargetRPMS() {
		if (Hood.isTrenchMode())
			return 11000;
		else return 9000;
	}
	public static boolean isSpedUp() {
		if (motor1 == null)
			return true;
		return BreakerMath.roughlyEquals(
				getAvgRPMS(), getTargetRPMS(), 
				Constants.FLYWHEEL_RPM_TOL * Constants.SUPERSTRUCTURE_TOL_SCALAR);
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
		
		setRampRate(Constants.FLYWHEEL_RAMP_RATE_UP);
		
		controller = new VelocityController(
				Constants.FLYWHEEL_KP,
				0,
				Constants.FLYWHEEL_KD,
				Constants.FLYWHEEL_KS,
				Constants.FLYWHEEL_KV,
				0
			);
		
		avgRPMS = new MovingAverage(50, 0);
	}

	//Reset
	public void disabled() {
		stop();
	}
}