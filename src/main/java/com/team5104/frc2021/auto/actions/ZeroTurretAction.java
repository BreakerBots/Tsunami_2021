/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.frc2021.auto.actions;

import com.team5104.frc2021.subsystems.Turret;
import com.team5104.lib.auto.AutoAction;

public class ZeroTurretAction extends AutoAction {

    private double angleToZeroTurretAt;

    public ZeroTurretAction(double angleToZeroTurretAt) {
        this.angleToZeroTurretAt = angleToZeroTurretAt;
    }

    public void init() {
        System.out.println("zeroing turret at " + angleToZeroTurretAt);
        Turret.setFieldOrientedTarget(angleToZeroTurretAt);
    }

    public void update() {

    }

    public void end() {

    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
