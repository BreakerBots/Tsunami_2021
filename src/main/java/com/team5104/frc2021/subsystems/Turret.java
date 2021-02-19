package com.team5104.frc2021.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.team5104.frc2021.Constants;
import com.team5104.frc2021.Ports;
import com.team5104.frc2021.Superstructure;
import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.lib.*;
import com.team5104.lib.control.PositionController;
import com.team5104.lib.managers.Subsystem;
import com.team5104.lib.sensors.Encoder.FalconEncoder;
import com.team5104.lib.sensors.Limelight;
import com.team5104.lib.webapp.Tuner;

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
        setPercentOutput(Constants.turret.CALIBRATE_SPEED);
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
        Util.wrap180(
          Drive.getHeading() + fieldOrientedOffset + tunerFieldOrientedOffsetAdd
        )
      );
    }
    
    //Disabled
    else {
      stop();
      targetAngle = Util.wrap180(Drive.getHeading() + fieldOrientedOffset);
      controller.calculate(getAngle(), targetAngle);
    }
  }

  //Fast Loop
  public void fastUpdate() {
    //Exit Calibration
    if (isCalibrating() && leftLimitHit()) {
      console.log("finished calibration!");
      Filer.createFile("/tmp/turret_calibrated.txt");
      stopCalibrating();
    }
    
    //Zero Encoder
    if (leftLimitHit()) {
      resetEncoder(Constants.turret.ZERO);
      enableSoftLimits(true);
    }

    latencyCompensator.update();
  }
  
  //Debugging
  public void debug() {
    Tuner.setTunerOutput("Turret FF", controller.getLastFFOutput());
    Tuner.setTunerOutput("Turret PID", controller.getLastPIDOutput());
    Tuner.setTunerOutput("Turret Error", controller.getLastError());
    Tuner.setTunerOutput("Turret Angle", getAngle());
    Tuner.setTunerOutput("Turret Target Angle", targetAngle);
    Constants.turret.KP = Tuner.getTunerInputDouble("Turret KP", Constants.turret.KP);
    Constants.turret.KD = Tuner.getTunerInputDouble("Turret KD", Constants.turret.KD);
    controller.setPID(Constants.turret.KP, 0, Constants.turret.KD);
  }

  //Internal Functions
  private void setAngle(double angle) {
    targetAngle = Util.limit(angle, -260, 260);
    outputAverage.update(controller.calculate(getAngle(), targetAngle));
    setVoltage(outputAverage.getDoubleOutput());
  }
  private void setVoltage(double volts) {
    volts = Util.limit(volts, -Constants.turret.VOLT_LIMIT, Constants.turret.VOLT_LIMIT);
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
    else if (Constants.robot.id == 0)
      return motor.isFwdLimitSwitchClosed() == 1;
    return motor.isRevLimitSwitchClosed() == 1;
  }
  public static boolean onTarget() {
    if (motor == null) return true;
    return Math.abs(getAngle() - targetAngle) < (Constants.turret.VISION_TOL);
  }
  public static void setFieldOrientedTarget(double angle) {
    fieldOrientedOffset = angle;
  }

  //Config
  public void init() {
    motor = new TalonFX(Ports.TURRET_MOTOR);
    motor.configFactoryDefault();
    motor.setInverted(Constants.robot.switchOnBot(false, true));

    encoder = new FalconEncoder(motor, Constants.turret.GEARING);

    motor.configForwardSoftLimitThreshold((int) encoder.componentRevsToTicks(Constants.turret.SOFT_LEFT / 360d));
    motor.configReverseSoftLimitThreshold((int) encoder.componentRevsToTicks(Constants.turret.SOFT_RIGHT / 360d));
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
      console.log("ready to calibrate!");
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
