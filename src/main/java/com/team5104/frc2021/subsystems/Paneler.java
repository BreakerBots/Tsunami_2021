package com.team5104.frc2021.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team5104.frc2021.Constants;
import com.team5104.frc2021.Ports;
import com.team5104.frc2021.Superstructure;
import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.frc2021.Superstructure.PanelState;
import com.team5104.lib.console;
import com.team5104.lib.managers.Subsystem;
import com.team5104.lib.sensors.ColorSensor;
import com.team5104.lib.sensors.ColorSensor.PanelColor;
import com.team5104.lib.sensors.Encoder.MagEncoder;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;

public class Paneler extends Subsystem {
  private static ColorSensor sensor;
  private static TalonSRX motor;
  private static MagEncoder encoder;
  private static DoubleSolenoid piston;
  private static boolean complete;
  private static int end;
  
  //Loop
  public void update() {
    if (Superstructure.isEnabled()) {
      //deploying
      if (Superstructure.is(Mode.PANEL_DEPLOYING)) {
        complete = false;
        end = 0;
        setPiston(true);
        stop();
        resetEncoder();
      }
  
      //paneling
      else if (Superstructure.is(Mode.PANELING)) {
        //rotation
        if (Superstructure.is(PanelState.ROTATION)) {
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
    
        //position
        else {
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
      //idle
      else if (Superstructure.is(Mode.IDLE)) {
        stop();
        setPiston(false);
      }
    }
    else {
      stop();
      setPiston(false);
    }
  }
  
  //Debugging
  public void debug() {
    
  }

  // Internal Functions
  private void setPiston(boolean up) {
    piston.set(up ? Value.kForward : Value.kReverse);
  }
  private void setPercentOutput(double percent) {
    motor.set(ControlMode.PercentOutput, percent);
  }
  private void stop() {
    motor.set(ControlMode.Disabled, 0);
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
  public void init() {
    sensor = new ColorSensor(I2C.Port.kOnboard);
    piston = new DoubleSolenoid(Ports.PANELER_DEPLOYER[0], Ports.PANELER_DEPLOYER[1]);
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
  public void disabled() {
    resetEncoder();
    stop();
    setPiston(false);
  }
}
