package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.controller.PIDController;
import frc.team5104.Constants;
import frc.team5104.Ports;
import frc.team5104.Superstructure;
import frc.team5104.Superstructure.Mode;
import frc.team5104.util.*;
import frc.team5104.util.LatchedBoolean.LatchedBooleanMode;
import frc.team5104.util.Sensor.PortType;
import frc.team5104.util.console.c;
import frc.team5104.util.managers.Subsystem;
import frc.team5104.util.Encoder.FalconEncoder;

public class Hopper extends Subsystem {
	private static VictorSPX startMotor, feederMotor;
	private static TalonFX indexMotor;
	private static FalconEncoder indexEncoder;
	private static Sensor entrySensor, endSensor;
	private static LatchedBoolean entrySensorLatch;
	private static MovingAverage isFullAverage, hasFed, entrySensorAverage;
	private static PIDController controller;
	private static boolean isIndexing;
	private static double targetMidPosition;
	
	//Loop
	public void update() {
		//Force Stopped
		if (Superstructure.isClimbing() || Superstructure.isPaneling() || Superstructure.isDisabled()) {
			stopAll();
		}
		
		//Shooting
		else if (Superstructure.is(Mode.SHOOTING)) {
			setIndex(Constants.HOPPER_FEED_SPEED);
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
				setIndex(
					controller.calculate(getMidPosition(), targetMidPosition) + Constants.hopperIndex.kS
				);
				setFeeder(2);
			}
			else {
				setIndex(0);
				setFeeder(0);
			}
			
			//Entry
			if (Superstructure.is(Mode.INTAKE))
				setStart(Constants.HOPPER_START_INTAKE_SPEED);
			else if (isIndexing)
				setStart(Constants.HOPPER_START_INDEX_SPEED);
			else setStart(0);
		}
		
		hasFed.update(Superstructure.is(Mode.SHOOTING));
		entrySensorAverage.update(entrySensor.get());
		isFullAverage.update(isFull());
	}
	
	//Debugging
	public void debug() {
		//Competition Debugging
		if (Constants.config.isAtCompetition) {
			Tuner.setTunerOutput("Hopper Mid Output", indexMotor.getMotorOutputPercent());
			Constants.HOPPER_INDEX_BALL_SIZE = Tuner.getTunerInputDouble("Hopper Mid Ball Size", Constants.HOPPER_INDEX_BALL_SIZE);
			return;
		}

		//Constants.HOPPER_INDEX_BALL_SIZE = Tuner.getTunerInputDouble("Hopper Mid Ball Size", Constants.HOPPER_INDEX_BALL_SIZE);
		Constants.hopperIndex.kP = Tuner.getTunerInputDouble("Hopper Index KP", Constants.hopperIndex.kP);
		Constants.hopperIndex.kI = Tuner.getTunerInputDouble("Hopper Index KI", Constants.hopperIndex.kI);
		Constants.hopperIndex.kD = Tuner.getTunerInputDouble("Hopper Index KD", Constants.hopperIndex.kD);
		Constants.HOPPER_INDEX_TOL = Tuner.getTunerInputDouble("Hopper Mid Tol", Constants.HOPPER_INDEX_TOL);
		Constants.HOPPER_FEED_SPEED = Tuner.getTunerInputDouble("Hopper Feed Speed", Constants.HOPPER_FEED_SPEED);
		Tuner.setTunerOutput("Hopper Target", targetMidPosition);
		Tuner.setTunerOutput("Hopper Position", getMidPosition());
		Tuner.setTunerOutput("Hopper Output", indexMotor.getMotorOutputVoltage());
		Tuner.setTunerOutput("Hopper Indexing", isIndexing);
		Tuner.setTunerOutput("Hopper Full", isFull());
		Tuner.setTunerOutput("Hopper Entry", isEntrySensorTrippedAvg());
		controller.setPID(Constants.hopperIndex.kP, Constants.hopperIndex.kI, Constants.hopperIndex.kD);
	}

	//Internal Functions
	private void setIndex(double volts) {
		indexMotor.set(ControlMode.PercentOutput, volts / indexMotor.getBusVoltage());
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
		indexMotor.set(ControlMode.Disabled, 0);
	}
	private void resetMidEncoder() {
		indexEncoder.reset();
	}
	public static boolean isEntrySensorTrippedAvg() {
		return entrySensorAverage.getBooleanOutput();
	}
	public static boolean isEndSensorTripped() {
		return endSensor.get();
	}
	
	//External Functions
	public static boolean isEmpty() {
		return (indexMotor == null) ? false : !isEndSensorTripped() && !isEntrySensorTrippedAvg();
	}
	public static boolean isFull() {
		return (indexMotor == null) ? false : isEndSensorTripped() && isEntrySensorTrippedAvg();
	}
	public static boolean isFullAverage() {
		return (indexMotor == null) ? false : isFullAverage.getBooleanOutput();
	}
	public static boolean isIndexing() {
		return (indexMotor == null) ? false : isIndexing;
	}
	public static boolean hasFedAverage() {
		return (indexMotor == null) ? false : hasFed.getBooleanOutput();
	}
	public static double getMidPosition() {
		return (indexMotor == null) ? 0 : indexEncoder.getComponentRevs();
	}
	
	//Config
	public void init() {
		startMotor = new VictorSPX(Ports.HOPPER_START_MOTOR);
		startMotor.configFactoryDefault();
		startMotor.setInverted(Constants.config.isCompetitionRobot ? false : true);

		feederMotor = new VictorSPX(Ports.HOPPER_FEEDER_MOTOR);
		feederMotor.configFactoryDefault();
		feederMotor.setInverted(Constants.config.isCompetitionRobot ? false : true);
		
		indexMotor = new TalonFX(Ports.HOPPER_INDEX_MOTOR);
		indexMotor.configFactoryDefault();
		indexMotor.setInverted(true);
		indexEncoder = new FalconEncoder(indexMotor, Constants.hopperIndex.gearing);

		entrySensor = new Sensor(PortType.ANALOG, Ports.HOPPER_SENSOR_START, Constants.config.isCompetitionRobot ? false : true);
		endSensor = new Sensor(PortType.ANALOG, Ports.HOPPER_SENSOR_END, Constants.config.isCompetitionRobot ? false : true);
		
		entrySensorLatch = new LatchedBoolean(LatchedBooleanMode.RISING);
		isFullAverage = new MovingAverage(200, 0);
		hasFed = new MovingAverage(100, 0);
		entrySensorAverage = new MovingAverage(4, false);
		
		controller = new PIDController(
				Constants.hopperIndex.kP, Constants.hopperIndex.kI, Constants.hopperIndex.kD
			);
	}

	//Reset
	public void disabled() {
		stopAll();
		resetMidEncoder();
		targetMidPosition = 0;
	}
}