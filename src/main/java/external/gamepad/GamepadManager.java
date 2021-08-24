package external.gamepad;

import java.util.ArrayList;
import java.util.List;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 1/9/16
 * <p>
 * This class wraps a gamepad and adds:
 * Edge detection to the digital inputs (buttons and dpad) {@see DigitalInputEdgeDetector}
 * Scaling to the analog inputs (joysticks and triggers) {@see AnalogInputScaler}
 */
public class GamepadManager {
    //this stores all the wrapped digital inputs
    public final DigitalInputEdgeDetector a, b, x, y, left_bumper, right_bumper,
            dpad_up, dpad_down, dpad_left, dpad_right,
            left_stick_button, right_stick_button, back, start;

    public final DigitalInputEdgeDetector[] digitalInputEdgeDetectors;

    //this stores all the wrapped analog inputs
//    public final AnalogInputScaler left_stick_x, left_stick_y, right_stick_x, right_stick_y,
//            left_trigger, right_trigger;

    public GamepadManager(VirtualGamepad gamepad) {
//        this(gamepad, Functions.none());
//    }

//    public GamepadManager(Gamepad gamepad, Function scalingFunction) {
//        this(gamepad, scalingFunction, InitButton.NONE);
//    }

        //use this constructor for custom joystick scaling
//    public GamepadManager(Gamepad gamepad, Function scalingFunction, InitButton initButton) {

//        DigitalInputEdgeDetector rawStart = new DigitalInputEdgeDetector(() -> gamepad.start);
//        detectors.add(rawStart);
//        if (initButton == InitButton.A) {
//            DigitalInputEdgeDetector rawA = new DigitalInputEdgeDetector(() -> gamepad.a);
//            detectors.add(rawA);
//            at = new ButtonAlivenessTester(rawA, rawStart);
//        } else if (initButton == InitButton.B) {
//            DigitalInputEdgeDetector rawB = new DigitalInputEdgeDetector(() -> gamepad.b);
//            detectors.add(rawB);
//            at = new ButtonAlivenessTester(rawB, rawStart);
//        } else if (initButton == InitButton.NONE) {
//            at = AlivenessTester.ALWAYS;
//        } else {
//            throw new RuntimeException("Unknown option for init button " + initButton.name());
//        }

        //create all the DigitalInputEdgeDetector objects
        a = new DigitalInputEdgeDetector(() -> gamepad.a);
        b = new DigitalInputEdgeDetector(() -> gamepad.b);
        x = new DigitalInputEdgeDetector(() -> gamepad.x);
        y = new DigitalInputEdgeDetector(() -> gamepad.y);
        left_bumper = new DigitalInputEdgeDetector(() -> gamepad.left_bumper);
        right_bumper = new DigitalInputEdgeDetector(() -> gamepad.right_bumper);
        dpad_up = new DigitalInputEdgeDetector(() -> gamepad.dpad_up);
        dpad_down = new DigitalInputEdgeDetector(() -> gamepad.dpad_down);
        dpad_left = new DigitalInputEdgeDetector(() -> gamepad.dpad_left);
        dpad_right = new DigitalInputEdgeDetector(() -> gamepad.dpad_right);
        left_stick_button = new DigitalInputEdgeDetector(() -> gamepad.left_stick_button);
        right_stick_button = new DigitalInputEdgeDetector(() -> gamepad.right_stick_button);
        back = new DigitalInputEdgeDetector(() -> gamepad.back);
        start = new DigitalInputEdgeDetector(() -> gamepad.start);

        //create all the AnalogInputScaler objects
//        left_stick_x = new AnalogInputScaler(() -> gamepad.left_stick_x, scalingFunction);
//        left_stick_y = new AnalogInputScaler(() -> gamepad.left_stick_y, scalingFunction);
//        right_stick_x = new AnalogInputScaler(() -> gamepad.right_stick_x, scalingFunction);
//        right_stick_y = new AnalogInputScaler(() -> gamepad.right_stick_y, scalingFunction);
//        left_trigger = new AnalogInputScaler(() -> gamepad.left_trigger, scalingFunction);
//        right_trigger = new AnalogInputScaler(() -> gamepad.right_trigger, scalingFunction);

        digitalInputEdgeDetectors = new DigitalInputEdgeDetector[]{
                a, b, x, y, left_bumper, right_bumper,
                dpad_up, dpad_down, dpad_left, dpad_right,
                left_stick_button, right_stick_button, back, start,
        };
    }

    public boolean justTriggered() {
        for (DigitalInputEdgeDetector i : digitalInputEdgeDetectors) {
            if (i.justPressed()) {
                return true;
            }
        }
        return false;
    }

    public void update() {
        //update all the values

        a.update();
        b.update();
        x.update();
        y.update();

        left_bumper.update();
        right_bumper.update();

//        left_trigger.update();
//        right_trigger.update();

        dpad_up.update();
        dpad_down.update();
        dpad_left.update();
        dpad_right.update();

        left_stick_button.update();
        right_stick_button.update();

        back.update();
        start.update();

//        left_stick_x.update();
//        left_stick_y.update();

//        right_stick_x.update();
//        right_stick_y.update();


    }
}
