/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.simulation.DifferentialDrivetrainSim;
import edu.wpi.first.wpilibj.system.plant.DCMotor;
import edu.wpi.first.wpilibj.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.util.Units;
import frc.team5104.Constants;
import frc.team5104.Ports;
import frc.team5104.util.DriveSignal;
import frc.team5104.util.Encoder;
import frc.team5104.util.Encoder.EncoderSim;
import frc.team5104.util.Encoder.FalconEncoder;
import frc.team5104.util.Gyro;
import frc.team5104.util.TalonSim;
import frc.team5104.util.managers.Subsystem;
import frc.team5104.util.setup.RobotState;

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
				Constants.drive.wheelDiameter
		);
		((EncoderSim) rightEncoder).setVelocityAccelMeters(
				drivetrainSim.getRightPositionMeters(),
				drivetrainSim.getRightVelocityMetersPerSecond(),
				Constants.drive.wheelDiameter
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
	public static void set(DriveSignal signal) { Drive.signal = signal; }
	public static void stop() { signal = new DriveSignal(); }
	public static double getHeading() {
		return (gyro == null) ? 0 : gyro.get();
	}
	public static double[] getDriveStateMeters() {
		return new double[] {
			leftEncoder.getComponentRevs() * Units.feetToMeters(Constants.drive.wheelDiameter) * Math.PI,
			rightEncoder.getComponentRevs() * Units.feetToMeters(Constants.drive.wheelDiameter) * Math.PI,
			leftEncoder.getComponentRPS() * Units.feetToMeters(Constants.drive.wheelDiameter) * Math.PI,
			rightEncoder.getComponentRPS() * Units.feetToMeters(Constants.drive.wheelDiameter) * Math.PI
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
		leftEncoder = new FalconEncoder((TalonFX) falconL1, Constants.drive.gearing);
		rightEncoder = new FalconEncoder((TalonFX) falconR1, Constants.drive.gearing);
		
		falconL1.configFactoryDefault();
		falconL2.configFactoryDefault();
		falconL2.set(ControlMode.Follower, falconL1.getDeviceID());
		
		falconR1.configFactoryDefault();
		falconR2.configFactoryDefault();
		falconR2.set(ControlMode.Follower, falconR1.getDeviceID());
		falconR1.setInverted(true);
		falconR2.setInverted(true);
		
		stopMotors();
		reset();
	}
	public void initSim() {
		falconL1 = new TalonSim(Ports.DRIVE_MOTOR_L1);
		falconR1 = new TalonSim(Ports.DRIVE_MOTOR_R1);
		leftEncoder = new EncoderSim((TalonSim) falconL1, Constants.drive.gearing);
		rightEncoder = new EncoderSim((TalonSim) falconR1, Constants.drive.gearing);
		gyro = new Gyro.GyroSim();

		if (!RobotState.isSimulation())
			falconR1.setInverted(true);

		drivetrainSim = new DifferentialDrivetrainSim(
				LinearSystemId.identifyDrivetrainSystem(
						Constants.drive.kLV,
						Constants.drive.kLA,
						Constants.drive.kAV,
						Constants.drive.kAA), //TODO: GET ACTUAL MEASUREMENT
				DCMotor.getFalcon500(2),
				Constants.drive.gearing,
				edu.wpi.first.wpilibj.util.Units.feetToMeters(Constants.drive.trackWidth),
				Units.feetToMeters(Constants.drive.wheelDiameter) / 2.0,
				null//VecBuilder.fill(0, 0, 0.0001, 0.1, 0.1, 0.005, 0.005) //TODO: GET ACTUAL MEASUREMENT
		);

		stopMotors();
		reset();
	}
	
	//Reset
	public void disabled() {
		stop();
	}
}