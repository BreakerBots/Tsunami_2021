package frc.team5104.util;

import frc.team5104.util.setup.RobotState;

public class ConstantsUtils {
    //Robot Config
    public static class RobotConfig {
        public final String robotName;
        public final boolean isAtCompetition;
        public final boolean isCompetitionRobot;

        /**@param isAtCompetition Say whether the robot is currently competing, this should add specific things to the webapp
         * @param robotNames List out all the robot names this code is targeted for. The first name is considered the "competition robot" */
        public RobotConfig(boolean isAtCompetition, String... robotNames) {
            this.isAtCompetition = isAtCompetition;
            if (RobotState.isSimulation())
                robotName = robotNames[0];
            else robotName = Filer.readFile(Filer.HOME_PATH + "robot.txt");
            this.isCompetitionRobot = robotName.equals(robotNames[0]);
        }
    }

    //Subsystem Constants
    public static class SubsystemConstants {
        public double kP, kI, kD, kS, kV, kA, maxVelocity, maxAccel, gearing;

        /**@param kP proportional constant for PID (use frc-characterization)
         * @param kI integral constant PID (use frc-characterization)
         * @param kD derivative constant for PID (use frc-characterization)
         * @param kS minimum voltage to move this mechanism (use frc-characterization)
         * @param kV voltage required to reach a specific velocity (use frc-characterization)
         * @param kA voltage required to reach a specific acceleration (use frc-characterization)
         * @param maxVelocity the max velocity this mechanism should move at
         * @param maxAccel the max acceleration this mechanism should move at
         * @param gearing the gearing from the motor to the final output */
        public SubsystemConstants(double kP, double kI, double kD, double kS, double kV, double kA,
                                  double maxVelocity, double maxAccel, double gearing) {
            this.kP = kP;
            this.kI = kI;
            this.kD = kD;
            this.kS = kS;
            this.kV = kV;
            this.kA = kA;
            this.maxVelocity = maxVelocity;
            this.maxAccel = maxAccel;
            this.gearing = gearing;
        }
    }
}
