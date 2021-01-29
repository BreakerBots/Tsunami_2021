/*BreakerBots Robotics Team 2019*/
package frc.team5104;

import frc.team5104.util.ConstantsUtils.DriveConstants;
import frc.team5104.util.ConstantsUtils.RobotConfig;
import frc.team5104.util.ConstantsUtils.SubsystemConstants;

public class Constants {
    //Main
    public static final RobotConfig config = new RobotConfig(false, "Tsunami", "Tidal-Wave");
    public static double SUPERSTRUCTURE_TOL_SCALAR = 1;

    //Drive
    public static DriveConstants drive = new DriveConstants(
            1/*0.153*/, 0,
            switchOnBot(0.162, 0.151),
            1.98,//switchOnBot(3.296, 3.328),
            0.2,//switchOnBot(0.0991, 0.135),
            0.66, 1.5, 0.3,
            6, 4,
            (50.0 / 8.0) * (46.0 / 24.0),
            2.0589795990198065, //lol
            0.5
    );

    //Flywheel
    public static final boolean FLYWHEEL_OPEN_LOOP = false;
    public static final double FLYWHEEL_RAMP_RATE_UP = 0.7;
    public static final double FLYWHEEL_RAMP_RATE_DOWN = 3;
    public static double FLYWHEEL_RPM_TOL = 500;
    public static SubsystemConstants flywheel = new SubsystemConstants(
            switchOnBot(0.2, 1), 0, switchOnBot(0.01, 0.2),
            switchOnBot(0.334, 0.261),
            switchOnBot(0.06, 0.111),
            0,
            0, 0,
            24.0 / 44.0
    );

    //Hood
    public static final double HOOD_TOL = 3;
    public static double HOOD_EQ_CONST = 0;
    public static final double HOOD_CALIBRATE_SPEED = 0.4;
    public static SubsystemConstants hood = new SubsystemConstants(
            0, 0, 0,
            switchOnBot(.835, .49),
            switchOnBot(.06, .0605),
            switchOnBot(.00195, .00218),
            200, 2000,
            (20.0 / 18.0) * (360.0 / 18.0)
    );

    //Hopper
    public static final double HOPPER_START_INTAKE_SPEED = 7;
    public static final double HOPPER_START_INDEX_SPEED = 0.5;
    public static double HOPPER_INDEX_BALL_SIZE = 2;
    public static double HOPPER_INDEX_TOL = 0.05;
    public static double HOPPER_FEED_SPEED = 6;
    public static SubsystemConstants hopperIndex = new SubsystemConstants(
            14, 0.5, 0.25,
            switchOnBot(0.308, 0.42), 0, 0,
            0, 0,
            70.0 / 12.0
    );


    //Intake
    public static final double INTAKE_SPEED = 1.0;
    public static final double INTAKE_FIRE_SPEED = 0;//0.25;
    public static final double INTAKE_REJECT_SPEED = 0;//.25;//0;

    //Paneler
    public static final double PANELER_ROT_SPEED = 0.15;
    public static final double PANELER_POS_SPEED = 0.0;
    public static final double PANELER_GEARING = /*control panel*/16.0 / 2;
    public static final double PANELER_ROTATIONS = 3;
    public static final int PANELER_BRAKE_INT = 25;

    //Turret
    public static final double TURRET_CALIBRATE_SPEED = 0.15;
    public static final double TURRET_VOLT_LIMIT = 6;
    public static final double TURRET_VISION_TOL = 6;
    public static final double TURRET_SOFT_LEFT = switchOnBot(120, 110);
    public static final double TURRET_SOFT_RIGHT = switchOnBot(-120, -120);
    public static final double TURRET_ZERO = switchOnBot(140, 130);
    public static SubsystemConstants turret = new SubsystemConstants(
            switchOnBot(0.2, 0.2),
            0,
            switchOnBot(0.01, 0.0),
            switchOnBot(.2/*0.258*/, 0.35),
            0.015,
            switchOnBot(0.000121, 0.000384),
            200, 2000,
            (8.0 / 60.0 /*gear*/) / (22.0 / 150.0 /*sprocket*/)
    );

    //Other
    /** Returns "c" if this is the competition robot otherwise returns "a" */
    public static double switchOnBot(double c, double a) {
        return config.isCompetitionRobot ? c : a;
    }
}