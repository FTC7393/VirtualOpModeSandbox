package external.webinterface;

public class VirtualGamepad {

    public boolean a, b, x, y, left_bumper, right_bumper,
            dpad_up, dpad_down, dpad_left, dpad_right,
            left_stick_button, right_stick_button, back, start;

    public void keyHandler(String ev, boolean isDownEvent) {
        // TODO: Replace these all with cells
        // ugly switch statement
        // see rust Entry for salvation
        // though javalang devs are so obstinate
        switch (ev) {
            case "arrowup":
                dpad_up = isDownEvent;
                break;
            case "arrowdown":
                dpad_down = isDownEvent;
                break;
            case "arrowleft":
                dpad_left = isDownEvent;
                break;
            case "arrowright":
                dpad_right = isDownEvent;
                break;
            case "w":
                a = isDownEvent;
                break;
            case "d":
                b = isDownEvent;
                break;
            case "s":
                y = isDownEvent;
                break;
            case "a":
                x = isDownEvent;
                break;
            // now I must wallow in the fact that javafx doesn't let you tell between l/r shift or l/r ctrl
            case "z":
                left_bumper = isDownEvent;
                break;
            case "/":
                right_bumper = isDownEvent;
                break;
        }
    }

}
