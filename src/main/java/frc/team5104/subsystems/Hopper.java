package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.controller.PIDController;
import frc.team5104.Constants;
import frc.team5104.Ports;
import frc.team5104.Superstructure;
import frc.team5104.Superstructure.Mode;
import frc.team5104.Superstructure.SystemState;
import frc.team5104.util.LatchedBoolean;
import frc.team5104.util.LatchedBoolean.LatchedBooleanMode;
import frc.team5104.util.MovingAverage;
import frc.team5104.util.Sensor;
import frc.team5104.util.Sensor.PortType;
import frc.team5104.util.console.c;
import frc.team5104.util.Tuner;
import frc.team5104.util.console;
import frc.team5104.util.managers.Subsystem;

public class Hopper extends Subsystem {
	private static VictorSPX startMotor, feederMotor;
	private static TalonFX middleMotor;
	private static Sensor entrySensor, endSensor;
	private static LatchedBoolean entrySensorLatch;
	private static MovingAverage isFullAverage, hasFed, entrySensorAverage;
	private static PIDController controller;
	private static boolean isIndexing;
	private static double targetMidPosition = 0;
	
	//Loop
	public void update() {
		//Competition Debugging
		if (Constants.AT_COMP) {
			Tuner.setTunerOutput("Hopper Mid Output", middleMotor.getMotorOutputPercent());
			Constants.HOPPER_INDEX_BALL_SIZE = Tuner.getTunerInputDouble("Hopper Mid Ball Size", Constants.HOPPER_INDEX_BALL_SIZE);
		}
		
		//Force Stopped
		if (Superstructure.isClimbing() || Superstructure.isPaneling() ||
			Superstructure.getSystemState() == SystemState.DISABLED) {
			stopAll();
		}
		
		//Shooting
		else if (Superstructure.getMode() == Mode.SHOOTING) {
			setMiddle(Constants.HOPPER_FEED_SPEED);
			setFeeder(Constants.HOPPER_FEED_SPEED);
			setStart(Constants.HOPPER_FEED_SPEED);
		}
		
		//Indexing
		else {
			//Indexing
			isIndexing = !isEndSensorTripped() && (isEntrySensorTrippedAvg() || 
					(getMidPosition() + Constants.HOPPER_INDEX_TOL) < targetMidPosition);
			if (entrySensorLatch.get(isEntrySensorTrippedAvg())) {
				console.log(c.HOPPER, "I gots da ball");
				targetMidPosition = Constants.HOPPER_INDEX_BALL_SIZE;
				controller.reset();
				resetMidEncoder();
			}
			
			//Mid and Feeder
			if (isIndexing) {
				setMiddle(
					controller.calculate(getMidPosition(), targetMidPosition) + Constants.HOPPER_INDEX_KS
				);
				setFeeder(
					2
				);
			}
			else {
				setMiddle(0);
				setFeeder(0);
			}
			
			//Entry
			if (Superstructure.getMode() == Mode.INTAKE)
				setStart(Constants.HOPPER_START_INTAKE_SPEED);
			else if (isIndexing)
				setStart(Constants.HOPPER_START_INDEX_SPEED);
			else setStart(0);
		}
		
		hasFed.update(Superstructure.getMode() == Mode.SHOOTING);
		entrySensorAverage.update(entrySensor.get());
		isFullAverage.update(isFull());
	}
	
	//Debugging
	public void debug() {
		//Constants.HOPPER_INDEX_BALL_SIZE = Tuner.getTunerInputDouble("Hopper Mid Ball Size", Constants.HOPPER_INDEX_BALL_SIZE);
		Constants.HOPPER_INDEX_KP = Tuner.getTunerInputDouble("Hopper Index KP", Constants.HOPPER_INDEX_KP);
		Constants.HOPPER_INDEX_KI = Tuner.getTunerInputDouble("Hopper Index KI", Constants.HOPPER_INDEX_KI);
		Constants.HOPPER_INDEX_KD = Tuner.getTunerInputDouble("Hopper Index KD", Constants.HOPPER_INDEX_KD);
		Constants.HOPPER_INDEX_TOL = Tuner.getTunerInputDouble("Hopper Mid Tol", Constants.HOPPER_INDEX_TOL);
		Constants.HOPPER_FEED_SPEED = Tuner.getTunerInputDouble("Hopper Feed Speed", Constants.HOPPER_FEED_SPEED);
		Tuner.setTunerOutput("Hopper Target", targetMidPosition);
		Tuner.setTunerOutput("Hopper Position", getMidPosition());
		Tuner.setTunerOutput("Hopper Output", middleMotor.getMotorOutputVoltage());
		Tuner.setTunerOutput("Hopper Indexing", isIndexing);
		Tuner.setTunerOutput("Hopper Full", isFull());
		Tuner.setTunerOutput("Hopper Entry", isEntrySensorTrippedAvg());
		controller.setPID(Constants.HOPPER_INDEX_KP, Constants.HOPPER_INDEX_KI, Constants.HOPPER_INDEX_KD);
	}

