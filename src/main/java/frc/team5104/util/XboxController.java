/*BreakerBots Robotics Team 2019*/
package frc.team5104.util;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.DriverStation;
import frc.team5104.util.XboxController.Button.ButtonType;
import frc.team5104.util.console.c;

import java.util.ArrayList;

public class XboxController {
    private static final ArrayList<XboxController> controllers = new ArrayList<XboxController>();
    private final int port;
    private final ArrayList<Button> buttons = new ArrayList<Button>();
    private final ArrayList<Axis> axises = new ArrayList<Axis>();
    private Rumble activeRumble = null;
    private boolean hasSentDisconnectMessage = false;

    //Contructors
    public static XboxController create(int port) {
        for (XboxController controller : controllers)
            if (controller.port == port) {
                console.error(c.MAIN, "controller on port " + port + " already declared!");
                return null;
            }

        return new XboxController(port);
    }

    XboxController(int port) {
        this.port = port;
        controllers.add(this);
    }

    //Update
    public static void update() {
        for (XboxController controller : controllers) {
            for (Button button : controller.buttons)
                button.update();
            if (controller.activeRumble != null)
                controller.activeRumble.update();
            else if (isConnected(controller.port))
                HAL.setJoystickOutputs((byte) controller.port, 0, (short) 0, (short) 0);
            if (isConnected(controller.port)) {
                if (controller.hasSentDisconnectMessage) {
                    console.log(c.MAIN, "Controller on port " + controller.port + " reconnected!");
                    controller.hasSentDisconnectMessage = false;
                }
            }
            else if (!controller.hasSentDisconnectMessage) {
                console.warn(c.MAIN, "Controller on port " + controller.port + " disconnected!");
                controller.hasSentDisconnectMessage = true;
            }
        }
    }

    //Is Connected
    public boolean isConnected() { return isConnected(port); }

    public static boolean isConnected(int port) { return HAL.getJoystickIsXbox((byte) port) == 1; }

    //Buttons
    public static Button getButton(int slot, XboxController controller1, XboxController controller2) {
        return new DoubleButton(
                controller1.getButton(slot),
                controller2.getButton(slot)
        );
    }

    public static Button getHoldButton(int slot, XboxController controller1, XboxController controller2) {
        return new DoubleButton(
                controller1.getHoldButton(slot),
                controller2.getHoldButton(slot)
        );
    }

    public static Button getHoldTimeButton(int slot, int holdTime, XboxController controller1, XboxController controller2) {
        return new DoubleButton(
                controller1.getHoldTimeButton(slot, holdTime),
                controller2.getHoldTimeButton(slot, holdTime)
        );
    }

    public static Button getDoubleClickButton(int slot, int killOffTime, XboxController controller1, XboxController controller2) {
        return new DoubleButton(
                controller1.getDoubleClickButton(slot, killOffTime),
                controller2.getDoubleClickButton(slot, killOffTime)
        );
    }

    public Button getButton(int slot) {
        buttons.add(new Button(port, slot, ButtonType.NORMAL, 0));
        return buttons.get(buttons.size() - 1);
    }

    public Button getHoldButton(int slot) {
        buttons.add(new Button(port, slot, ButtonType.HOLD, 0));
        return buttons.get(buttons.size() - 1);
    }

    public Button getHoldTimeButton(int slot, int holdTime) {
        buttons.add(new Button(port, slot, ButtonType.HOLD_TIME, holdTime));
        return buttons.get(buttons.size() - 1);
    }

    public Button getDoubleClickButton(int slot, int killOffTime) {
        buttons.add(new Button(port, slot, ButtonType.DOUBLE_CLICK, killOffTime));
        return buttons.get(buttons.size() - 1);
    }

    public static class Button {
        public enum ButtonType {NORMAL, HOLD, HOLD_TIME, DOUBLE_CLICK}

        private final ButtonType buttonType;
        private final int port;
        private final int slot;
        private boolean value, lastValue, pressed, released, heldEventReturned;
        private long heldEventTime, doubleClickTime;
        private int heldEventLength, doubleClickKilloff, doubleClickIndex = 0;

