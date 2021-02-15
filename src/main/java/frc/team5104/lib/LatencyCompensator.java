/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.lib;

import edu.wpi.first.wpilibj.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

public class LatencyCompensator {
    private final List<Double> values;
    private final List<Double> timeStamps;
    private final DoubleSupplier valueReader;

    public LatencyCompensator(DoubleSupplier valueReader) {
        values = new ArrayList<Double>();
        timeStamps = new ArrayList<Double>();
        this.valueReader = valueReader;
    }

    //Reset
    public void reset() {
        values.clear();
        timeStamps.clear();
    }

    //Update
    public void update() {
        values.add(this.valueReader.getAsDouble());
        timeStamps.add(Timer.getFPGATimestamp());

        if (values.size() > 20)
            values.remove(0);
        if (timeStamps.size() > 20)
            timeStamps.remove(0);
    }

    //Getters
    public double getValueInHistory(double msBack) {
        double targetTime = Timer.getFPGATimestamp() - (msBack / 1000.0);
        for (int i = timeStamps.size() - 1; i >= 0; i--) {
            if (targetTime >= timeStamps.get(i))
                return values.get(i);
        }
        return 0;
    }
}
