/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.sensors.PigeonIMU;

import frc.team5104.Constants;
import frc.team5104.Ports;
import frc.team5104.util.DriveSignal;
import frc.team5104.util.Tuner;
import frc.team5104.util.DriveEncoder;
import frc.team5104.util.managers.Subsystem;

public class Drive extends Subsystem {
	private static TalonFX falconL1, falconL2, falconR1, falconR2;
	private static DriveEncoder leftEncoder, rightEncoder;
	private static PigeonIMU gyro;
	
	//Update
	private static DriveSignal currentDriveSignal = new DriveSignal();
	public void update() {
		//Competition Debugging
		if (Constants.AT_COMP) {
			Tuner.setTunerOutput("Drive Left Side", currentDriveSignal.leftSpeed);
			Tuner.setTunerOutput("Drive Right Side", currentDriveSignal.rightSpeed);
		}
			
		switch (currentDriveSignal.unit) {
			case PERCENT_OUTPUT: {
				setMotors(
						currentDriveSignal.leftSpeed, 
						currentDriveSignal.rightSpeed, 
						ControlMode.PercentOutput,
						currentDriveSignal.leftFeedForward,
						currentDriveSignal.rightFeedForward
					);
				break;
			}
			case FEET_PER_SECOND: {
				setMotors(
						DriveEncoder.feetPerSecondToTalonVel(currentDriveSignal.leftSpeed), 
						DriveEncoder.feetPerSecondToTalonVel(currentDriveSignal.rightSpeed), 
						ControlMode.Velocity,
						currentDriveSignal.leftFeedForward,
						currentDriveSignal.rightFeedForward
					);
				break;
			}
			case VOLTAGE: {
				setMotors(
						currentDriveSignal.leftSpeed / getLeftGearboxVoltage(),
						currentDriveSignal.rightSpeed / getRightGearboxVoltage(),
						ControlMode.PercentOutput,
						currentDriveSignal.leftFeedForward,
						currentDriveSignal.rightFeedForward
					);
				break;
			}
			case STOP:
				stopMotors();
				break;
		}
		currentDriveSignal = new DriveSignal();
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
	public static void set(DriveSignal signal) { currentDriveSignal = signal; }
	public static void stop() { currentDriveSignal = new DriveSignal(); }
	public static double getLeftGearboxVoltage() { return falconL1.getBusVoltage(); }
	public static double getRightGearboxVoltage() { return falconR1.getBusVoltage(); }
	public static double getLeftGearboxOutputVoltage() { return falconL1.getMotorOutputVoltage(); }
	public static double getRightGearboxOutputVoltage() { return falconR1.getMotorOutputVoltage(); }
	public static void resetGyro() {
		if (gyro != null)
			gyro.setFusedHeading(0);
	}
	public static double getGyro() {
		if (gyro != null)
			return gyro.getFusedHeading() * (Constants.COMP_BOT ? 1 : 1);
		else return 0;
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
	
	//Config
	public void init() {
		falconL1 = new TalonFX(Ports.DRIVE_MOTOR_L1);
		falconL2 = new TalonFX(Ports.DRIVE_MOTOR_L2);
		falconR1 = new TalonFX(Ports.DRIVE_MOTOR_R1);
		falconR2 = new TalonFX(Ports.DRIVE_MOTOR_R2);
		gyro = new PigeonIMU(Ports.DRIVE_GYRO);
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
		resetGyro();
		resetEncoders();
	}
	
	//Reset
	public void disabled() {
		currentDriveSignal = new DriveSignal();
	}
}