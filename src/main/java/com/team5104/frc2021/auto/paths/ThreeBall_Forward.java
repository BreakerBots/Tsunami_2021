/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.frc2021.auto.paths;

import com.team5104.frc2021.Superstructure.Mode;
import com.team5104.frc2021.auto.actions.*;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Position;
import com.team5104.frc2021.auto.actions.DriveTrajectory;
import com.team5104.frc2021.auto.actions.ResetOdometry;


/**
 * @startingPosition Front of Power Port
 * Drives forward (toward power port) 5 feet
 * Shoots 3 balls
 */
public class ThreeBall_Forward extends AutoPath {
    final double MAX_VEL = 8;
    final double MAX_ACC = 4;

    public void start() {
        //Set position relative to field
        run(new ResetOdometry(0, 0));

        run(new DelayAction(500));

        run(new DriveTrajectory(true, MAX_VEL, MAX_ACC,
                new Position(0, 0, 0),
                new Position(-5, 0, 0)));

        run(new ShootAction());

    }
}

