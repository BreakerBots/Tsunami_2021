package frc.team5104.util;


import frc.team5104.Constants;

public class DriveEncoderSim extends DriveEncoder {
	private TalonSRXSim talon;

	//Constructor
	public DriveEncoderSim(TalonSRXSim talon) {
		super(talon);
		this.talon = talon;
		reset();
	}

	//Reset
	public void reset() {
		talon.setSelectedSensorPosition(0);
	}

	//Set
	public void set(double posMeters, double velMetersSecond) {
		talon.getSimCollection().setQuadratureRawPosition((int) feetToTicks(Units.metersToFeet(posMeters)));
		talon.getSimCollection().setQuadratureVelocity((int) feetPerSecondToTalonVel(Units.metersToFeet(velMetersSecond)));
	}

	//Velocity
	public double getVelocityMetersSecond() {
		return Units.feetToMeters(getVelocityFeetSecond());
	}
	public double getVelocityFeetSecond() {
		return talonVelToFeetPerSecond(getVelocityRaw());
	}
	public double getVelocityRaw() {
		return talon.getSelectedSensorVelocity();
	}

	//Position
	public double getPositionMeters() {
		return Units.feetToMeters(getPositionFeet());
	}
	public double getPositionFeet() {
		return ticksToFeet(getPositionRaw());
	}
	public double getPositionRaw() {
		return talon.getSelectedSensorPosition();
	}

	//Conversions
	public static double ticksToWheelRevolutions(double ticks) {
		return ticks / (Constants.DRIVE_TICKS_PER_REV * 2);
	}
	public static double wheelRevolutionsToTicks(double revs) {
		return revs * (Constants.DRIVE_TICKS_PER_REV * 2);
	}
	public static double feetToWheelRevolutions(double feet) {
		return feet / (Constants.DRIVE_WHEEL_DIAMETER * Math.PI);
	}
	public static double wheelRevolutionsToFeet(double revs) {
		return revs * (Constants.DRIVE_WHEEL_DIAMETER * Math.PI);
	}
	public static double ticksToFeet(double ticks) {
		return wheelRevolutionsToFeet(ticksToWheelRevolutions(ticks));
	}
	public static double feetToTicks(double feet) {
		return wheelRevolutionsToTicks(feetToWheelRevolutions(feet));
	}
	public static double talonVelToFeetPerSecond(double talonVel) {
		return ticksToFeet(talonVel) * 10.0;
	}
	public static double feetPerSecondToTalonVel(double feetPerSecond) {
		return feetToTicks(feetPerSecond) / 10.0;
	}
}
