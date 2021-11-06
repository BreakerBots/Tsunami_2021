package com.team5104.frc2021.auto.paths;

import com.team5104.frc2021.auto.actions.*;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Position;
import com.team5104.frc2021.auto.actions.DriveTrajectory;

/**
 * @startingPosition Right of the Field
 * Intakes 1 (maybe 2) from our Trench
 * Returns to shoot 4 (maybe 5) balls from right
 */

public class FiveBall_Right extends AutoPath {

    final double MAX_VEL = 8;
    final double MAX_ACC = 4;

    public void start() {

        run(new IntakeAction());
        run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
                new Position(0, 0, 0),
                new Position(15, 0, 0)
        ));
        run(new IdleAction());
        run(new ChargeFlywheelAction());
        run(new DriveTrajectory(true, MAX_VEL + 2, MAX_ACC,
                new Position (15, 0, 0),
                new Position (2, 0, 0)
        ));

        run(new ShootAction());
    }
}