	//Internal Functions
	private void setMiddle(double volts) {
		middleMotor.set(ControlMode.PercentOutput, volts / middleMotor.getBusVoltage());
	}
	private void setStart(double volts) {
		startMotor.set(ControlMode.PercentOutput, volts / startMotor.getBusVoltage());
	}
	private void setFeeder(double volts) {
		feederMotor.set(ControlMode.PercentOutput, volts / feederMotor.getBusVoltage());
	}
	private void stopAll() {
		startMotor.set(ControlMode.Disabled, 0);
		feederMotor.set(ControlMode.Disabled, 0);
		middleMotor.set(ControlMode.Disabled, 0);
	}
	private void resetMidEncoder() {
		middleMotor.setSelectedSensorPosition(0);
	}
	public static boolean isEntrySensorTrippedAvg() {
		return entrySensorAverage.getBooleanOutput();
	}
	public static boolean isEndSensorTripped() {
		return endSensor.get();
	}
	
	//External Functions
	public static boolean isEmpty() {
		if (middleMotor == null)
			return false;
		return !isEndSensorTripped() && !isEntrySensorTrippedAvg();
	}
	public static boolean isFull() {
		if (middleMotor == null)
			return false;
		return isEndSensorTripped() && isEntrySensorTrippedAvg();
	}
	public static boolean isFullAverage() {
		if (middleMotor == null)
			return false;
		return isFullAverage.getBooleanOutput();
	}
	public static boolean isIndexing() {
		if (middleMotor == null)
			return false;
		return isIndexing;
	}
	public static boolean hasFedAverage() {
		if (middleMotor == null)
			return false;
		return hasFed.getBooleanOutput();
	}
	public static double getMidPosition() {
		if (middleMotor == null)
			return 0;
		return middleMotor.getSelectedSensorPosition() / Constants.HOPPER_INDEX_TICKS_PER_REV;
	}
	
	//Config
	public void init() {
		startMotor = new VictorSPX(Ports.HOPPER_START_MOTOR);
		startMotor.configFactoryDefault();
		startMotor.setInverted(Constants.COMP_BOT ? false : true);

		feederMotor = new VictorSPX(Ports.HOPPER_FEEDER_MOTOR);
		feederMotor.configFactoryDefault();
		feederMotor.setInverted(Constants.COMP_BOT ? false : true);
		
		middleMotor = new TalonFX(Ports.HOPPER_MIDDLE_MOTOR);
		middleMotor.configFactoryDefault();
		middleMotor.setInverted(true);

		entrySensor = new Sensor(PortType.ANALOG, Ports.HOPPER_SENSOR_START, Constants.COMP_BOT ? false : true);
		endSensor = new Sensor(PortType.ANALOG, Ports.HOPPER_SENSOR_END, Constants.COMP_BOT ? false : true);
		
		entrySensorLatch = new LatchedBoolean(LatchedBooleanMode.RISING);
		isFullAverage = new MovingAverage(200, 0);
		hasFed = new MovingAverage(100, 0);
		entrySensorAverage = new MovingAverage(4, false);
		
		controller = new PIDController(
				Constants.HOPPER_INDEX_KP, Constants.HOPPER_INDEX_KI, Constants.HOPPER_INDEX_KD
				);
	}

	//Reset
	public void disabled() {
		stopAll();
		resetMidEncoder();
		targetMidPosition = 0;
	}
}