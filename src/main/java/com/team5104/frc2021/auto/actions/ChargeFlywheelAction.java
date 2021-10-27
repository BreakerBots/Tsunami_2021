/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.frc2021.auto.actions;

import com.team5104.frc2021.subsystems.Flywheel.FlywheelState;
import com.team5104.frc2021.Superstructure;
import com.team5104.lib.auto.AutoAction;

public class ChargeFlywheelAction extends AutoAction {
    public ChargeFlywheelAction() {

    }

    public void init() {
        System.out.println("charging flywheel");
        Superstructure.setFlywheelState(FlywheelState.SPINNING);
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
