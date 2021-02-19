/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.frc2021.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.team5104.frc2021.Constants;
import com.team5104.frc2021.Ports;
import com.team5104.frc2021.teleop.DriveController.DriveSignal;
import com.team5104.frc2021.teleop.DriveController.DriveSignal.DriveUnit;
import com.team5104.lib.managers.Subsystem;
import com.team5104.lib.motion.TalonSim;
import com.team5104.lib.sensors.Encoder;
import com.team5104.lib.sensors.Encoder.EncoderSim;
import com.team5104.lib.sensors.Encoder.FalconEncoder;
import com.team5104.lib.sensors.Gyro;
import com.team5104.lib.setup.RobotState;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.simulation.DifferentialDrivetrainSim;
import edu.wpi.first.wpilibj.system.plant.DCMotor;
import edu.wpi.first.wpilibj.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.util.Units;

public class Drive extends Subsystem {
	private static BaseTalon falconL1, falconL2, falconR1, falconR2;
	private static DifferentialDrivetrainSim drivetrainSim;
	private static Encoder leftEncoder, rightEncoder;
	private static Gyro gyro;
	
	//Update
	private static DriveSignal signal = new DriveSignal();
	public void update() {
		if (RobotState.isEnabled() && signal.unit == DriveSignal.DriveUnit.VOLTAGE) {
			setMotors(
				signal.leftSpeed / falconL1.getBusVoltage(),
				signal.rightSpeed / falconR1.getBusVoltage(),
				ControlMode.PercentOutput);
		}
		else {
			stopMotors();
		}

		signal = new DriveSignal();
	}
	public void updateSim() {
		((TalonSim) falconL1).update();
		((TalonSim) falconR1).update();

		drivetrainSim.setInputs(
				falconL1.getMotorOutputVoltage(),
				falconR1.getMotorOutputVoltage()
		);
		drivetrainSim.update(0.02);

		((EncoderSim) leftEncoder).setVelocityAccelMeters(
				drivetrainSim.getLeftPositionMeters(),
				drivetrainSim.getLeftVelocityMetersPerSecond(),
				Constants.drive.WHEEL_DIAMETER
		);
		((EncoderSim) rightEncoder).setVelocityAccelMeters(
				drivetrainSim.getRightPositionMeters(),
				drivetrainSim.getRightVelocityMetersPerSecond(),
				Constants.drive.WHEEL_DIAMETER
		);
		gyro.set(-drivetrainSim.getHeading().getDegrees());
	}
	
	//Internal Functions
	void setMotors(double leftSpeed, double rightSpeed, ControlMode controlMode) {
		falconL1.set(controlMode, leftSpeed);
		falconR1.set(controlMode, rightSpeed);
	}
	void stopMotors() {
		falconL1.set(ControlMode.Disabled, 0);
		falconR1.set(ControlMode.Disabled, 0);
	}
	
	//External Functions
	public static void set(DriveSignal signal) { com.team5104.frc2021.subsystems.Drive.signal = signal; }
	public static void stop() { signal = new DriveSignal(); }
	public static double getHeading() {
		return (gyro == null) ? 0 : gyro.get();
	}
	public static double[] getDriveStateMeters() {
		return new double[] {
			leftEncoder.getComponentRevs() * Units.feetToMeters(Constants.drive.WHEEL_DIAMETER) * Math.PI,
			rightEncoder.getComponentRevs() * Units.feetToMeters(Constants.drive.WHEEL_DIAMETER) * Math.PI,
			leftEncoder.getComponentRPS() * Units.feetToMeters(Constants.drive.WHEEL_DIAMETER) * Math.PI,
			rightEncoder.getComponentRPS() * Units.feetToMeters(Constants.drive.WHEEL_DIAMETER) * Math.PI
		};
	}
	public static void reset() {
		if (gyro != null)
			gyro.reset();
		if (leftEncoder != null) {
			leftEncoder.reset();
			rightEncoder.reset();
		}
		if (RobotState.isSimulation())
			drivetrainSim.setPose(new Pose2d());
	}
	
	//Config
	public void init() {
		falconL1 = new TalonFX(Ports.DRIVE_MOTOR_L1);
		falconL2 = new TalonFX(Ports.DRIVE_MOTOR_L2);
		falconR1 = new TalonFX(Ports.DRIVE_MOTOR_R1);
		falconR2 = new TalonFX(Ports.DRIVE_MOTOR_R2);
		gyro = new Gyro.GyroPigeon(Ports.DRIVE_GYRO);
		leftEncoder = new FalconEncoder((TalonFX) falconL1, Constants.drive.GEARING);
		rightEncoder = new FalconEncoder((TalonFX) falconR1, Constants.drive.GEARING);
		
		falconL1.configFactoryDefault();
		falconL2.configFactoryDefault();
		falconL2.set(ControlMode.Follower, falconL1.getDeviceID());
		
		falconR1.configFactoryDefault();
		falconR2.configFactoryDefault();
		falconR2.set(ControlMode.Follower, falconR1.getDeviceID());
		falconR1.setInverted(true);
		falconR2.setInverted(true);

		configCharacterization(
				() -> leftEncoder.getComponentRevs(),
				() -> leftEncoder.getComponentRPS(),
				() -> rightEncoder.getComponentRevs(),
				() -> rightEncoder.getComponentRPS(),
				() -> gyro.getRadians(),
				(Double left, Double right) -> set(new DriveSignal(left, right, DriveUnit.VOLTAGE))
		);

		stopMotors();
		reset();
	}
	public void initSim() {
		falconL1 = new TalonSim(Ports.DRIVE_MOTOR_L1);
		falconR1 = new TalonSim(Ports.DRIVE_MOTOR_R1);
		leftEncoder = new EncoderSim((TalonSim) falconL1, Constants.drive.GEARING);
		rightEncoder = new EncoderSim((TalonSim) falconR1, Constants.drive.GEARING);
		gyro = new Gyro.GyroSim();

		if (!RobotState.isSimulation())
			falconR1.setInverted(true);

		drivetrainSim = new DifferentialDrivetrainSim(
				LinearSystemId.identifyDrivetrainSystem(
						Constants.drive.KLV,
						Constants.drive.KLA,
						Constants.drive.KAV,
						Constants.drive.KAA), //TODO: GET ACTUAL MEASUREMENT
				DCMotor.getFalcon500(2),
				Constants.drive.GEARING,
				edu.wpi.first.wpilibj.util.Units.feetToMeters(Constants.drive.TRACK_WIDTH),
				Units.feetToMeters(Constants.drive.WHEEL_DIAMETER) / 2.0,
				null//VecBuilder.fill(0, 0, 0.0001, 0.1, 0.1, 0.005, 0.005) //TODO: GET ACTUAL MEASUREMENT
		);

		configCharacterization(
				() -> leftEncoder.getComponentRevs(),
				() -> leftEncoder.getComponentRPS(),
				() -> rightEncoder.getComponentRevs(),
				() -> rightEncoder.getComponentRPS(),
				() -> gyro.getRadians(),
				(Double left, Double right) -> set(new DriveSignal(left, right, DriveUnit.VOLTAGE))
		);

		stopMotors();
		reset();
	}
	
	//Reset
	public void disabled() {
		stop();
	}
}
