package external.webprint;

import java.io.OutputStream;
import java.io.PrintStream;

public class WebPrint {

    public static final PrintStream out = new WebPrintStream(System.out);

}

class WebPrintStream extends PrintStream {

    // TODO: Actually forward the input to websocket
    public WebPrintStream(OutputStream out) {
        super(out);
    }
}
