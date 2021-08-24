package external.gamepad;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Map;

public class VirtualGamepad {

    boolean a, b, x, y, left_bumper, right_bumper,
            dpad_up, dpad_down, dpad_left, dpad_right,
            left_stick_button, right_stick_button, back, start;

    public void keyHandler(KeyEvent ev, boolean isDownEvent) {
        // ugly switch statement
        // see rust Entry for salvation
        // though javalang devs are so obstinate
        switch (ev.getCode()) {
            case UP:
                dpad_up = isDownEvent;
                break;
            case DOWN:
                dpad_down = isDownEvent;
                break;
            case LEFT:
                dpad_left = isDownEvent;
                break;
            case RIGHT:
                dpad_right = isDownEvent;
                break;
            case W:
                a = isDownEvent;
                break;
            case D:
                b = isDownEvent;
                break;
            case S:
                y = isDownEvent;
                break;
            case A:
                x = isDownEvent;
                break;
            // now I must wallow in the fact that javafx doesn't let you tell between l/r shift or l/r ctrl
            case Z:
                left_bumper = isDownEvent;
                break;
            case M:
                right_bumper = isDownEvent;
                break;
        }
    }

}
