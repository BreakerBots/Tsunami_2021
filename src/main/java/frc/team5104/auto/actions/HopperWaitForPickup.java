/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.actions;

import frc.team5104.auto.AutoAction;
import frc.team5104.subsystems.Hopper;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.setup.RobotState;

public class HopperWaitForPickup extends AutoAction {
    private boolean intaked, simulationValue;
	private long startTime;
	private int timeoutMs;

    public HopperWaitForPickup(int timeoutMs) {
        this(timeoutMs, false);
    }
    public HopperWaitForPickup(int timeoutMs, boolean simulationValue) {
        this.timeoutMs = timeoutMs;
        this.simulationValue = simulationValue;
    }

    public void init() {
    	console.log(c.AUTO, "Waiting for hopper to intake a ball");
    	startTime = System.currentTimeMillis();
    }

    public boolean update() {
        if (RobotState.isSimulation() ? false : Hopper.isEntrySensorTrippedAvg())
            intaked = true;
        return intaked || (System.currentTimeMillis() >= startTime + timeoutMs);
    }

    public void end() {
    	
    }

    public boolean getValue() {
        return RobotState.isSimulation() ? simulationValue : intaked;
    }
}
