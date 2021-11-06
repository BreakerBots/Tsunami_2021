package com.team5104.frc2021.auto.paths;

import com.team5104.frc2021.auto.actions.*;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Position;
import com.team5104.frc2021.auto.actions.DriveTrajectory;
import com.team5104.frc2021.auto.actions.ResetOdometry;


/**
 * @startingPosition Right of the Field
 * Shoots 3 balls
 * Drives and intakes 3 balls from trench
 */
public class ThreeBall_Right extends AutoPath {

    final double MAX_VEL = 8;
    final double MAX_ACC = 4;
    public void start() {

        run(new ResetOdometry(0, 0));
        run(new DelayAction(1000));

        run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
                new Position(0, 0, 0),
                new Position(4, 0, 0)
                ));
        run(new ShootAction());

        run(new IntakeAction());
        run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
                new Position(4, 0, 0),
                new Position(16, 0, 0)
        ));

    }
}
