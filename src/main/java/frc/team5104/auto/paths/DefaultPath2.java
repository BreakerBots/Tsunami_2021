package frc.team5104.auto.paths;

import frc.team5104.auto.AutoPath;
import frc.team5104.auto.Position;
import frc.team5104.auto.actions.DriveTrajectoryAction;

public class DefaultPath2 extends AutoPath {
    //aka gooder default path
    public void start() {
        run(new DriveTrajectoryAction(false,
                new Position(0, 0, 0),
                new Position(7.5, 2.5, -60)
        ));
        run(new DriveTrajectoryAction(true,
                new Position(7.5, 2.5, -60),
                new Position(15, 5, 30)
        ));
        run(new DriveTrajectoryAction(false,
                new Position(15, 5, 30),
                new Position(17.5, 2.5, 0)
        ));
        run(new DriveTrajectoryAction(false,
                new Position(17.5, 2.5, 0),
                new Position(20, 2.5, 0)
        ));
        run(new DriveTrajectoryAction(false,
                new Position(20, 2.5, 0),
                new Position(22.5, 0, 90)
        ));
        run(new DriveTrajectoryAction(false,
                new Position(22.5, 0, 90),
                new Position(25, -2.5, 90)
        ));
        run(new DriveTrajectoryAction(false,//doesn't pick up a ball, its a weird angle. (also in hindsight horrible place to add a comment)
                new Position(25, -2.5, 90),
                new Position(20, -7.5, 180)
        ));

        run(new DriveTrajectoryAction(false,
                new Position(15, -7.5, 180),
                new Position(15, -5, 270)
        ));
        run(new DriveTrajectoryAction(false,//this could work but might not
                new Position(15, -5, 270),
                new Position(15, -2.5, 225)
        ));
        run(new DriveTrajectoryAction(false,
                new Position(15, -2.5, 225),
                new Position(12.5, -2.5, 180)
        ));
        run(new DriveTrajectoryAction(false,
                new Position(12.5, -2.5, 180),
                new Position(7.5, 0, 180)
        ));
        run(new DriveTrajectoryAction(true,//back to 0,0! idk if needed.
                new Position(7.5, 0, 180),
                new Position(30, 0, 180)
        ));
    }
}