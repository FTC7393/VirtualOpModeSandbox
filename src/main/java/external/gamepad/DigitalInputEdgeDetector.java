package external.gamepad;

import external.util.InputExtractor;

/**
 * This file was made by the electronVolts, FTC team 7393
 * <p>
 * A class that is very useful for joystick control. It does edge detection for
 * a digital input.
 */
public class DigitalInputEdgeDetector implements InputExtractor<Boolean> {
    private Boolean currentValue = null;
    private Boolean previousValue = null;
    private final InputExtractor<Boolean> extractor;

    /**
     * @param extractor the InputExtractor to do edge detection on
     */
    public DigitalInputEdgeDetector(InputExtractor<Boolean> extractor) {
        this.extractor = extractor;
    }

    /**
     * update the current and previous value of the input
     *
     * @return the current value of the input
     */
    public boolean update() {
        // if this is the first call to update()
        if (currentValue == null) {
            // set currentValue and previousValue to the reading so no edges are
            // triggered
            currentValue = extractor.getValue();
            previousValue = currentValue;
        } else {
            previousValue = currentValue;
            currentValue = extractor.getValue();
        }
        return currentValue;
    }

    /**
     * @return whether or not the input is true right now
     */
    @Override
    public Boolean getValue() {
        return currentValue;
    }

    /**
     * @return whether or not the input is true right now
     */
    public boolean isPressed() {
        return currentValue;
    }

    /**
     * @return if the input just turned from false to true
     */
    public boolean justPressed() {
        return currentValue && !previousValue;
    }

    /**
     * @return if the input just turned from true to false
     */
    public boolean justReleased() {
        return !currentValue && previousValue;
    }
}