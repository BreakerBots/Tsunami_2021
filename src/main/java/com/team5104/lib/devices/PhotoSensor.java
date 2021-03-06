/* BreakerBots Robotics Team (FRC 5104) 2020 */
package com.team5104.lib.devices;

import com.team5104.lib.devices.Health.Status;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;

/** A wrapper class of DigitalInput and AnalogInput to manager
 * Banner Photoelectric Sensors (that can be plugged into either ports) */
public class PhotoSensor extends Device {
    public enum PortType {
        DIGITAL,
        ANALOG
    }

    private static final double ANALOG_TRIGGER_VALUE = 2.5;

    private AnalogInput analogInput;
    private DigitalInput digitalInput;
    private final boolean inverted;

    //Constructors

    /**
     * Creates a Sensor object of the specifed port type, port number (assumes the sensor is not inverted)
     *
     * @param portType The port type on the RoboRIO that the sensor is plugged into
     * @param port     The port number on the RoboRIO that the sensor is plugged into
     */
    public PhotoSensor(PortType portType, int port) { this(portType, port, false); }

    /**
     * Creates a Sensor object of the specifed port type, port number, and inversion.
     *
     * @param portType The port type on the RoboRIO that the sensor is plugged into
     * @param port     The port number on the RoboRIO that the sensor is plugged into
     * @param inverted Will flip the value that the sensor reads. See get() for more information.
     */
    public PhotoSensor(PortType portType, int port, boolean inverted) {
        if (portType == PortType.ANALOG)
            analogInput = new AnalogInput(port);
        else digitalInput = new DigitalInput(port);
        this.inverted = inverted;
    }

    //Getters

    /**
     * Returns a boolean value if the sensor is tripped.
     * For digital ports it will simply return the value, and flip it if 'inverted' is true.
     * For analog ports it will compare the voltage to ANALOG_TRIGGER_VALUE. If the voltage is
     * greater than ANALOG_TRIGGER_VALUE it will return true and false otherwise. Then
     * it will invert the value if 'inverted' is true.
     */
    public boolean get() {
        if (analogInput != null)
            return analogInput.getVoltage() > ANALOG_TRIGGER_VALUE ^ inverted;
        return digitalInput.get() ^ inverted;
    }

    public Health getHealth() {
        return new Health(Status.GOOD);
    }

    public void stop() { }
}