        Button(int port, int slot, ButtonType buttonType, int demand) {
            this.port = port;
            this.slot = slot;
            this.buttonType = buttonType;
            if (buttonType == ButtonType.HOLD_TIME) heldEventLength = demand;
            if (buttonType == ButtonType.DOUBLE_CLICK) doubleClickKilloff = demand;
        }

        //Update
        void update() {
            pressed = false;
            released = false;
            value = isDown();

            //Pressed/Released
            if (value != lastValue) {
                lastValue = value;
                if (value) {
                    pressed = true;
                    heldEventTime = System.currentTimeMillis();
                }
                else released = true;
            }

            //Held Event
            if (!value) heldEventReturned = false;

            //Double Click
            doubleClickIndex = 0;
            if (doubleClickTime != -1 && System.currentTimeMillis() > doubleClickTime + doubleClickKilloff) {
                //Killoff
                doubleClickIndex = 1;
                doubleClickTime = -1;
            }
            if (pressed) {
                //First Click
                if (doubleClickTime == -1) {
                    doubleClickTime = System.currentTimeMillis();
                }
                //Second Click
                else {
                    doubleClickIndex = 2;
                    doubleClickTime = -1;
                }
            }
        }

        //Reads

        /**
         * Returns true on event trigger
         * - Normal: button pressed event
         * - Hold: if button is down
         * - Hold Time: held event
         * - Double Click: double click event
         */
        public boolean get() {
            if (buttonType == ButtonType.NORMAL)
                return pressed;
            if (buttonType == ButtonType.HOLD)
                return isDown();
            if (buttonType == ButtonType.HOLD_TIME) {
                boolean temp = ((value ? ((double) (System.currentTimeMillis() - heldEventTime)) : 0) > heldEventLength) && (!heldEventReturned);
                if (temp)
                    heldEventReturned = true;
                return temp;
            }
            if (buttonType == ButtonType.DOUBLE_CLICK)
                return doubleClickIndex == 2;
            return false;
        }

        /**
         * Returns an alternative trigger depending on button type.
         * - Normal: returns button released action.
         * - Hold: returns nothing
         * - Hold Time: returns nothing.
         * - Double Click: returns if the double click was killed off with only 1 button pressed (event).
         */
        public boolean getAlt() {
            if (buttonType == ButtonType.NORMAL)
                return released;
            if (buttonType == ButtonType.DOUBLE_CLICK)
                return doubleClickIndex == 1;
            return false;
        }

        /** Returns true if the button is down */
        private boolean isDown() {
            if (isConnected(port)) {
                if (slot > 0 && slot < 11)
                    return DriverStation.getInstance().getStickButton(port, (byte) slot);
                return DriverStation.getInstance().getStickPOV(port, 0) == slot;
            }
            return false;
        }

        //Slots
        public static final int
                A = 1,
                B = 2,
                X = 3,
                Y = 4,
                LEFT_BUMPER = 5,
                RIGHT_BUMPER = 6,
                MENU = 7,
                LIST = 8,
                LEFT_JOYSTICK_PRESS = 9,
                RIGHT_JOYSTICK_PRESS = 10,
                DIRECTION_PAD_UP_LEFT = 315,
                DIRECTION_PAD_UP = 0,
                DIRECTION_PAD_UP_RIGHT = 45,
                DIRECTION_PAD_RIGHT = 90,
                DIRECTION_PAD_DOWN_LEFT = 225,
                DIRECTION_PAD_DOWN = 180,
                DIRECTION_PAD_DOWN_RIGHT = 135,
                DIRECTION_PAD_LEFT = 270;
    }

    private static class DoubleButton extends Button {
        private final Button button1;
        private final Button button2;

        public DoubleButton(Button button1, Button button2) {
            super(0, 0, null, 0);
            this.button1 = button1;
            this.button2 = button2;
        }

        void update() { }

        /**
         * Ors the result of get() on both buttons
         * Returns true on event trigger (button pressed, held event, or double click)
         */
        public boolean get() {
            return button1.get() || button2.get();
        }

        /**
         * Ors the result of getAlt() on both buttons
         * Returns an alternative trigger depending on button type.
         * - Normal: returns button released action.
         * - Hold: returns nothing.
         * - Double Click: returns if the double click was killed off with only 1 button pressed (event).
         */
        public boolean getAlt() {
            return button1.getAlt() || button2.getAlt();
        }
    }

