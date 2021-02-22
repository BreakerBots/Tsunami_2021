package com.team5104.frc2021.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team5104.frc2021.Constants;
import com.team5104.frc2021.Ports;
import com.team5104.frc2021.Superstructure;
import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.frc2021.Superstructure.PanelState;
import com.team5104.lib.console;
import com.team5104.lib.devices.ColorSensor;
import com.team5104.lib.devices.ColorSensor.PanelColor;
import com.team5104.lib.devices.Encoder.MagEncoder;
import com.team5104.lib.devices.Solenoid;
import com.team5104.lib.subsystem.ServoSubsystem;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;

public class Paneler extends ServoSubsystem {
  private ColorSensor sensor;
  private TalonSRX motor;
  private static MagEncoder encoder;
  private Solenoid piston;
  private static boolean complete;
  private int end;

  //Loop
  public void update() {
    if (Superstructure.isEnabled()) {
      if (Superstructure.is(Mode.PANEL_DEPLOYING)) {
        setFiniteState("Deploying");
        complete = false;
        end = 0;
        piston.set(true);
        stop();
        resetEncoder();
      }

      else if (Superstructure.is(Mode.PANELING)) {
        if (Superstructure.is(PanelState.ROTATION)) {
          setFiniteState("Paneling - Rotation");
          console.log(getPanelRotations());
          if (getPanelRotations() >= Constants.paneler.ROTATIONS && end < Constants.paneler.BRAKE_INT) {
            setPercentOutput(0);
            end++;
          } else if (getPanelRotations() >= Constants.paneler.ROTATIONS) {
            complete = true;
            end = 0;
          }
          else setPercentOutput(Constants.paneler.ROT_SPEED);
        }

        else {
          setFiniteState("Paneling - Position");
          if (readFMS().length() > 0 && PanelColor.fromChar(readFMS().charAt(0)) == readColor()
              && end < Constants.paneler.BRAKE_INT) {
            setPercentOutput(0);
            end++;
          } else if (readFMS().length() > 0 && PanelColor.fromChar(readFMS().charAt(0)) == readColor()) {
            complete = true;
            end = 0;
          }
          else setPercentOutput(Constants.paneler.POS_SPEED);
        }
      }

      else if (Superstructure.is(Mode.IDLE)) {
        setFiniteState("Stopped");
        stop();
        piston.set(false);
      }
    }
    else {
      setFiniteState("Stopped");
      stop();
      piston.set(false);
    }
  }

  // Internal Functions
  private void setPercentOutput(double percent) {
    motor.set(ControlMode.PercentOutput, percent);
  }
  private PanelColor readColor() {
    return sensor.getNearestColor();
  }
  private void resetEncoder() {
    encoder.reset();
  }
  private String readFMS() {
    return DriverStation.getInstance().getGameSpecificMessage();
  }

  //External Functions
  public static boolean isFinished() {
    return complete;
  }
  public static double getPanelRotations() {
    return encoder.getComponentRevs();
  }

  //Config
  public Paneler() {
    super(Constants.paneler);

    sensor = new ColorSensor(I2C.Port.kOnboard);
    piston = new Solenoid(Ports.PANELER_DEPLOYER);
    motor = new TalonSRX(Ports.PANELER_MOTOR);
    motor.configOpenloopRamp(0.25);
    motor.configFactoryDefault();
    motor.setInverted(Constants.robot.switchOnBot(true, false));
    motor.setSensorPhase(Constants.robot.switchOnBot(true, false));
    motor.configNeutralDeadband(0);
    encoder = new MagEncoder(motor, Constants.paneler.GEARING);
    resetEncoder();
  }

  //Reset
  public void reset() {
    resetEncoder();
  }
}
