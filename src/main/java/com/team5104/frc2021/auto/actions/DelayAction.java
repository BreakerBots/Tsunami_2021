/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.frc2021.auto.actions;

import com.team5104.lib.auto.AutoAction;
import edu.wpi.first.wpilibj.Timer;


public class DelayAction extends AutoAction {
    long startTime;
    int delay;
    int ms;
    private final Timer timer = new Timer();

    public DelayAction(int milliseconds) {
        delay = milliseconds;
        ms = milliseconds;
    }

    public void init() {
        System.out.println("Delaying " + delay + "ms");
        startTime = System.currentTimeMillis();
    }

    public void update() {

    }

    public void end() {

    }
    public boolean isFinished() {
        return (System.currentTimeMillis() >= startTime + delay);
    }
}
