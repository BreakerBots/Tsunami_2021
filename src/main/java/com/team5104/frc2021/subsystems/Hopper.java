package com.team5104.frc2021.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team5104.frc2021.Constants;
import com.team5104.frc2021.Ports;
import com.team5104.frc2021.Superstructure;
import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.lib.LatchedBoolean;
import com.team5104.lib.LatchedBoolean.LatchedBooleanMode;
import com.team5104.lib.MovingAverage;
import com.team5104.lib.console;
import com.team5104.lib.devices.Encoder.FalconEncoder;
import com.team5104.lib.devices.PhotoSensor;
import com.team5104.lib.devices.PhotoSensor.PortType;
import com.team5104.lib.subsystem.ServoSubsystem;
import edu.wpi.first.wpilibj.controller.PIDController;

public class Hopper extends ServoSubsystem {
  private static VictorSPX startMotor, feederMotor;
  private static TalonFX indexMotor;
  private static FalconEncoder indexEncoder;
  private static PhotoSensor entrySensor, endSensor;
  private LatchedBoolean entrySensorLatch;
  private static MovingAverage isFullAverage, hasFed, entrySensorAverage;
  private PIDController controller;
  private static boolean isIndexing;
  private double targetIndexPosition;

  //Loop
  public void update() {
    if (Superstructure.isClimbing() || Superstructure.isPaneling() || Superstructure.isDisabled()) {
      setFiniteState("Stopped");
      stopAll();
    }

    else if (is(SubsystemMode.CHARACTERIZING)) {
      setFiniteState("Characterizing");
      //do nothing
    }

    else if (Superstructure.is(Mode.SHOOTING)) {
      setFiniteState("Shooting");
      setIndexer(Constants.hopper.FEED_SPEED);
      setFeeder(Constants.hopper.FEED_SPEED);
      setStart(Constants.hopper.FEED_SPEED);
    }

    else {
      setFiniteState("Indexing");
      //Indexing
      isIndexing = !isEndSensorTripped() && (isEntrySensorTrippedAvg() ||
          (getIndexPosition() + Constants.hopper.TOLERANCE) < targetIndexPosition);
      if (entrySensorLatch.get(isEntrySensorTrippedAvg())) {
        console.log("i gots da ball");
        targetIndexPosition = Constants.hopper.BALL_SIZE;
        controller.reset();
        resetIndexerEncoder();
      }

      //Indexer and Feeder
      if (isIndexing) {
        setIndexer(
          controller.calculate(getIndexPosition(), targetIndexPosition) + Constants.hopper.KS
        );
        setFeeder(2);
      }
      else {
        setIndexer(0);
        setFeeder(0);
      }

      //Entry
      if (Superstructure.is(Mode.INTAKE))
        setStart(Constants.hopper.START_SPEED_INTAKING);
      else if (isIndexing)
        setStart(Constants.hopper.START_SPEED_INDEXING);
      else setStart(0);
    }

    hasFed.update(Superstructure.is(Mode.SHOOTING));
    entrySensorAverage.update(entrySensor.get());
    isFullAverage.update(isFull());
  }

  //Internal Functions
  private void setIndexer(double volts) {
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
  private void resetIndexerEncoder() {
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
  public static double getIndexPosition() {
    return (indexMotor == null) ? 0 : indexEncoder.getComponentRevs();
  }

  //Config
  public Hopper() {
    super(Constants.hopper);

    startMotor = new VictorSPX(Ports.HOPPER_START_MOTOR);
    startMotor.configFactoryDefault();
    startMotor.setInverted(Constants.robot.switchOnBot(false, true));

    feederMotor = new VictorSPX(Ports.HOPPER_FEEDER_MOTOR);
    feederMotor.configFactoryDefault();
    feederMotor.setInverted(Constants.robot.switchOnBot(false, true));

    indexMotor = new TalonFX(Ports.HOPPER_INDEX_MOTOR);
    indexMotor.configFactoryDefault();
    indexMotor.setInverted(true);
    indexEncoder = new FalconEncoder(indexMotor, Constants.hopper.GEARING);

    entrySensor = new PhotoSensor(PortType.ANALOG, Ports.HOPPER_SENSOR_START,
                                  Constants.robot.switchOnBot(false, true));
    endSensor = new PhotoSensor(PortType.ANALOG, Ports.HOPPER_SENSOR_END,
                                Constants.robot.switchOnBot(false, true));

    entrySensorLatch = new LatchedBoolean(LatchedBooleanMode.RISING);
    isFullAverage = new MovingAverage(200, 0);
    hasFed = new MovingAverage(100, 0);
    entrySensorAverage = new MovingAverage(4, false);

    controller = new PIDController(
        Constants.hopper.KP, Constants.hopper.KI, Constants.hopper.KD
      );

    configCharacterization(indexEncoder, (double voltage) -> setIndexer(voltage));
  }

  //Reset
  public void reset() {
    resetIndexerEncoder();
    targetIndexPosition = 0;
  }
}
