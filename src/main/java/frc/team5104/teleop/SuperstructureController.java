package frc.team5104.teleop;

import frc.team5104.Controls;
import frc.team5104.Superstructure;
import frc.team5104.Superstructure.FlywheelState;
import frc.team5104.Superstructure.Mode;
import frc.team5104.Superstructure.PanelState;
import frc.team5104.Superstructure.Target;
import frc.team5104.subsystems.Climber;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.managers.TeleopController;

public class SuperstructureController extends TeleopController {
	protected void update() {
		// Idle
		if (Controls.IDLE.get()) {
			Superstructure.setMode(Mode.IDLE);
			Superstructure.setFlywheelState(FlywheelState.STOPPED);
			console.log(c.SUPERSTRUCTURE, "idling");
		}

		// Panel
		if (Controls.PANEL_DEPLOY.get()) {
			if (Superstructure.getMode() == Mode.PANEL_DEPLOYING || Superstructure.getMode() == Mode.PANELING) {
				Superstructure.setMode(Mode.IDLE);
				console.log(c.SUPERSTRUCTURE, "manually exiting paneling... idling");
			} else {
				Superstructure.setMode(Mode.PANEL_DEPLOYING);
				console.log(c.SUPERSTRUCTURE, "deploying paneler");
			}
		}
		if (Controls.PANEL_SPIN.get()) {
			if (Superstructure.getMode() == Mode.PANEL_DEPLOYING) {
				Superstructure.setMode(Mode.PANELING);
				console.log(c.SUPERSTRUCTURE, "panel starting " + Superstructure.getPanelState().toString());
			} else if (Superstructure.getMode() == Mode.PANELING) {
				Superstructure.setMode(Mode.PANEL_DEPLOYING);
				console.log(c.SUPERSTRUCTURE, "deploying paneler");
			}
		}
		if (Controls.PANEL_POSITION.get()) {
			Superstructure.setPanelState(PanelState.POSITION);
			console.log(c.SUPERSTRUCTURE, "setting panel mode to position");
		}
		if (Controls.PANEL_ROTATION.get()) {
			Superstructure.setPanelState(PanelState.ROTATION);
			console.log(c.SUPERSTRUCTURE, "setting panel mode to rotation");
		}

		// Intake
		if (Controls.INTAKE.get()) {
			if (Superstructure.getMode() == Mode.INTAKE) {
				Superstructure.setMode(Mode.IDLE);
				console.log(c.SUPERSTRUCTURE, "exiting intake... idling");
			} else {
				Superstructure.setMode(Mode.INTAKE);
				console.log(c.SUPERSTRUCTURE, "intaking");
			}
		}

		//Shooter
		if (Controls.SHOOT.get()) {
			if (Superstructure.getMode() == Mode.SHOOTING || Superstructure.getMode() == Mode.AIMING) {
				Superstructure.setMode(Mode.IDLE);
				Superstructure.setFlywheelState(FlywheelState.STOPPED);
				console.log(c.SUPERSTRUCTURE, "exiting shooting... idling");
			}
			else {
				Superstructure.setMode(Mode.AIMING);
				console.log(c.SUPERSTRUCTURE, "aiming");
			}
		}
		if (Controls.CHARGE_FLYWHEEL.get()) {
			if (Superstructure.getFlywheelState() == FlywheelState.STOPPED) {
				console.log(c.SUPERSTRUCTURE, "charging flywheel");
				Superstructure.setFlywheelState(FlywheelState.SPINNING);
			}
			else {
				console.log(c.SUPERSTRUCTURE, "stopped charging flywheel");
				Superstructure.setFlywheelState(FlywheelState.STOPPED);
			}
		}
		if (Controls.SHOOT_LOW.get()) {
			console.log(c.SUPERSTRUCTURE, "setting target to low");
			Superstructure.setTarget(Target.LOW);
		}
		if (Controls.SHOOT_HIGH.get()) {
			console.log(c.SUPERSTRUCTURE, "setting target to high");
			Superstructure.setTarget(Target.HIGH);
		}

		// Climb
		if (Controls.CLIMBER_DEPLOY.get()) {
			if (Superstructure.getMode() == Mode.CLIMBING) {
				Superstructure.setMode(Mode.IDLE);
				console.log(c.SUPERSTRUCTURE, "stopping climb... idling");
			}
			else if (Superstructure.getMode() == Mode.CLIMBER_DEPLOYING) {
				Superstructure.setMode(Mode.CLIMBING);
				console.log(c.SUPERSTRUCTURE, "climbing!");
			}
			else {
				Superstructure.setMode(Mode.CLIMBER_DEPLOYING);
				console.log(c.SUPERSTRUCTURE, "deploying climber!!!!");
			}
		}
		Climber.climberManual = Controls.CLIMBER_WINCH.get() + Controls.CLIMBER_WINCH_OP.get();
	}
}
