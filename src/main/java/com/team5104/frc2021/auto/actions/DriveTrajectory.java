/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.frc2021.auto.actions;

import com.team5104.frc2021.Constants;
import com.team5104.frc2021.subsystems.Drive;
import com.team5104.frc2021.teleop.DriveController.DriveSignal;
import com.team5104.frc2021.teleop.DriveController.DriveSignal.DriveUnit;
import com.team5104.lib.auto.AutoAction;
import com.team5104.lib.auto.Odometry;
import com.team5104.lib.auto.Position;
import com.team5104.lib.console;
import com.team5104.lib.console.Set;
import com.team5104.lib.dashboard.DashboardTrajectory;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.Trajectory.State;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj.util.Units;

import java.util.List;

/**
 * Follow a trajectory using the Breaker Trajectory Follower (Ramses Follower)
 */
public class DriveTrajectory extends AutoAction {

  public static final double CORRECTION_FACTOR = 1/*1*/; //>0
  public static final double DAMPENING_FACTOR = 0.3/*0.5*/; //0-1

  private final Timer timer = new Timer();
  private final Trajectory trajectory;
  private final RamseteController follower;
  private final SimpleMotorFeedforward feedforward;
  private final DifferentialDriveKinematics kinematics;
  private final PIDController leftController, rightController;
  private DifferentialDriveWheelSpeeds lastSpeeds;
  private double lastTime;

  public DriveTrajectory(boolean isReversed, Position... waypoints) {
    feedforward = new SimpleMotorFeedforward(
        Constants.drive.KLS,
        Constants.drive.KLV,
        Constants.drive.KLA
      );

    kinematics = new DifferentialDriveKinematics(
        Units.feetToMeters(Constants.drive.TRACK_WIDTH)
      );

    // Create a voltage constraint to ensure we don't accelerate too fast
    DifferentialDriveVoltageConstraint autoVoltageConstraint =
        new DifferentialDriveVoltageConstraint(
            feedforward,
            kinematics,
          10
        );

    // Create config for trajectory
    TrajectoryConfig config = new TrajectoryConfig(
        Units.feetToMeters(Constants.drive.MAX_VEL),
        Units.feetToMeters(Constants.drive.MAX_ACC)
      ).setKinematics(kinematics)
       .addConstraint(autoVoltageConstraint)
       .setReversed(isReversed);

    // An example trajectory to follow. All Util in meters.
    trajectory = TrajectoryGenerator.generateTrajectory(
        Position.toPose2dMeters(waypoints),
        config
      );

    follower = new RamseteController(
        CORRECTION_FACTOR,
        DAMPENING_FACTOR
      );
    leftController = new PIDController(Constants.drive.KP, 0, Constants.drive.KD);
    rightController = new PIDController(Constants.drive.KP, 0, Constants.drive.KD);
  }

  public List<State> getPoints() {
    return trajectory.getStates();
  }

  public void init() {
    Set.create("RunTrajectoryTime");
    console.log("Running Trajectory");
    lastTime = 0;
    Trajectory.State initialState = trajectory.sample(0);
    lastSpeeds = kinematics.toWheelSpeeds(
      new ChassisSpeeds(
        initialState.velocityMetersPerSecond,
        0,
        initialState.curvatureRadPerMeter * initialState.velocityMetersPerSecond
      )
    );
    timer.reset();
    timer.start();
    leftController.reset();
    rightController.reset();
  }

  public void update() {
    double curTime = timer.get();
    double dt = curTime - lastTime;

    DifferentialDriveWheelSpeeds targetWheelSpeeds = kinematics.toWheelSpeeds(
        follower.calculate(
            Odometry.getPose2dMeters(),
            trajectory.sample(curTime)
      )
    );

    double leftFeedforward = feedforward.calculate(
        targetWheelSpeeds.leftMetersPerSecond,
        (targetWheelSpeeds.leftMetersPerSecond - lastSpeeds.leftMetersPerSecond) / dt
      );

    double rightFeedforward = feedforward.calculate(
        targetWheelSpeeds.rightMetersPerSecond,
        (targetWheelSpeeds.rightMetersPerSecond - lastSpeeds.rightMetersPerSecond) / dt
      );

    double leftFeedback = leftController.calculate(
        Odometry.getWheelSpeeds().leftMetersPerSecond,
        targetWheelSpeeds.leftMetersPerSecond
      );

    double rightFeedback = rightController.calculate(
        Odometry.getWheelSpeeds().rightMetersPerSecond,
        targetWheelSpeeds.rightMetersPerSecond
      );

    lastTime = curTime;
    lastSpeeds = targetWheelSpeeds;

    Drive.set(new DriveSignal(
        leftFeedforward + leftFeedback,
        rightFeedforward + rightFeedback,
        DriveUnit.VOLTAGE
    ));

    DashboardTrajectory.sendOdometry();
  }

  public boolean isFinished() {
    return timer.hasPeriodPassed(trajectory.getTotalTimeSeconds());
  }

  public void end() {
    timer.stop();
    Drive.set(new DriveSignal()); //stop
    console.log(
        "Trajectory Finished in " +
        Set.getTime("RunTrajectoryTime") + "s" +
        ", at: " + Odometry.getPositionFeet()
    );
  }
}
