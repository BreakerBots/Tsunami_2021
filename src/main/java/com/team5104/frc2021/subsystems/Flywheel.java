package com.team5104.frc2021.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.team5104.frc2021.Constants;
import com.team5104.frc2021.Ports;
import com.team5104.frc2021.Superstructure;
import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.lib.LatchedBoolean;
import com.team5104.lib.MovingAverage;
import com.team5104.lib.Util;
import com.team5104.lib.console;
import com.team5104.lib.devices.Encoder.FalconEncoder;
import com.team5104.lib.motion.VelocityController;
import com.team5104.lib.subsystem.ServoSubsystem;

public class Flywheel extends ServoSubsystem {
  public enum FlywheelState { STOPPED, SPINNING }
  public static FlywheelState state = FlywheelState.STOPPED;

  private static TalonFX motor1, motor2;
  private static FalconEncoder encoder;
  private static MovingAverage avgRPM;
  private VelocityController controller;
  private static LatchedBoolean onTargetTrigger = new LatchedBoolean();

  //Loop
  public void update() {
    if (Superstructure.isEnabled() && state == FlywheelState.SPINNING) {
      setFiniteState("Spinning");
      setRampRate(Constants.flywheel.RAMP_RATE_UP);
      if (Constants.flywheel.OPEN_LOOP)
        setPercentOutput(1.0);
      else setSpeed(getTargetRPM());
    }

    else if (is(SubsystemMode.CHARACTERIZING)) {
      setFiniteState("Characterizing");
      setRampRate(Constants.flywheel.RAMP_RATE_UP);
    }

    else {
      setFiniteState("Stopped");
      setRampRate(Constants.flywheel.RAMP_RATE_DOWN);
      stop();
    }

    //Logging
    if (onTargetTrigger.get(isSpedUp()) && Superstructure.is(Mode.AIMING))
      console.log("sped up");

    avgRPM.update(getRPM());
  }

  //Internal Functions
  private void setSpeed(double rpm) {
    setVoltage(controller.calculate(encoder.getComponentUPS(), rpm / 60d));
  }
  private void setVoltage(double volts) {
    motor1.set(ControlMode.PercentOutput, volts / motor1.getBusVoltage());
    motor2.follow(motor1);
  }
  private void setPercentOutput(double percent) {
    motor1.set(ControlMode.PercentOutput, percent);
  }
  private void setRampRate(double rate) {
    motor1.configOpenloopRamp(rate);
    motor2.configOpenloopRamp(rate);
    motor1.configClosedloopRamp(rate);
    motor2.configClosedloopRamp(rate);
  }

  //External Functions
  public static double getRPM() {
    return (motor1 == null) ? 0 : encoder.getComponentUPM();
  }
  public static double getAvgRPM() {
    return (motor1 == null) ? 0 : avgRPM.getDoubleOutput();
  }
  public static double getTargetRPM() {
    return (Hood.isTrenchMode()) ? 11000 : 9000;
  }
  public static boolean isSpedUp() {
    if (motor1 == null)
      return true;
    return Util.roughlyEquals(getAvgRPM(), getTargetRPM(), Constants.flywheel.RPM_TOL);
  }

  //Config
  public Flywheel() {
    super(Constants.flywheel);

    motor1 = new TalonFX(Ports.FLYWHEEL_MOTOR_1);
    motor1.configFactoryDefault();
    motor1.setInverted(false);

    motor2 = new TalonFX(Ports.FLYWHEEL_MOTOR_2);
    motor2.configFactoryDefault();
    motor2.follow(motor1);
    motor2.setInverted(true);

    setRampRate(Constants.flywheel.RAMP_RATE_UP);

    encoder = new FalconEncoder(motor1, Constants.flywheel.GEARING);

    controller = new VelocityController(
        Constants.flywheel.KP,
        Constants.flywheel.KI,
        Constants.flywheel.KD,
        Constants.flywheel.KS,
        Constants.flywheel.KV,
        Constants.flywheel.KA
    );
    avgRPM = new MovingAverage(50, 0);

    configCharacterization(encoder, (double voltage) -> setVoltage(voltage));
  }

  //Reset
  public void reset() {
    state = FlywheelState.STOPPED;
  }
}
