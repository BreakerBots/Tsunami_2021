package com.team5104.frc2021.auto.paths;

import com.team5104.frc2021.auto.actions.*;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Position;
import com.team5104.frc2021.auto.actions.DriveTrajectory;

/**
 * @startingPosition Right of the Field
 * Intakes 1 from our Trench
 * Returns to shoot 4 balls from right
 */

public class FourBall_Right extends AutoPath {

    final double MAX_VEL = 8;
    final double MAX_ACC = 4;

    public void start() {

        run(new ResetOdometry(0, 0));
        run(new IntakeAction());
        run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
                new Position(0, 0, 0),
                new Position(11, 0, 0)
        ));
        run(new IdleAction());
        run(new ChargeFlywheelAction());
        run(new DriveTrajectory(true, MAX_VEL + 2, MAX_ACC,
                new Position (11, 0, 0),
                new Position (2, 0, 0)
        ));

        run(new ShootAction());
    }
}
