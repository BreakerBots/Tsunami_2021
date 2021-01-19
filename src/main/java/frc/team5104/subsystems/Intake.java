package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.team5104.Constants;
import frc.team5104.Ports;
import frc.team5104.Superstructure;
import frc.team5104.Superstructure.Mode;
import frc.team5104.Superstructure.SystemState;
import frc.team5104.util.managers.Subsystem;

public class Intake extends Subsystem {
	private static VictorSPX motor;
	private static DoubleSolenoid piston;
	
	//Loop
	public void update() {
		setPiston(true);
		if (Superstructure.getSystemState() == SystemState.AUTOMATIC ||
			Superstructure.getSystemState() == SystemState.MANUAL) {
			if (Superstructure.getMode() == Mode.INTAKE) {
				setPercentOutput(Constants.INTAKE_SPEED);
			}
			else if (Superstructure.getMode() == Mode.SHOOTING) {
				setPercentOutput(Constants.INTAKE_FIRE_SPEED);
			}
			else setPercentOutput(-Constants.INTAKE_REJECT_SPEED);
		}
		else stop();
	}

	//Internal Functions
	public void setPiston(boolean position) {
		if (Constants.COMP_BOT)
			piston.set(position ? DoubleSolenoid.Value.kReverse : DoubleSolenoid.Value.kForward);
	}
	public void setPercentOutput(double percent) {
		motor.set(ControlMode.PercentOutput, percent);
	}
	public void stop() {
		setPiston(false);
		motor.set(ControlMode.Disabled, 0.0);
	}

	//Config
	public void init() {
		if (Constants.COMP_BOT)
			piston = new DoubleSolenoid(Ports.INTAKE_DEPLOYER_FORWARD, Ports.INTAKE_DEPLOYER_REVERSE);
			
		motor = new VictorSPX(Ports.INTAKE_MOTOR);
		motor.configFactoryDefault();
		motor.setInverted(true);
	}

	//Reset
	public void disabled() {
		stop();
	}
}