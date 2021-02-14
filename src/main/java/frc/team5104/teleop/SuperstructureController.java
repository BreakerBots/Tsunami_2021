package frc.team5104.teleop;

import frc.team5104.Controls;
import frc.team5104.Superstructure;
import frc.team5104.Superstructure.FlywheelState;
import frc.team5104.Superstructure.Mode;
import frc.team5104.Superstructure.PanelState;
import frc.team5104.Superstructure.Target;
import frc.team5104.subsystems.Climber;
import frc.team5104.util.console;
import frc.team5104.util.managers.TeleopController;

public class SuperstructureController extends TeleopController {
	protected void update() {
		// Idle
		if (Controls.IDLE.get()) {
			Superstructure.set(Mode.IDLE);
			Superstructure.set(FlywheelState.STOPPED);
			console.log("idling");
		}

		// Panel
		if (Controls.PANEL_DEPLOY.get()) {
			if (Superstructure.is(Mode.PANEL_DEPLOYING) || Superstructure.is(Mode.PANELING)) {
				Superstructure.set(Mode.IDLE);
				console.log("manually exiting paneling... idling");
			} else {
				Superstructure.set(Mode.PANEL_DEPLOYING);
				console.log("deploying paneler");
			}
		}
		if (Controls.PANEL_SPIN.get()) {
			if (Superstructure.is(Mode.PANEL_DEPLOYING)) {
				Superstructure.set(Mode.PANELING);
				if (Superstructure.is(PanelState.POSITION))
					console.log("panel running position control");
				else console.log("panel running rotation control");
			} else if (Superstructure.is(Mode.PANELING)) {
				Superstructure.set(Mode.PANEL_DEPLOYING);
				console.log("deploying paneler");
			}
		}
		if (Controls.PANEL_POSITION.get()) {
			Superstructure.set(PanelState.POSITION);
			console.log("setting panel mode to position");
		}
		if (Controls.PANEL_ROTATION.get()) {
			Superstructure.set(PanelState.ROTATION);
			console.log("setting panel mode to rotation");
		}

		// Intake
		if (Controls.INTAKE.get()) {
			if (Superstructure.is(Mode.INTAKE)) {
				Superstructure.set(Mode.IDLE);
				console.log("exiting intake... idling");
			} else {
				Superstructure.set(Mode.INTAKE);
				console.log("intaking");
			}
		}

		//Shooter
		if (Controls.SHOOT.get()) {
			if (Superstructure.is(Mode.SHOOTING) || Superstructure.is(Mode.AIMING)) {
				Superstructure.set(Mode.IDLE);
				Superstructure.set(FlywheelState.STOPPED);
				console.log("exiting shooting... idling");
			}
			else {
				Superstructure.set(Mode.AIMING);
				console.log("aiming");
			}
		}
		if (Controls.CHARGE_FLYWHEEL.get()) {
			if (Superstructure.is(FlywheelState.STOPPED)) {
				console.log("charging flywheel");
				Superstructure.set(FlywheelState.SPINNING);
			}
			else {
				console.log("stopped charging flywheel");
				Superstructure.set(FlywheelState.STOPPED);
			}
		}
		if (Controls.SHOOT_LOW.get()) {
			console.log("setting target to low");
			Superstructure.set(Target.LOW);
		}
		if (Controls.SHOOT_HIGH.get()) {
			console.log("setting target to high");
			Superstructure.set(Target.HIGH);
		}

		// Climb
		if (Controls.CLIMBER_DEPLOY.get()) {
			if (Superstructure.is(Mode.CLIMBING)) {
				Superstructure.set(Mode.IDLE);
				console.log("stopping climb... idling");
			}
			else if (Superstructure.is(Mode.CLIMBER_DEPLOYING)) {
				Superstructure.set(Mode.CLIMBING);
				console.log("climbing!");
			}
			else {
				Superstructure.set(Mode.CLIMBER_DEPLOYING);
				console.log("deploying climber!!!!");
			}
		}
		Climber.climberManual = Controls.CLIMBER_WINCH.get();
	}
}
