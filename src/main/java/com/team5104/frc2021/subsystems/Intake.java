package com.team5104.frc2021.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team5104.frc2021.Constants;
import com.team5104.frc2021.Ports;
import com.team5104.frc2021.Superstructure;
import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.frc2021.teleop.SuperstructureController;
import com.team5104.lib.devices.Solenoid;
import com.team5104.lib.subsystem.Subsystem;

public class Intake extends Subsystem {
  private VictorSPX motor;
  private Solenoid piston;

  //Loop
  public void update() {
    setPiston(true);
    if (Superstructure.isEnabled()) {
      if (Superstructure.is(Mode.INTAKING)) {
        setFiniteState("Intaking");
//        System.out.println("intaking pls?");
        setPercentOutput(Constants.intake.INTAKING_SPEED);
      }
      else if (Superstructure.is(Mode.SHOOTING)) {
        setFiniteState("Slow (firing)");
        setPercentOutput(Constants.intake.FIRING_SPEED);
      }
      else {
        setFiniteState("Rejecting");
        setPercentOutput(-Constants.intake.REJECTING_SPEED);
      }
    }
    else {
      setFiniteState("Stopped");
      stop();
    }
  }

  //Internal Functions
  public void setPiston(boolean position) {
    if (Constants.robot.id == 0)
      piston.set(position);
  }
  public void setPercentOutput(double percent) {
    motor.set(ControlMode.PercentOutput, percent);
  }

  //Config
  public Intake() {
    super(Constants.intake);

    if (Constants.robot.id == 0)
      piston = new Solenoid(Ports.INTAKE_DEPLOYER, true);

    motor = new VictorSPX(Ports.INTAKE_MOTOR);
    motor.configFactoryDefault();
    motor.setInverted(true);
  }
}
