package external.opmode;

import external.webinterface.VirtualGamepad;
import external.util.Logger;

import java.io.File;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 *
 * adds useful features to the OpMode such as a MatchTimer, servo management, use of RobotCfg
 */
public abstract class HardwarelessAbstractOp {

    private Logger logger;
    public VirtualGamepad gamepad1;

    /**
     * This is implemented by the opmode to log values
     *
     * @return a Logger that has been configured return null for no logging
     */
    protected abstract Logger createLogger();

    /**
     * This is implemented by the opmode
     * It is called when the init button is pressed on the driver station
     */
    protected abstract void setup();

    /**
     * This is implemented by the opmode
     * It is called continuously between the setup() and go() methods
     */
    protected abstract void setup_act();

    /**
     * This is implemented by the opmode
     * It is called when the start button on the driver station is pressed
     */
    protected abstract void go();

    /**
     * This is implemented by AbstractTeleOp to update the joysticks
     * It is called right before the act() method
     *
     * @see AbstractTeleOp
     */
    protected abstract void pre_act();

    /**
     * This is implemented by the opmode
     * It is called between the go() and stop() methods
     */
    protected abstract void act();

    /**
     * This is implemented by AbstractAutoOp to run the StateMachine
     * It is called right after the act() method
     */
    protected abstract void post_act();

    /**
     * This is implemented by the opmode
     * It is called when the stop button is pressed on the driver station
     */
    protected abstract void end();

    public void init() {

        logger = createLogger(); //create the logger
        setup();

    }

    public void init_loop() {
        setup_act();
        //servos.act();
    }

    public void start() {

        File log_dir = new File("FTC/log/");
        log_dir.mkdirs();

        if (logger != null) logger.start(log_dir); // start the logging
        go();

    }

    public void loop() {

        pre_act();

        if (logger != null) logger.act();

        act();
        post_act();

    }

    public void stop() {
        if (logger != null) logger.stop();

        end();
    }

}
