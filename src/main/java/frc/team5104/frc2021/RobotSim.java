package frc.team5104.frc2021;

import frc.team5104.frc2021.auto.paths.AutoNavBarrel;
import frc.team5104.frc2021.auto.paths.AutoNavBounce;
import frc.team5104.frc2021.auto.paths.GalacticDefault2;
import frc.team5104.lib.auto.AutoManager;
import frc.team5104.lib.auto.Odometry;
import frc.team5104.frc2021.subsystems.Drive;
import frc.team5104.lib.webapp.Plotter;
import frc.team5104.lib.webapp.Webapp;
import frc.team5104.lib.managers.SubsystemManager;
import frc.team5104.lib.setup.RobotController;

public class RobotSim extends RobotController.BreakerRobot {
    public RobotSim() {
        //Win
        this.win();

        //Managers
        SubsystemManager.useSubsystems(
            new Drive()
        );

        //Other Initialization
        Webapp.run();
        Plotter.reset();
        Odometry.init();
        AutoManager.setTargetPath(new AutoNavBarrel());
        //AutoManager.runTrajectoryTester();
        AutoManager.enabledPlotting();
        //AutoManager.characterize(Drive.class);
    }

    //Autonomous
    public void autoStart() {
        AutoManager.enabled();
    }
    public void autoStop() {
        AutoManager.disabled();
    }
    public void autoLoop() {
        AutoManager.update();
    }

    //Main
    public void mainStart() {
        SubsystemManager.reset();
    }
    public void mainStop() {
        SubsystemManager.reset();
    }

//    double last = RobotState.getFPGATimestamp();
    public void mainLoop() {
//        Tuner.setTunerOutput("dt", RobotState.getFPGATimestamp() - last);
//        last = RobotState.getFPGATimestamp();

        SubsystemManager.update();


    }
}
