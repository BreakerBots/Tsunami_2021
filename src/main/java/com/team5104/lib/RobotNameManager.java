package com.team5104.lib;

//Robot Name Manager
public class RobotNameManager {
  public final String name;
  public final int id;

  /** @param robotNames List out all the robot names this code is targeted for. The first name is considered the "competition robot"*/
  public RobotNameManager(String... robotNames) {
    this.id = 0;
    this.name = robotNames[id];
//
//
//    if (RobotState.isSimulation()) {
//      name = robotNames[0];
//    }
//    else name = Filer.readFile(Filer.HOME_PATH + "robot.txt");
//    id = Arrays.asList(robotNames).indexOf(name);
//    if (id == -1) {
//      console.error("Invalid Robot Name!");
//    }
  }

  /** Returns a value that corresponds to the robotName index */
  public double switchOnBot(double... out) {
    return out[id];
  }

  /** Returns a value that corresponds to the robotName index */
  public boolean switchOnBot(boolean... out) {
    return out[id];
  }

  /** Returns a value that corresponds to the robotName index */
  public String switchOnBot(String... out) {
    return out[id];
  }

  /** Returns a value that corresponds to the robotName index */
  public int switchOnBot(int... out) {
    return out[id];
  }
}
