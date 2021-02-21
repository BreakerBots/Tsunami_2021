package com.team5104.frc2021.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.team5104.frc2021.Ports;
import com.team5104.frc2021.Superstructure;
import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.lib.devices.Solenoid;
import com.team5104.lib.subsystem.Subsystem;

public class Climber extends Subsystem {
  public static double climberManual = 0.0; //passed through from SuperstructureController
  private Solenoid deployerPiston, brakePiston;
  private TalonFX motor;

  //Loop
  public void update() {
    if (Superstructure.isEnabled() && Superstructure.isClimbing()) {
      brakePiston.set(false);
      deployerPiston.set(Superstructure.is(Mode.CLIMBER_DEPLOYING));
      motor.set(ControlMode.PercentOutput, climberManual);
    }
    else {
      brakePiston.set(true);
      deployerPiston.set(false);
      stop();
    }
  }

  //Config
  public Climber() {
    super(new SubsystemConstants());

    deployerPiston = new Solenoid(Ports.CLIMBER_DEPLOYER);
    brakePiston = new Solenoid(Ports.CLIMBER_BRAKE, true);
    motor = new TalonFX(Ports.CLIMBER_MOTOR);
    motor.configFactoryDefault();
    motor.setInverted(false);
  }
}
