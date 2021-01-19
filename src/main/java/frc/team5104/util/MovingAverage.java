/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util;

/**
 * Simply smoothes out a input by using an average between the last n inputs.
 * Useful for sensors that give variable outputs or any variable that spikes.
 */
public class MovingAverage {
	private double[] values;
	private double init;
	
	public MovingAverage(int n, double init) {
		this.init = init;
		values = new double[n];
		for(int i = 0; i < n; i++) 
			values[i] = init;
	}
	public MovingAverage(int n, int init) {
		this(n, (double) init);
	}
	public MovingAverage(int n, boolean init) {
		this(n, init ? 1.0 : 0.0);
	}
	
	//Update
	public void update(double n) {
		for(int i = 0; i < values.length - 1; i++) 
			values[i] = values[i + 1];
		values[values.length - 1] = n;
	}
	public void update(int n) {
		update((double) n);
	}
	public void update(boolean b) {
		update(b ? 1.0 : 0.0);
	}
	
	//Reset
	public void reset() {
		for(int i = 0; i < values.length; i++) 
			values[i] = init;
	}
	
	//Getters
	public double getDoubleOutput() {
		double sum = 0;
		for(int i = 0; i < values.length; i++) 
			sum += values[i];
		return sum / values.length;
	}
	public int getIntegerOutput() {
		return (int) (getDoubleOutput());
	}
	public boolean getBooleanOutput() {
		return getDoubleOutput() > 0.5;
	}
}
