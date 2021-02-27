/*BreakerBots Robotics Team 2019*/
package com.team5104.lib.controller;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.team5104.lib.console;
import com.team5104.lib.controller.Button.ButtonType;
import edu.wpi.first.hal.HAL;

import java.util.ArrayList;
import java.util.List;

public class XboxController {
    private static final List<XboxController> controllers = new ArrayList<XboxController>();
    public final int port;
    private final List<Button> buttons = new ArrayList<Button>();
    private final List<Axis> axises = new ArrayList<Axis>();
    private Rumble activeRumble = null;
    private boolean hasSentDisconnectMessage = false;

    //Contructors
    public static XboxController create(int port) {
        for (XboxController controller : controllers) {
            if (controller.port == port) {
                console.error("controller on port " + port + " already declared!");
                return null;
            }
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
                    console.log("Controller on port " + controller.port + " reconnected!");
                    controller.hasSentDisconnectMessage = false;
                }
            }
            else if (!controller.hasSentDisconnectMessage) {
                console.warn("Controller on port " + controller.port + " disconnected!");
                controller.hasSentDisconnectMessage = true;
            }
        }
    }

    //Is Connected
    @JsonGetter("connected")
    public boolean isConnected() { return isConnected(port); }

    public static boolean isConnected(int port) {
        return HAL.getJoystickIsXbox((byte) port) == 1;
    }

    //Buttons
    public Button button(Button.Slot slot) {
        buttons.add(new Button(port, slot, ButtonType.NORMAL, 0));
        return buttons.get(buttons.size() - 1);
    }
    public Button holdButton(Button.Slot slot) {
        buttons.add(new Button(port, slot, ButtonType.HOLD, 0));
        return buttons.get(buttons.size() - 1);
    }
    public Button holdTimeButton(Button.Slot slot, int holdTime) {
        buttons.add(new Button(port, slot, ButtonType.HOLD_TIME, holdTime));
        return buttons.get(buttons.size() - 1);
    }
    public Button doubleClickButton(Button.Slot slot, int killOffTime) {
        buttons.add(new Button(port, slot, ButtonType.DOUBLE_CLICK, killOffTime));
        return buttons.get(buttons.size() - 1);
    }

    //Axis'
    public Axis axis(Axis.Slot slot) { return axis(slot, null); }
    public Axis axis(Axis.Slot slot, Deadband deadband) { return axis(slot, deadband, null); }
    public Axis axis(Axis.Slot slot, Deadband deadband, BezierCurve curve) { return axis(slot, deadband, curve, false); }
    public Axis axis(Axis.Slot slot, Deadband deadband, BezierCurve curve, boolean reversed) {
        axises.add(new Axis(port, slot, deadband, curve, reversed));
        return axises.get(axises.size() - 1);
    }

    //Rumbles
    public Rumble rumble(double strength, boolean hard, int timeoutMs) { return rumble(strength, hard, timeoutMs, 0); }
    public Rumble rumble(double strength, boolean hard, int timeoutMs, int dipCount) {
        return new Rumble(port, strength, hard, timeoutMs, dipCount);
    }
    public void setActiveRumble(Rumble rumble) {
        this.activeRumble = rumble;
    }

    //Other
    @JsonGetter("port")
    public int getPort() {
        return port;
    }
    public static List<XboxController> getControllers() {
        return controllers;
    }
}
