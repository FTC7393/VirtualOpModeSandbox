package main;

import external.util.Cell;
import external.webinterface.VirtualGamepad;
import external.opmode.AbstractOptionsOpMode;
import external.opmode.HardwarelessAbstractOp;
import external.util.OptionEntries;
import external.webinterface.WebInterface;
import fi.iki.elonen.NanoHTTPD;

public class Main {

    static final HardwarelessAbstractOp opMode = new OptionsOp();
    static volatile Cell<Boolean> sigkill = new Cell<>(false);
    static Thread opThread;

    public void start() {

        // CAUTION!!! This variable is referenced in opmode worker thread. It must not be nulled.

        // Operation runner

        opThread.start();

    }

    public void stop() throws InterruptedException {
        opThread.join();
    }

    public static void main(String[] args) {

        try {
            NanoHTTPD webserver = new WebInterface(8080);
        } catch (Exception e) {
            System.out.println(e);
        }

        opThread = new Thread(() -> {

            opMode.gamepad1 = WebInterface.vgp;
            opMode.init();
            // skip init_loop for now
            opMode.start();
            while (!sigkill.get()) {
                opMode.loop();
            }
            opMode.stop();

        });

        opThread.start();

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