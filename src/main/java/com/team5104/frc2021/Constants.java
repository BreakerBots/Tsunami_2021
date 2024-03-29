/*BreakerBots Robotics Team 2019*/
package com.team5104.frc2021;

import com.team5104.lib.RobotNameManager;

public class Constants {
    public static RobotNameManager robot =
            new RobotNameManager("Tsunami", "Tidal-Wave");

    //Drive
    public static DriveConstants drive = new DriveConstants();
    public static class DriveConstants {
        public double KP = 1;
        public double KD = 0;
        public double KLS = 0.162;
        public double KLV = 1.98;
        public double KLA = 0.0991;
        public double KAS = 0.66;
        public double KAV = 1.5;
        public double KAA = 0.3;
        public double GEARING = (50.0 / 8.0) * (46.0 / 24.0);
        public double TRACK_WIDTH = 2.0589795990198065;
        public double WHEEL_DIAMETER = 0.5;
    }

    //Flywheel
    public static FlywheelConstants flywheel = new FlywheelConstants();
    public static class FlywheelConstants {
        public double KP = sob(0.2, 1);
        public double KI = 0;
        public double KD = sob(0.01, 0.2);
        public double KS = sob(0.334, 0.261);
        public double KV = sob(0.06, 0.111);
        public double KA = 0;
        public double MAX_VEL = 0;
        public double MAX_ACC = 0;
        public double GEARING = 24.0 / 44.0;

        public boolean OPEN_LOOP = true;
        public double RAMP_RATE_UP = 0.7;
        public double RAMP_RATE_DOWN = 3;
        public double RPM_TOL = 500;
    }

    //Hood
    public static HoodConstants hood = new HoodConstants();
    public static class HoodConstants {
        public double KI = 0;
        public double KD = 0;
        public double KS = 0; //sob(.835, .49);
        public double KV = 0; //sob(.06, .0605);
        public double KA = 0; //sob(.00195, .00218);
        public double MAX_VEL = 200;
        public double MAX_ACC = 2000;
        public double GEARING = (20.0 / 18.0) * (360.0 / 18.0);

        public double TOL = 3;
        public double EQ_CONST = 1;
        public double HOMING_SPEED = 0.4;
    }

    //Hopper
    public static HopperConstants hopper = new HopperConstants();
    public static class HopperConstants {
        public double KP = 14;
        public double KI = 0.5;
        public double KD = 0.25;
        public double KS = sob(0.308, 0.42);
        public double KV = 0;
        public double KA = 0;
        public double MAX_VEL = 0;
        public double MAX_ACC = 0;
        public double GEARING = 70.0 / 12.0;

        public double BALL_SIZE = 3;
        public double TOLERANCE = 0.05;
        public double START_SPEED_INTAKING = 7;
        public double START_SPEED_INDEXING = 0.5;
        public double[] FEED_SPEED = { 5, 5, 5 }; //start, indexer, feeder (OG: 4,4,6)
    }

    //Turret
    public static TurretConstants turret = new TurretConstants();
    public static class TurretConstants {
        public double KP = 0.19;
        public double KI = 0;
        public double KD = sob(0.02, 0.0);
        public double KS = sob(0/*0.258*/, 0.35);
        public double KV = 0;
        public double KA = sob(0, 0.000384);
        public double MAX_VEL = 200;
        public double MAX_ACC = 2000;
        public double GEARING = (60.0 / 8.0 /*gear*/) * (150.0 / 22.0 /*sprocket*/);

        public double HOMING_SPEED = 1.8;
        public double VOLT_LIMIT = 6;
        public double VISION_TOL = 6;
        public double SOFT_LEFT = sob(120, 110);
        public double SOFT_RIGHT = sob(-120, -120);
        public double ZERO = sob(140, 130);
    }

    //Intake
    public static IntakeConstants intake = new IntakeConstants();
    public static class IntakeConstants {
        public double INTAKING_SPEED = 0.75;
        public double FIRING_SPEED = 0;//0.25
        public double REJECTING_SPEED = 0;//.25
    }

    //Paneler
    public static PanelerConstants paneler = new PanelerConstants();
    public static class PanelerConstants {
        public double ROT_SPEED = 0.15;
        public double POS_SPEED = 0.0;
        public double GEARING = 16.0 / 2.0; //to control panel
        public double ROTATIONS = 3;
        public int BRAKE_INT = 25;
    }

    //Other
    /** See RobotNameManager.switchOnBot() */
    public static double sob(double... out) {
        return robot.switchOnBot(out);
    }
}
