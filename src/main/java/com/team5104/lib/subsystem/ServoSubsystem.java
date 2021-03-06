package com.team5104.lib.subsystem;

import com.team5104.lib.devices.Encoder;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;

import java.util.function.BiConsumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public abstract class ServoSubsystem extends Subsystem {

  /** Base Constructor
   * @param constants Link ServoConstants class from Constants.java */
  public ServoSubsystem(Object constants) {
    super(constants);
  }

  //Characterization
  private DoubleSupplier getLeftEncoderPos, getLeftEncoderVel, getRightEncoderPos, getRightEncoderVel, getGyroAngleRadians;
  private BiConsumer<Double, Double> setOutputVoltage;
  private double lastOutputVoltage;

  protected void configCharacterization(Encoder encoder, DoubleConsumer outputVoltage) {
    configCharacterization(() -> encoder.getComponentRevs(), () -> encoder.getComponentRPS(), outputVoltage);
  }
  protected void configCharacterization(DoubleSupplier encoderPos, DoubleSupplier encoderVel,
                                        DoubleConsumer outputVoltage) {
    configCharacterization(encoderPos, encoderVel, encoderPos, encoderVel,
                           () -> 0, (Double l, Double r) -> { outputVoltage.accept(l); });
  }
  protected void configCharacterization(DoubleSupplier leftEncoderPos, DoubleSupplier leftEncoderVel,
                                        DoubleSupplier rightEncoderPos, DoubleSupplier rightEncoderVel,
                                        DoubleSupplier gyroAngleRadians,
                                        BiConsumer<Double, Double> outputVoltage) {
    getLeftEncoderPos = leftEncoderPos;
    getLeftEncoderVel = leftEncoderVel;
    getRightEncoderPos = rightEncoderPos;
    getRightEncoderVel = rightEncoderVel;
    getGyroAngleRadians = gyroAngleRadians;
    setOutputVoltage = outputVoltage;
  }
  public double[] getCharacterization(double percentOutput, boolean shouldRotate) {
    double batteryVoltage = RobotController.getBatteryVoltage();
    setOutputVoltage.accept((shouldRotate ? -1 : 1) * percentOutput, percentOutput);

    double[] returnArray = new double[] {
        Timer.getFPGATimestamp(),
        batteryVoltage,
        percentOutput,
        lastOutputVoltage,
        lastOutputVoltage,
        getLeftEncoderPos.getAsDouble() * 0.25, //convert from rotations to edges
        getRightEncoderPos.getAsDouble() * 0.25,
        getLeftEncoderVel.getAsDouble() * 0.25,
        getRightEncoderVel.getAsDouble() * 0.25,
        getGyroAngleRadians.getAsDouble()
    };

    lastOutputVoltage = percentOutput * batteryVoltage;

    return returnArray;
  }
}