    //Axis'
    public static Axis getAxis(int slot, XboxController controller1, XboxController controller2) {
        return new DoubleAxis(
                controller1.getAxis(slot),
                controller2.getAxis(slot)
        );
    }

    public static Axis getAxis(int slot, Deadband deadband, XboxController controller1, XboxController controller2) {
        return new DoubleAxis(
                controller1.getAxis(slot, deadband),
                controller2.getAxis(slot, deadband)
        );
    }

    public static Axis getAxis(int slot, Deadband deadband, BezierCurve curve, XboxController controller1, XboxController controller2) {
        return new DoubleAxis(
                controller1.getAxis(slot, deadband, curve),
                controller2.getAxis(slot, deadband, curve)
        );
    }

    public static Axis getAxis(int slot, Deadband deadband, BezierCurve curve, boolean reversed, XboxController controller1, XboxController controller2) {
        return new DoubleAxis(
                controller1.getAxis(slot, deadband, curve, reversed),
                controller2.getAxis(slot, deadband, curve, reversed)
        );
    }

    public Axis getAxis(int slot) { return getAxis(slot, null); }

    public Axis getAxis(int slot, Deadband deadband) { return getAxis(slot, deadband, null); }

    public Axis getAxis(int slot, Deadband deadband, BezierCurve curve) { return getAxis(slot, deadband, curve, false); }

    public Axis getAxis(int slot, Deadband deadband, BezierCurve curve, boolean reversed) {
        axises.add(new Axis(port, slot, deadband, curve, reversed));
        return axises.get(axises.size() - 1);
    }

    public static class Axis {
        private final int port;
        private final int slot;
        public boolean reversed;
        public Deadband deadband;
        public BezierCurve curve;

        Axis(int port, int slot, Deadband deadband, BezierCurve curve, boolean reversed) {
            this.port = port;
            this.slot = slot;
            this.deadband = deadband == null ? new Deadband() : deadband;
            this.curve = curve;
            this.reversed = reversed;
        }

        //Reads
        public double get() {
            double val = deadband.get(getRawAxisValue()) * (reversed ? -1 : 1);
            if (curve != null)
                val = curve.getPoint(val);
            return val;
        }

        private double getRawAxisValue() {
            if (isConnected(port))
                return DriverStation.getInstance().getStickAxis(port, slot);
            return 0;
        }

        //Settings
        public void changeCurveX1(double x1) {
            if (curve != null)
                curve.x1 = x1;
        }

        //Slots
        public static final int
                LEFT_JOYSTICK_X = 0,
                LEFT_JOYSTICK_Y = 1,
                LEFT_TRIGGER = 2,
                RIGHT_TRIGGER = 3,
                RIGHT_JOYSTICK_X = 4,
                RIGHT_JOYSTICK_Y = 5;
    }

    public static class DoubleAxis extends Axis {
        private final Axis axis1;
        private final Axis axis2;

        public DoubleAxis(Axis axis1, Axis axis2) {
            super(0, 0, null, null, false);
            this.axis1 = axis1;
            this.axis2 = axis2;
        }

        public double get() {
            return axis1.get() + axis2.get();
        }
    }

    //Rumbles
    public Rumble getRumble(double strength, boolean hard, int timeoutMs) { return getRumble(strength, hard, timeoutMs, 0); }

    public Rumble getRumble(double strength, boolean hard, int timeoutMs, int dipCount) {
        return new Rumble(port, strength, hard, timeoutMs, dipCount);
    }

    public static class Rumble {
        private final int port;
        private final int timeoutMs;
        private final int dipCount;
        private final short strength;
        private final boolean hard;
        private long startTime;

        Rumble(int port, double strength, boolean hard, int timeoutMs, int dipCount) {
            this.port = port;
            this.strength = (short) ((strength < 0 ? 0 : (strength > 1 ? 1 : strength)) * 65535);
            this.hard = hard;
            this.timeoutMs = timeoutMs < 50 ? 50 : timeoutMs;
            this.dipCount = dipCount < 0 ? 0 : dipCount;
        }

