/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util;

/**
 * A latched boolean is a boolean that only returns true when the input changes values.
 * For example when a boolean changes from false->true or true->false.
 * This class needs to be called every loop to work properly.
 */
public class LatchedBoolean {
	/**
	 * The mode for a latched boolean (affects when triggered)<br>
	 *  - always: False->True, and True->False activation,<br>
	 *  - rising: only False->True activation,<br>
	 *  - falling: only True->False activation
	 */
	public static enum LatchedBooleanMode {
		ALWAYS,
		RISING,
		FALLING
	}
	
	//Class Values
	private boolean lastValue = false;
	public LatchedBooleanMode mode;
	
	//Constructors
	/**
	 * Creates a latched boolean with the mode "Always" in which between False->True and True->False it will be activated.
	 */
	public LatchedBoolean() {
		this(LatchedBooleanMode.ALWAYS);
	}
	/**
	 * Creates a latched boolean with the specified mode.
	 * @param mode The specified mode.
	 */
	public LatchedBoolean(LatchedBooleanMode mode) {
		this(mode, false);
	}
	
	/**
	 * Creates a latched boolean with the specific mode and starting value.
	 * @param mode The specific mode.
	 * @param startValue the value to start at (if opposite of first value, latched boolean will be triggered)
	 */
	public LatchedBoolean(LatchedBooleanMode mode, boolean startValue) {
		this.mode = mode;
		this.lastValue = startValue;
	}
	
	//Main Getter Function
	/**
	 * 
	 * @param currentValue
	 * @return
	 */
	public boolean get(boolean currentValue) {
		boolean changed = currentValue != lastValue;
		lastValue = currentValue;
		if (changed) {
			if (mode == LatchedBooleanMode.ALWAYS)
				return true;
			if (mode == LatchedBooleanMode.RISING && currentValue == true)
				return true;
			if (mode == LatchedBooleanMode.FALLING && currentValue == false)
				return true;
		}
		return false;
	}
}
