package main;

import external.webinterface.VirtualGamepad;
import external.opmode.AbstractOptionsOpMode;
import external.opmode.HardwarelessAbstractOp;
import external.util.OptionEntries;
import external.webinterface.WebInterface;
import fi.iki.elonen.NanoHTTPD;

public class Main {

    HardwarelessAbstractOp opMode = new OptionsOp();
    Thread opThread;
    volatile boolean sigkill = false;

    public void start() {

        // CAUTION!!! This variable is referenced in opmode worker thread. It must not be nulled.
        VirtualGamepad vgp = new VirtualGamepad();

//        s.setOnKeyPressed((ev) -> vgp.keyHandler(ev, true));
//        s.setOnKeyReleased((ev) -> vgp.keyHandler(ev, false));

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

    public void stop() throws InterruptedException {
        sigkill = true;
        opThread.join();
    }

    public static void main(String[] args) {
//        launch(args);

        try {
            NanoHTTPD webserver = new WebInterface(8080);
        } catch (Exception e) {
            System.out.println(e);
        }

//        new Main().start();
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