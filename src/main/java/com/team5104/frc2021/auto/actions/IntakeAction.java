/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.frc2021.auto.actions;

import com.team5104.frc2021.Superstructure;
import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.lib.auto.AutoAction;

public class IntakeAction extends AutoAction {
    public IntakeAction() {

    }

    public void init() {
        System.out.println("intaking!");
        Superstructure.set(Mode.INTAKING);
    }

    public void update() {

    }

    public void end() {
    }

    @Override
    public boolean isFinished() {
        return true;
        //exit when hopper is empty
    }
}

