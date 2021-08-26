package main;

import examples.ExampleOptionsOp;
import examples.GameChangersOptionOp;
import external.util.Cell;
import external.opmode.HardwarelessAbstractOp;
import external.webinterface.WebInterface;
import fi.iki.elonen.NanoHTTPD;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class Main {

    static final HardwarelessAbstractOp opMode = new GameChangersOptionOp("options-test");
    static volatile Cell<Boolean> connected = new Cell<>(false);
    static Thread opThread;

    public static void main(String[] args) throws InterruptedException {

        try {
            NanoHTTPD webserver = new WebInterface(28080, connected);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Desktop.getDesktop().browse(URI.create("http://localhost:28080"));
        } catch (IOException e) {
            System.out.println("Please open http://localhost:28080 in your browser");
        }

        opThread = new Thread(() -> {

            opMode.gamepad1 = WebInterface.vgp;
            opMode.init();
            while (!connected.get()) {
                // WARNING: This method is not guaranteed to loop to completion (in fact, it probably won't run at all)
                opMode.init_loop();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            opMode.start();
            while (connected.get()) {
                opMode.loop();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            opMode.stop();

        });

        opThread.start();

        opThread.join(); // wait for run to completion
        System.out.println("OpMode completed successfully, have a nice day");
        System.exit(0); // to join the webserver worker as well

    }
}