/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.simulation.DifferentialDrivetrainSim;
import edu.wpi.first.wpilibj.system.plant.DCMotor;
import edu.wpi.first.wpilibj.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.util.Units;
import frc.team5104.Constants;
import frc.team5104.Ports;
import frc.team5104.util.*;
import frc.team5104.util.managers.Subsystem;
import frc.team5104.util.setup.RobotState;

public class Drive extends Subsystem {
	private static BaseTalon falconL1, falconL2, falconR1, falconR2;
	private static DifferentialDrivetrainSim drivetrainSim;
	private static DriveEncoder leftEncoder, rightEncoder;
	private static Gyro gyro;
	
	//Update
	private static DriveSignal signal = new DriveSignal();
	public void update() {
		if (signal.unit == DriveSignal.DriveUnit.VOLTAGE) {
			setMotors(
				signal.leftSpeed / falconL1.getBusVoltage(),
				signal.rightSpeed / falconR1.getBusVoltage(),
				ControlMode.PercentOutput,
				signal.leftFeedForward,
				signal.rightFeedForward
			);
		}
		else {
			stopMotors();
		}

		if (RobotState.isSimulation())
			updateSim();
		signal = new DriveSignal();
	}
	public void updateSim() {
		((TalonSRXSim) falconL1).update();
		((TalonSRXSim) falconR1).update();

		drivetrainSim.setInputs(
				falconL1.getMotorOutputVoltage(),
				falconR1.getMotorOutputVoltage()
		);
		drivetrainSim.update(0.02);

		((DriveEncoderSim) leftEncoder).set(drivetrainSim.getLeftPositionMeters(), drivetrainSim.getLeftVelocityMetersPerSecond());
		((DriveEncoderSim) rightEncoder).set(drivetrainSim.getRightPositionMeters(), drivetrainSim.getRightVelocityMetersPerSecond());
		gyro.set(-drivetrainSim.getHeading().getDegrees());
	}
	
	//Internal Functions
	void setMotors(double leftSpeed, double rightSpeed, ControlMode controlMode, double leftFeedForward, double rightFeedForward) {
		falconL1.set(controlMode, leftSpeed, DemandType.ArbitraryFeedForward, leftFeedForward);
		falconR1.set(controlMode, rightSpeed, DemandType.ArbitraryFeedForward, rightFeedForward);
	}
	void stopMotors() {
		falconL1.set(ControlMode.Disabled, 0);
		falconR1.set(ControlMode.Disabled, 0);
	}
	
	//External Functions
	public static void set(DriveSignal signal) { Drive.signal = signal; }
	public static void stop() { signal = new DriveSignal(); }
	public static void resetGyro() {
		if (gyro != null)
			gyro.reset();
	}
	public static double getGyro() {
		return (gyro == null) ? 0 : gyro.get();
	}
	public static void resetEncoders() {
		if (leftEncoder != null) {
			leftEncoder.reset();
			rightEncoder.reset();
		}
	}
	public static DriveEncoder getLeftEncoder() {
		return leftEncoder;
	}
	public static DriveEncoder getRightEncoder() {
		return rightEncoder;
	}
	public static void reset() {
		resetGyro();
		resetEncoders();
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
		leftEncoder = new DriveEncoder(falconL1);
		rightEncoder = new DriveEncoder(falconR1);
		
		falconL1.configFactoryDefault();
		falconL2.configFactoryDefault();
		falconL1.config_kP(0, Constants.DRIVE_KP, 0);
		falconL1.config_kD(0, Constants.DRIVE_KD, 0);
		falconL2.set(ControlMode.Follower, falconL1.getDeviceID());
		
		falconR1.configFactoryDefault();
		falconR2.configFactoryDefault();
		falconR1.config_kP(0, Constants.DRIVE_KP, 0);
		falconR1.config_kD(0, Constants.DRIVE_KD, 0);
		falconR2.set(ControlMode.Follower, falconR1.getDeviceID());
		falconR1.setInverted(true);
		falconR2.setInverted(true);
		
		stopMotors();
		reset();
	}
	public void initSim() {
		falconL1 = new TalonSRXSim(Ports.DRIVE_MOTOR_L1);
		falconR1 = new TalonSRXSim(Ports.DRIVE_MOTOR_R1);
		leftEncoder = new DriveEncoderSim((TalonSRXSim) falconL1);
		rightEncoder = new DriveEncoderSim((TalonSRXSim) falconR1);
		gyro = new Gyro.GyroSim();

		if (!RobotState.isSimulation())
			falconR1.setInverted(true);

		drivetrainSim = new DifferentialDrivetrainSim(
				LinearSystemId.identifyDrivetrainSystem(
						1.98, //TODO: GET ACTUAL MEASUREMENT
						0.2, //TODO: GET ACTUAL MEASUREMENT
						1.5, //TODO: GET ACTUAL MEASUREMENT
						0.3), //TODO: GET ACTUAL MEASUREMENT
				DCMotor.getFalcon500(2),
				Constants.DRIVE_TICKS_PER_REV / 2048.0,
				edu.wpi.first.wpilibj.util.Units.feetToMeters(Constants.DRIVE_TRACK_WIDTH),
				Units.feetToMeters(Constants.DRIVE_WHEEL_DIAMETER) / 2.0,
				null//VecBuilder.fill(0, 0, 0.0001, 0.1, 0.1, 0.005, 0.005) //TODO: GET ACTUAL MEASUREMENT
		);

		stopMotors();
		reset();
	}
	
	//Reset
	public void disabled() {
		signal = new DriveSignal();
	}
}