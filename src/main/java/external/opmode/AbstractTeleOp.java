package external.opmode;

import external.gamepad.GamepadManager;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 *
 * extends AbstractOp and adds gamepad edge detection and scaling, and a 2 minute timer
 *
// * @see ftc.evlib.opmodes.AbstractOp
 * @see GamepadManager
 */
public abstract class AbstractTeleOp extends HardwarelessAbstractOp {
    public GamepadManager driver1;

//    /**
//     * This is implemented by the teleop opmode
//     *
//     * @return a Function to scale the joysticks by
//     */
//    protected abstract Function getJoystickScalingFunction();

    @Override
    public void init() {
        driver1 = new GamepadManager(gamepad1);
        super.init();
    }

    @Override
    public void pre_act() {
        driver1.update();
    }

    @Override
    public void post_act() {

    }
}
