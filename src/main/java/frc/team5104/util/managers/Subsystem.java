/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util.managers;

import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import frc.team5104.util.Encoder;
import frc.team5104.util.console;
import frc.team5104.util.console.c;

import java.util.function.BiConsumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

/** 
 * A snickers wrapper of all the requirements of a subsystem. 
 */
public abstract class Subsystem {
	protected boolean attached, emergencyStopped, calibrating, debugging, characterizing, canCharacterize;
	private long calibrationStart;
	
	/** Called periodically from the robot loop */
	public abstract void update();

	/** Called periodically at 100hz always */
	public void fastUpdate() { }

	/** Called when robots boots up; initialize devices here */
	public abstract void init();

	/** Called whenever the robot becomes enabled or disabled. Stop motors here. */
	public abstract void disabled();
	
	/** Labels this subsystem as emergency stopped. Stops calling the update() and fastUpdate() methods and instead
	 * spam calls disabled(). */
	public void emergencyStop() {
		console.error(c.MAIN, "Emergency stopped the " + this.getClass().getSimpleName());
		emergencyStopped = true;
	}

	//Sim
	/** Called periodically from the robot loop (along with update() and fastUpdate()) while in simulation
	 * @default nothing */
	public void updateSim() { }

	/** Called when robots boots up in simulation; initialize devices here.
	 * @warning this replaces init() while in simulation
	 * @default calls init() method */
	public void initSim() {
		init();
	}

	//Debugging
	/** Labels this subsystem as debugging, starts calling the debug method */
	public void startDebugging() {
		debugging = true;
	}

	/** Called periodically while the subsystem is in debug mode */
	public void debug() { }

	//Calibrating
	/** Returns if this subsystem is labeled as calibrating */
	public boolean isCalibrating() {
		return calibrating;
	}
	
	/** Labels this subsystem as calibrating */
	public void startCalibrating() {
		calibrating = true;
		calibrationStart = System.currentTimeMillis();
	}
	
	/** Labels this subsystem as not calibrating */
	public void stopCalibrating() {
		calibrating = false;
	}
	
	/** Returns how long this subsystem has been calibrating for (in milliseconds) */
	public long getTimeInCalibration() {
		if (isCalibrating())
			return System.currentTimeMillis() - calibrationStart;
		else return 0;
	}

	//Characterization
	private DoubleSupplier getLeftEncoderPos, getLeftEncoderVel, getRightEncoderPos, getRightEncoderVel,
							getGyroAngleRadians;
	private BiConsumer<Double, Double> setOutputVoltage;
	private double lastOutputVoltage;
	protected void configCharacterization(Encoder encoder, DoubleConsumer outputVoltage) {
		configCharacterization(() -> encoder.getComponentRevs(), () -> encoder.getComponentRPS(), outputVoltage);
	}
	protected void configCharacterization(DoubleSupplier encoderPos, DoubleSupplier encoderVel,
	                                      DoubleConsumer outputVoltage) {
		configCharacterization(encoderPos, encoderVel, encoderPos, encoderVel,
		                       () -> 0, (Double l, Double r) -> { outputVoltage.accept(l); });
	}
	protected void configCharacterization(DoubleSupplier leftEncoderPos, DoubleSupplier leftEncoderVel,
	                                      DoubleSupplier rightEncoderPos, DoubleSupplier rightEncoderVel,
	                                      DoubleSupplier gyroAngleRadians,
	                                      BiConsumer<Double, Double> outputVoltage) {
		canCharacterize = true;
		getLeftEncoderPos = leftEncoderPos;
		getLeftEncoderVel = leftEncoderVel;
		getRightEncoderPos = rightEncoderPos;
		getRightEncoderVel = rightEncoderVel;
		getGyroAngleRadians = gyroAngleRadians;
		setOutputVoltage = outputVoltage;
	}
	protected boolean isCharacterizing() {
		return characterizing;
	}
	public double[] getCharacterization(double percentOutput, boolean shouldRotate) {
		if (!canCharacterize)
			console.error(c.MAIN, "You have not set up characterization for this setup. Call the configCharacterization() method in the init() method add an `if (isCharacterizing())` statement to your update() method so it doesnt move motors.");

		characterizing = true;

		double batteryVoltage = RobotController.getBatteryVoltage();
		setOutputVoltage.accept((shouldRotate ? -1 : 1) * percentOutput, percentOutput);

		double[] returnArray = new double[] {
				Timer.getFPGATimestamp(),
				batteryVoltage,
				percentOutput,
				lastOutputVoltage,
				lastOutputVoltage,
				getLeftEncoderPos.getAsDouble(),
				getRightEncoderPos.getAsDouble(),
				getLeftEncoderVel.getAsDouble(),
				getRightEncoderVel.getAsDouble(),
				getGyroAngleRadians.getAsDouble()
		};

		lastOutputVoltage = percentOutput * batteryVoltage;

		return returnArray;
	}
}