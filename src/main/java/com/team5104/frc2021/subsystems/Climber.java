package com.team5104.frc2021.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.team5104.frc2021.Controls;
import com.team5104.frc2021.Ports;
import com.team5104.frc2021.Superstructure;
import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.lib.console;
import com.team5104.lib.devices.Solenoid;
import com.team5104.lib.subsystem.Subsystem;

public class Climber extends Subsystem {
  private Solenoid deployerPiston, brakePiston;
  private TalonFX motor;

  //Loop
  public void update() {

    if (Superstructure.is(Mode.BRAKE_IT_DOWN)) {
      console.log("in brake mode");
    }
    if (Superstructure.isEnabled() && Superstructure.isClimbing()) {
      setFiniteState("Climbing");
      brakePiston.set(false);
      deployerPiston.set(Superstructure.is(Mode.CLIMBER_DEPLOYING));
      motor.set(ControlMode.PercentOutput, Controls.CLIMBER_WINCH.get());
    }
    else if (Superstructure.isEnabled() && Superstructure.is(Mode.BRAKE_IT_DOWN)) {
      brakePiston.set(true);
      console.log("Piston is broken ;-;");
      Superstructure.set(Mode.IDLE);
      console.log("Idling after braking.");
    }
    else {
      setFiniteState("Stopped");
      brakePiston.set(true);
      deployerPiston.set(false);
      stop();
    }
  }

  //Config
  public Climber() {
    super(null);

    deployerPiston = new Solenoid(Ports.CLIMBER_DEPLOYER);
    brakePiston = new Solenoid(Ports.CLIMBER_BRAKE, true);
    motor = new TalonFX(Ports.CLIMBER_MOTOR);
    motor.configFactoryDefault();
    motor.setInverted(false);
  }
}
