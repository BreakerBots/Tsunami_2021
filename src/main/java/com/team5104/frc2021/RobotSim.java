package com.team5104.frc2021;

import com.team5104.frc2021.subsystems.*;
import com.team5104.lib.auto.AutoManager;
import com.team5104.lib.auto.Odometry;
import com.team5104.lib.dashboard.Dashboard;
import com.team5104.lib.setup.RobotController;
import com.team5104.lib.subsystem.SubsystemManager;

public class RobotSim extends RobotController.Robot {
    public RobotSim() {
        //Win
        win();

        //Subsystems
        SubsystemManager.attach(
            new Drive(),
            new Intake(),
            new Climber(),
            new Flywheel(),
            new Hood(),
            new Paneler(),
            new Turret()
        );

        //Other
        Odometry.init();
    }

    //Autonomous
    public void autoEnabled() {
        AutoManager.enabled();
    }
    public void autoDisabled() {
        AutoManager.disabled();
    }
    public void autoLoop() {
        AutoManager.update();
    }

    //Main
    public void mainEnabled() {
        SubsystemManager.reset();
    }
    public void mainDisabled() {
        SubsystemManager.reset();
    }
    public void mainLoop() {
        SubsystemManager.update();
    }
    public void mainShutdown() {
        Dashboard.close();
    }
}
