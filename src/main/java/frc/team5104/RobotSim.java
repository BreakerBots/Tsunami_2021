package frc.team5104;

import frc.team5104.auto.AutoManager;
import frc.team5104.auto.Odometry;
import frc.team5104.subsystems.Drive;
import frc.team5104.util.Plotter;
import frc.team5104.util.Webapp;
import frc.team5104.util.managers.SubsystemManager;
import frc.team5104.util.setup.RobotController;
import frc.team5104.util.setup.RobotState;

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
        //AutoManager.setTargetPath(new ExamplePath());
        //AutoManager.enabledPlotting();
        AutoManager.characterize(Drive.class);
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

    public void mainLoop() {
        SubsystemManager.update();

        if (RobotState.getMode() == RobotState.RobotMode.DISABLED) {
            Drive.reset();
            Odometry.reset();
        }
    }
}
