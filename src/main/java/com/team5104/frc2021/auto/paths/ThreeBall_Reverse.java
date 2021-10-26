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
 * Shoots 3 balls
 * Drives forward 7 feet
 */
public class ThreeBall_Reverse extends AutoPath {
    final double MAX_VEL = 8;
    final double MAX_ACC = 4;

    public void start() {
        //Set position relative to field
        run(new ResetOdometry(0, 0));

        run(new DelayAction(5000));
        run(new ShootAction());


        // Evading Marker D5
        run(new DriveTrajectory(false, MAX_VEL, MAX_ACC,
                new Position(0, 0, 0),
                new Position(2, 0, 0)));


//        add(new ZeroTurretAction(0));
//        add(new DelayAction(500));
//        add(new ShootAction());
//        System.out.println("running auto");
//        new DriveTrajectory(false, MAX_VEL * 0.5, MAX_ACC * 0.5,
//                new Position(0, 0, 0),
//                new Position(2, 0, 0)
//        );
//        add(new DriveStopAction());

    }
}
