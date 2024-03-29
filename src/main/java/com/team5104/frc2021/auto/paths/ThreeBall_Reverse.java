/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.frc2021.auto.paths;

import com.team5104.frc2021.auto.actions.*;
import com.team5104.lib.auto.AutoPath;
import com.team5104.lib.auto.Position;
import com.team5104.frc2021.auto.actions.DriveTrajectory;
import com.team5104.frc2021.auto.actions.ResetOdometry;


/**
 * @startingPosition Front of Power Port
 * Drives backward 2 feet
 * Shoots 3 balls
 */
public class ThreeBall_Reverse extends AutoPath {
    final double MAX_VEL = 8;
    final double MAX_ACC = 4;

    public void start() {
        //Set position relative to field
        run(new ResetOdometry(0, 0));

        run(new DelayAction(500));



        // Evading Marker D5
        run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
                new Position(0, 0, 0),
                new Position(2.5, 0, 0)));

        run(new ShootAction());

    }
}
