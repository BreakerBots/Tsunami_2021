package com.team5104.frc2021.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team5104.frc2021.Constants;
import com.team5104.frc2021.Ports;
import com.team5104.frc2021.Superstructure;
import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.lib.LatchedBoolean;
import com.team5104.lib.MovingAverage;
import com.team5104.lib.Util;
import com.team5104.lib.console;
import com.team5104.lib.devices.Encoder.MagEncoder;
import com.team5104.lib.devices.Limelight;
import com.team5104.lib.motion.PositionController;
import com.team5104.lib.subsystem.ServoSubsystem;

public class Hood extends ServoSubsystem {
  public enum HoodTarget { LOW, HIGH }
  public static HoodTarget target = HoodTarget.HIGH;

  private static TalonSRX motor;
  private static MagEncoder encoder;
  private static MovingAverage visionFilter;
  private PositionController controller;
  private static double targetAngle = 0;
  private static LatchedBoolean onTargetTrigger = new LatchedBoolean();

  //Loop
  public void update() {

//    System.out.println(getAngle() + ", " + targetAngle);
//    System.out.println(visionFilter.getDoubleOutput());
//    System.out.println(visionFilter.getDoubleOutput() + ", " + getAngle() + ", " + getTargetVisionAngle());


    //Automatic
    if (Superstructure.isEnabled()) {
      if (is(SubsystemMode.HOMING)) {
        setFiniteState("Homing");
        setPercentOutput(-Constants.hood.HOMING_SPEED);
      }

      else if (is(SubsystemMode.CHARACTERIZING)) {
        setFiniteState("Characterizing");
        //do nothing
      }

      //Low
      else if (target == HoodTarget.LOW) {
        setFiniteState("Low");
        setAngle(40);
       }

      //Vision
      else if (Superstructure.is(Mode.AIMING) || Superstructure.is(Mode.SHOOTING)) {
        setFiniteState("Vision");

//        setAngle(Math.PI);
//        System.out.println(getAngle());

        if (/*Limelight.hasTarget() && */Superstructure.is(Mode.AIMING)) {
          visionFilter.update(Limelight.getTargetY());
          setAngle(getTargetVisionAngle());
        }
        else setAngle(targetAngle);
      }

      //Pull Back
      else {
        setFiniteState("Back");
        setAngle(-1);
      }
    }

    //Disabled
    else {
      setFiniteState("Stopped");
      stop();
    }

    //Logging
    if (onTargetTrigger.get(onTarget()) && Superstructure.is(Mode.AIMING))
      console.log("on target");
  }

  //Fast Loop
  public void fastUpdate() {
    //Exit Homing
    if (is(SubsystemMode.HOMING) && backLimitHit()) {
      //console.log("finished homing");
      setMode(SubsystemMode.OPERATING, true);
    }

    //Zero
    if (backLimitHit()) {
      resetEncoder();
      motor.configForwardSoftLimitEnable(true);
    }
  }

  //Internal Functions
  private void setAngle(double degrees) {
    targetAngle = Util.limit(degrees, -1, 40);
    controller.setP(getKP());
    setVoltage(controller.calculate(getAngle(), targetAngle));
  }
  private void setVoltage(double volts) {
    setPercentOutput(Util.limit(volts, -6, 6) / motor.getBusVoltage());
  }
  private void setPercentOutput(double percent) {
    motor.set(ControlMode.PercentOutput, percent);
  }
  private void resetEncoder() {
    encoder.reset();
  }
  private double getKP() {
    double x = getAngle();
    return -0.000250 * x * x * x + 0.0136 * x * x - 0.209 * x + 1.7;
  }


  //External Functions
  public static double getAngle() {
    if (motor == null) return 0;
    return encoder.getComponentUnits() * 360d;
  }
  public static double getTargetAngle() {
    return targetAngle;
  }
  public static boolean backLimitHit() {
    if (motor == null) return true;
    return motor.isRevLimitSwitchClosed() == 0;
  }
  public static boolean onTarget() {
    if (motor == null) return true;
    return Math.abs(getAngle() - getTargetVisionAngle()) < (Constants.hood.TOL);
  }
  public static boolean isTrenchMode() {
    return visionFilter.getDoubleOutput() < -13;
  }
  public static double getTargetVisionAngle() {
    if (isTrenchMode())
      return 7;
    double x = visionFilter.getDoubleOutput();
    return -0.00638178 * x * x * x - 0.297426 * x * x - 3.24309 * x + Constants.hood.EQ_CONST;
////    if (isTrenchMode())
////      return 0;
//    double x = visionFilter.getDoubleOutput();
//    //System.out.print("X is ");
//    if (x <= -13) { // Red
////      System.out.println(x);
////      System.out.printf("%.2f\n", x);
////      System.out.println("Hood angle is 13");
//      return 13;
//    }
//    else if (x < -5) { // Blue
//      //System.out.println(x);
////      System.out.printf("%.2f\n", x);
////      System.out.println("Hood angle is 14");
//      return 14;
//    }
//    else if (x < 3){ // Yellow
//      //System.out.println(x);
////      System.out.printf("%.2f\n", x);
////      System.out.println("Hood angle is 8");
////      return 8;
//      return 14;
//    }
//    else {
//      //System.out.println(x);
////      System.out.printf("%.2f\n", x);
////      System.out.println("Hood angle is 0");
//      return 0;
//    }
////    else {
////      return -0.00638178 * x * x * x - 0.297426 * x * x - 3.24309 * x + Constants.hood.EQ_CONST;
////    }
  }
  public HoodTarget getTarget() {
    return target;
  }

  //Config
  public Hood() {
    super(Constants.hood);

    motor = new TalonSRX(Ports.HOOD_MOTOR);
    motor.configFactoryDefault();
    motor.setInverted(Constants.robot.switchOnBot(true, false));
    motor.setSensorPhase(Constants.robot.switchOnBot(false, true));

    encoder = new MagEncoder(motor, Constants.hood.GEARING);

    motor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed);
    motor.configForwardSoftLimitThreshold((int) encoder.componentUnitsToTicks(38 / 360d));
    motor.configForwardSoftLimitEnable(false);

    controller = new PositionController(
        0,
        Constants.hood.KI,
        Constants.hood.KD,
        Constants.hood.MAX_VEL,
        Constants.hood.MAX_ACC,
        Constants.hood.KS,
        Constants.hood.KV,
        Constants.hood.KA
    );
    controller.setP(getKP());
    visionFilter = new MovingAverage(3, 0);

    configCharacterization(
      () -> encoder.getComponentUnits() * 360d,
      () -> encoder.getComponentUPS() * 360d,
      (double voltage) -> setVoltage(voltage)
    );
  }

  //Reset
  public void reset() {
    stop();

    target = HoodTarget.HIGH;

    if (!is(SubsystemMode.HOMING)) {
      //console.log("ready to home");
      setMode(SubsystemMode.HOMING);
    }
  }
}
