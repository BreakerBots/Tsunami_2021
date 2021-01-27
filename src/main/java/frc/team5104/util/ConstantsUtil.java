package frc.team5104.util;

import frc.team5104.util.setup.RobotState;

public class ConstantsUtil {
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

    //System Constants
    public static class SystemConstants {
        public final double kP, kI, kD, kS, kV, kA, maxVelocity, maxAccel, ticksPerRev;

        public SystemConstants(double kP, double kI, double kD, double kS, double kV, double kA, double maxVelocity, double maxAccel, double ticksPerRev) {
            this.kP = kP;
            this.kI = kI;
            this.kD = kD;
            this.kS = kS;
            this.kV = kV;
            this.kA = kA;
            this.maxVelocity = maxVelocity;
            this.maxAccel = maxAccel;
            this.ticksPerRev = ticksPerRev;
        }
    }
}
