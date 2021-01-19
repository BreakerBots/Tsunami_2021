/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import frc.team5104.util.CrashLogger.Crash;

public class LatencyCompensator {
	private List<Double> values, timeStamps;
	private Notifier thread;
	private DoubleSupplier valueReader;
	
	public LatencyCompensator(DoubleSupplier valueReader) {
		values = new ArrayList<Double>();
		timeStamps = new ArrayList<Double>();
		this.valueReader = valueReader;
		thread = new Notifier(() -> {
			try {
				values.add(this.valueReader.getAsDouble());
				timeStamps.add(Timer.getFPGATimestamp());
				
				if (values.size() > 20)
					values.remove(0);
				if (timeStamps.size() > 20)
					timeStamps.remove(0);
			} catch (Exception e) { CrashLogger.logCrash(new Crash("latency compensator", e)); }
		});
		thread.startPeriodic(0.01);
	}
	
	//Reset
	public void reset() {
		values.clear();
		timeStamps.clear();
	}
	
	//Getters
	public double getValueInHistory(double msBack) {
		double targetTime = Timer.getFPGATimestamp() - (msBack / 1000.0);
		for (int i = timeStamps.size()-1; i >= 0; i--) {
			if (targetTime >= timeStamps.get(i))
				return values.get(i);
		}
		return 0;
	}
}
