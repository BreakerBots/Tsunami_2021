package com.team5104.lib.control;

import com.team5104.lib.ConstantsUtils.SubsystemConstants;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile.Constraints;

public class PositionController {
    private final Timer timer = new Timer();
    private final ProfiledPIDController pid;
    private final SimpleMotorFeedforward ff;
    private double lastVelocity, lastPIDOutput, lastFFOutput, lastOutput,
            lastPosition, lastTarget, lastTime;

    public PositionController(SubsystemConstants constants) {
        this(constants.kP, constants.kI, constants.kD, constants.maxVelocity, constants.maxAccel,
                constants.kS, constants.kV, constants.kA);
    }

    public PositionController(double kP, double kI, double kD, double maxVel,
                              double maxAccel, double kS, double kV, double kA) {
        pid = new ProfiledPIDController(kP, kI, kD, new Constraints(maxVel, maxAccel));
        ff = new SimpleMotorFeedforward(kS, kV, kA);
    }

    public double calculate(double currentPosition, double targetPosition) {
        double curTime = timer.get();
        double dt = curTime - lastTime;

        lastPIDOutput = pid.calculate(currentPosition, targetPosition);
        lastFFOutput = ff.calculate(
                pid.getSetpoint().velocity,
                (pid.getSetpoint().velocity - lastVelocity) / dt
        );
        lastOutput = lastFFOutput + lastPIDOutput;
        lastVelocity = pid.getSetpoint().velocity;
        lastTime = curTime;

        return lastOutput;
    }

    public double getLastFFOutput() { return lastFFOutput; }

    public double getLastPIDOutput() { return lastPIDOutput; }

    public double getLastOutput() { return lastOutput; }

    public double getLastError() { return lastTarget - lastPosition; }

    public void setPID(double kP, double kI, double kD) {
        pid.setPID(kP, kI, kD);
    }

    public void setP(double kP) {
        pid.setPID(kP, pid.getI(), pid.getD());
    }

    public void setProfiling(double maxVel, double maxAccel) {
        pid.setConstraints(new Constraints(maxVel, maxAccel));
    }
}