package com.team5104.frc2021.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team5104.frc2021.Constants;
import com.team5104.frc2021.Ports;
import com.team5104.frc2021.Superstructure;
import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.lib.managers.Subsystem;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Intake extends Subsystem {
  private static VictorSPX motor;
  private static DoubleSolenoid piston;

  // Loop
  public void update() {
    setPiston(true);
    if (Superstructure.isEnabled()) {
      if (Superstructure.is(Mode.INTAKE)) {
        setPercentOutput(Constants.intake.INTAKING_SPEED);
      } else if (Superstructure.is(Mode.SHOOTING)) {
        setPercentOutput(Constants.intake.FIRING_SPEED);
      } else setPercentOutput(-Constants.intake.REJECTING_SPEED);
    } else stop();
  }

  // Internal Functions
  public void setPiston(boolean position) {
    if (Constants.robot.id == 0)
      piston.set(position ? DoubleSolenoid.Value.kReverse : DoubleSolenoid.Value.kForward);
  }

  public void setPercentOutput(double percent) {
    motor.set(ControlMode.PercentOutput, percent);
  }

  public void stop() {
    setPiston(false);
    motor.set(ControlMode.Disabled, 0.0);
  }

  // Config
  public void init() {
    if (Constants.robot.id == 0)
      piston = new DoubleSolenoid(Ports.INTAKE_DEPLOYER[0], Ports.INTAKE_DEPLOYER[1]);

    motor = new VictorSPX(Ports.INTAKE_MOTOR);
    motor.configFactoryDefault();
    motor.setInverted(true);
  }

  // Reset
  public void disabled() {
    stop();
  }
}
