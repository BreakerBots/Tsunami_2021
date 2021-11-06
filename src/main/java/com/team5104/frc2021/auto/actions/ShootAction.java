package com.team5104.frc2021.auto.actions;

import com.team5104.frc2021.Superstructure;
import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.lib.auto.AutoAction;


public class ShootAction extends AutoAction {
    public ShootAction() {

    }

    public void init() {
        System.out.println("firing!");
        Superstructure.set(Mode.AIMING);
    }

    public void update() {

    }

    public void end() {
        System.out.println("finished firing");
    }

    @Override
    public boolean isFinished() {
        return Superstructure.isIdle();
    }
}
