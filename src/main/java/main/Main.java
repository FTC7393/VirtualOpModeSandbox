package main;

import external.gamepad.VirtualGamepad;
import external.opmode.AbstractOptionsOpMode;
import external.opmode.HardwarelessAbstractOp;
import external.util.OptionEntries;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Main extends Application {

    HardwarelessAbstractOp opMode = new OptionsOp();
    Thread opThread;
    volatile boolean sigkill = false;

    public void start(Stage stage) throws Exception {

        // CAUTION!!! This variable is referenced in opmode worker thread. It must not be nulled.
        VirtualGamepad vgp = new VirtualGamepad();

        Parent root = new Label("Focus on this window to start input to gamepad");
        stage.setTitle("win0");
        Scene s = new Scene(root, 400, 40);
        s.setOnKeyPressed((ev) -> vgp.keyHandler(ev, true));
        s.setOnKeyReleased((ev) -> vgp.keyHandler(ev, false));

        stage.setScene(s);
        stage.show();

        // Operation runner
        opThread = new Thread(() -> {

            opMode.gamepad1 = vgp;
            opMode.init();
            // skip init_loop for now
            opMode.start();
            while (!sigkill) {
                opMode.loop();
            }
            opMode.stop();

        });

        opThread.start();

    }

    @Override
    public void stop() throws InterruptedException {
        sigkill = true;
        opThread.join();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class OptionsOp extends AbstractOptionsOpMode {

    public OptionsOp() {
        super("options-test", Options.class);
    }

}

enum Options implements OptionEntries {

    LIKE(TypeData
            .integerType(1, 0, 10)
            .withFallback(99)
    ),
    SUBSCRIBE(TypeData
            .booleanType()
            .withFallback(false)
    ),
    TESTENUM(TypeData
            .enumType(ForkOptions.class)
            .withFallback(ForkOptions.BRUH1)),
    ;

    TypeData<?> data;

    Options(TypeData<?> data) {
        this.data = data;
    }

    @Override
    public TypeData<?> getData() {
        return data;
    }
}

enum ForkOptions {
    BRUH1,
    BRUH2,
    BRUH3,
    ;
}