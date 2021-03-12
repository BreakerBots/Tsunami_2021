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
import com.team5104.lib.devices.Encoder.FalconEncoder;
import com.team5104.lib.devices.Limelight;
import com.team5104.lib.motion.PositionController;
import com.team5104.lib.setup.RobotState;
import com.team5104.lib.subsystem.ServoSubsystem;

public class Turret extends ServoSubsystem {
  private static TalonFX motor;
  private static FalconEncoder encoder;
  private PositionController controller;
  private LatencyCompensator latencyCompensator;
  private MovingAverage outputAverage;
  private static double targetAngle = 0,
                        fieldOrientedOffset = -10;
  private static LatchedBoolean onTargetTrigger = new LatchedBoolean();

  //Loop
  public void update() {


    //Automatic
    if (Superstructure.isEnabled()) {

      //Homing
      if (is(SubsystemMode.HOMING)) {
        System.out.println("We are trying to home the turret");
        setFiniteState("Homing");
        enableSoftLimits(false);
        setVoltage(Constants.turret.HOMING_SPEED);
      }

      //Characterizing
      else if (is(SubsystemMode.CHARACTERIZING)) {
        setFiniteState("Characterizing");
        //do nothing
      }

      else if (Superstructure.is(Mode.AIMING) || Superstructure.is(Mode.SHOOTING)) {
        setFiniteState("Vision");
        if (/*Limelight.hasTarget() &&*/ Superstructure.is(Mode.AIMING)) {
          setAngle(
              latencyCompensator.getValueInHistory(Limelight.getLatency()) - Limelight.getTargetX()
          );
        }
        else setAngle(targetAngle);
      }

      else {
        setFiniteState("Field-Oriented");
        setAngle(
            Util.wrap180(
                Drive.getHeading() + fieldOrientedOffset
            )
        );
      }
    }

    else {
      setFiniteState("Stopped");
      stop();
      targetAngle = Util.wrap180(Drive.getHeading() + fieldOrientedOffset);
      controller.calculate(getAngle(), targetAngle);
    }

    //Logging
    if (onTargetTrigger.get(Turret.onTarget()) && Superstructure.is(Mode.AIMING))
      console.log("on target");
  }

  //Fast Loop
  public void fastUpdate() {
    //Exit Homing
    if (is(SubsystemMode.HOMING) && leftLimitHit()) {
      //console.log("finished homing");
      Filer.createFile("/tmp/turret_homed.txt");
      setMode(SubsystemMode.OPERATING, true);
      System.out.println("finished homing!");
    }

    //Zero Encoder
    if (leftLimitHit()) {
      resetEncoder(Constants.turret.ZERO);
      enableSoftLimits(true);
    }

    latencyCompensator.update();
  }

  //Internal Functions
  private void setAngle(double angle) {
    targetAngle = Util.limit(angle, -260, 260);
    outputAverage.update(controller.calculate(getAngle(), targetAngle));
    setVoltage(outputAverage.getDoubleOutput());
  }
  private void setVoltage(double volts) {
    volts = Util.limit(volts, -Constants.turret.VOLT_LIMIT, Constants.turret.VOLT_LIMIT);
    motor.setNeutralMode(NeutralMode.Brake);
    motor.set(ControlMode.PercentOutput, volts / motor.getBusVoltage());
  }
  private void resetEncoder(double angle) {
    encoder.setComponentUnits(angle / 360d);
  }
  private void enableSoftLimits(boolean enabled) {
    motor.configForwardSoftLimitEnable(enabled);
    motor.configReverseSoftLimitEnable(enabled);
  }

  //External Functions
  public static double getAngle() {
    if (motor == null) return 0;
    return encoder.getComponentUnits() * 360d;
  }
  public static boolean leftLimitHit() {
    if (motor == null || RobotState.isSimulation()) return true;
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
  public Turret() {
    super(Constants.turret);

    motor = new TalonFX(Ports.TURRET_MOTOR);
    motor.configFactoryDefault();
    motor.setInverted(Constants.robot.switchOnBot(false, true));

    encoder = new FalconEncoder(motor, Constants.turret.GEARING);

    motor.configForwardSoftLimitThreshold((int) encoder.componentUnitsToTicks(Constants.turret.SOFT_LEFT / 360d));
    motor.configReverseSoftLimitThreshold((int) encoder.componentUnitsToTicks(Constants.turret.SOFT_RIGHT / 360d));
    enableSoftLimits(false);
    motor.configReverseLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled);

    controller = new PositionController(
        Constants.turret.KP,
        Constants.turret.KI,
        Constants.turret.KD,
        Constants.turret.MAX_VEL,
        Constants.turret.MAX_ACC,
        Constants.turret.KS,
        Constants.turret.KV,
        Constants.turret.KA
    );
    latencyCompensator = new LatencyCompensator(() -> getAngle());
    outputAverage = new MovingAverage(4, 0);

    configCharacterization(
        () -> encoder.getComponentUnits() * 360d,
        () -> encoder.getComponentUPS() * 360d,
        (double voltage) -> setVoltage(voltage)
    );
  }

  //Reset
  public void reset() {
    motor.setNeutralMode(NeutralMode.Coast);
    latencyCompensator.reset();
    outputAverage.reset();

    //Only home once per roborio boot while not.
    if (!Filer.fileExists("/tmp/turret_homed.txt")) {
      //console.log("ready to home");
      setMode(SubsystemMode.HOMING, true);
    }
    else enableSoftLimits(true);
  }
}