        void update() {
            boolean on = true;
            double x = ((double) (System.currentTimeMillis() - startTime)) / timeoutMs;
            for (int i = 0; i < dipCount; i++) {
                if (x > (0.4 / dipCount) + ((double) i / dipCount) && x < (0.6 / dipCount) + ((double) i / dipCount))
                    on = false;
            }
            setRumble(on);

            if (System.currentTimeMillis() > startTime + timeoutMs) {
                for (XboxController controller : controllers)
                    if (controller.port == port) controller.activeRumble = null;
            }
        }

        private void setRumble(boolean on) {
            if (isConnected(port)) {
                if (on)
                    HAL.setJoystickOutputs((byte) port, 0, hard ? strength : 0, hard ? 0 : strength);
                else HAL.setJoystickOutputs((byte) port, 0, (short) 0, (short) 0);
            }
        }

        public void start() {
            startTime = System.currentTimeMillis();
            for (XboxController controller : controllers)
                if (controller.port == port) controller.activeRumble = this;
        }
    }

    //Bezier Curves

    /**
     * Processes bezier curves between two points.
     * Desmos Link: https://www.desmos.com/calculator/da8zwxpgzo
     */
    public static class BezierCurve {
        public double x1, y1, x2, y2;

        public BezierCurve(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        /** Get value of curve at point x. Flips the curve for negative values. */
        public double getPoint(double x) {
            boolean tn = x < 0;
            x = Math.abs(x);
            double x0 = 0.0;
            double y0 = 0.0;
            double x3 = 1.0;
            double y3 = 1.0;
            x = (1 - x);
            x = 1 - (((1 - x) * p(x, x0, x1, x2)) + (x * p(x, x1, x2, x3)));
            x = (((1 - x) * p(x, y0, y1, y2)) + (x * p(x, y1, y2, y3)));
            return x * (tn ? -1 : 1);
        }

        private double p(double x, double a, double b, double c) {
            return ((1 - x) * ((1 - x) * a + (x * b))) + (x * ((1 - x) * b + (x * c)));
        }
    }

    //Deadband

    /**
     * A deadband cuts out areas of an input.
     * For example in a clipping deadband with a radius of .05, .05 would go to 0 and .06 would not change.
     * This class has two deadbands.
     * - Clipping: in which areas will be directly cut out (r=.05: .05->0, .06->.06)
     * - Slope Adjustment: in which the slope is adjusted (r=.05: .05->0, 0.06->0.01)
     * Desmos Link: https://www.desmos.com/calculator/xhbilptzt9
     */
    public static class Deadband {
        //Deadband Types

        /**
         * Clipping: in which areas will be directly cut out (r=.05: .05->0, .06->.06)
         * Slope Adjustment: in which the slope is adjusted (r=.05: .05->0, 0.06->0.01)
         */
        public enum DeadbandType {
            CLIPPING,
            SLOPE_ADJ,
            NONE
        }

        //Main Getter Function

        /**
         * Processes a deadband upon "x" at "radius" distance.
         *
         * @param x      The input value
         * @param radius The size of the deadband
         * @param type   The type of deadband (clipping or slope adj)
         */
        public static double get(double x, double radius, DeadbandType type) {
            //Call function for specified deadband and send it abs(x), then undo the abs(x)
            if (type == DeadbandType.CLIPPING)
                return getClipping(Math.abs(x), radius) * (x > 0 ? 1 : -1);
            if (type == DeadbandType.SLOPE_ADJ)
                return getSlopeAdjustment(Math.abs(x), radius) * (x > 0 ? 1 : -1);
            else return x;
        }

        //Deadband Processors
        private static double getSlopeAdjustment(double x, double radius) {
            double m = 1 / (1 - radius);
            double b = -m * radius;
            if (x <= radius)
                return 0;
            else
                return m * x + b;
        }

        private static double getClipping(double x, double radius) {
            if (x <= radius)
                return 0;
            else
                return x;
        }

        //Saved Deadband
        double savedRadius;
        DeadbandType savedType;

        public Deadband() { this(0, DeadbandType.NONE); }

        public Deadband(double radius) { this(radius, DeadbandType.SLOPE_ADJ); }

        public Deadband(double radius, DeadbandType type) {
            this.savedRadius = radius;
            this.savedType = type;
        }

        public double get(double x) {
            return get(x, this.savedRadius, this.savedType);
        }
    }
}